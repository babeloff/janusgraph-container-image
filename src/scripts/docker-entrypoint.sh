#!/bin/bash

# Copyright 2019 JanusGraph Authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

GREMLIN_YAML="${JANUS_CONFIG_DIR}/gremlin-server.yaml"
JANUS_YAML="${JANUS_CONFIG_DIR}/janusgraph.yaml"

if [ "$1" == 'janusgraph' ]
then
  if [ "$(id -u)" == "0" ]
  then
    echo 'starting entry point as root; stepping down to run as "janusgraph" user'
    mkdir -p "${JANUS_DATA_DIR}" "${JANUS_CONFIG_DIR}"
    chown -R janusgraph:janusgraph "${JANUS_DATA_DIR}" "${JANUS_CONFIG_DIR}"
    chmod 700 "${JANUS_DATA_DIR}" "${JANUS_CONFIG_DIR}"

    exec chroot --skip-chdir --userspec janusgraph:janusgraph / "${BASH_SOURCE[0]}" "$@"
  fi
fi

echo 'running as non-root user ' "$(id -un)"
if [ "$1" == 'janusgraph' ]
then
  mkdir -p "${JANUS_DATA_DIR}" "${JANUS_CONFIG_DIR}"

  GREMLIN_YAML_SRC="conf/gremlin-server/${GREMLIN_SERVER_TEMPLATE:-gremlin-server}.yaml"
  if cp "${GREMLIN_YAML_SRC}" "${GREMLIN_YAML}"
  then
    echo 'copied ' "${GREMLIN_YAML_SRC}"
  else
    echo 'failed to copy ' "${GREMLIN_YAML_SRC}"
    ls "conf/gremlin-server/"
  fi

  JANUS_YAML_SRC="conf/gremlin-server/${JANUSGRAPH_TEMPLATE:-janusgraph}.yaml"
  if cp "${JANUS_YAML_SRC}" "${JANUS_YAML}"
  then
    echo 'copied ' "${JANUS_YAML_SRC}"
  else
    echo 'failed to copy ' "${JANUS_YAML_SRC}"
    ls "conf/gremlin-server/"
  fi

  chown -R "$(id -u):$(id -g)" "${JANUS_DATA_DIR}" "${JANUS_CONFIG_DIR}"
  chmod 700 "${JANUS_DATA_DIR}" "${JANUS_CONFIG_DIR}"
  chmod -R 600 "${JANUS_CONFIG_DIR}"/*

  echo 'apply configuration from environment'
  while IFS='=' read -r ENV_KEY
  do
    ENV_VAL="${!ENV_KEY}"

    # GREMLIN__*
    if [[ "${EVAL_END}" =~ GREMLIN__([[:alnum:]_]+) ]]
    then
      EVAL_END=${BASH_REMATCH[1]}
      EVAL_KEY=${ENV_VAL}
      echo "update gremlin server '$EVAL_END' with '${ENV_VAL}'"
      yq eval "${ENV_VAL}" "${GREMLIN_YAML}" --prettyPrint --inplace

    # JANUSGRAPH__*
    elif [[ "${EVAL_END}" =~ JANUSGRAPH__([[:alnum:]]+)_([[:graph:]]+) ]] && [[ -n ${env_var_val} ]]
    then
      EVAL_END=${BASH_REMATCH[1]}
      EVAL_KEY=${BASH_REMATCH[2]}

      JANUS_CFG_TGT="${JANUS_CONFIG_DIR}/janusgraph_${EVAL_END:-default}}.yaml"
      JANUS_PROPS_TGT="${JANUS_CONFIG_DIR}/janusgraph_${EVAL_END:-default}}.properties"
      if -z "${JANUS_CFG_TGT}"
      then
        cp "${JANUS_YAML}" "${JANUS_CFG_TGT}"
      fi
      echo "update graph back-end '$EVAL_END' and property eval '$ENV_VAL'"
      yq eval "${ENV_VAL}" "${JANUS_CFG_TGT}" --prettyPrint --inplace
      yq eval --outputformat=props "${JANUS_CFG_TGT}" > "${JANUS_PROPS_TGT}"

    # GREMLIN_GROOVY__*
    elif [[ "${EVAL_END}" =~ GREMLIN_GROOVY__([[:alnum:]_]+) ]]
    then
      EVAL_END=${BASH_REMATCH[1]}
      echo 'define gremlin script ' "$EVAL_END" ' with ' "${env_var_val}"
      echo "${env_var_val}" > "${JANUS_CONFIG_DIR}/${EVAL_END}.groovy"

    # other environment parameters that we are not concerned about
    else
      continue
    fi
  done < <(compgen -A variable | sort --ignore-nonprinting)

  show_array=($JG_SHOW)
  for (( ix=0; ix<${#show_array[@]}; ix++ ))
  do
    ITEM="${show_array[$ix]}"
    case "$ITEM" in
     env | environment)
      echo '== ENVIRONMENT =================================='
      env
      ;;
     server)
      echo '== GREMLIN SERVER ==============================='
      yq e -P '... comments=""' "${JANUS_CONFIG_DIR}/gremlin-server.yaml"
      ;;
     graph | graphs)
      find "${JANUS_CONFIG_DIR}" -type f -name '*.properties' | while read -r configFile
      do
        echo '== PROPERTIES ==================================='
        echo "- file: $configFile "
        echo '-----------------------------------------------'
        sed '/^[[:space:]]*#[^!]/d
             /#$/d
             /^[[:space:]]*$/d' "$configFile"
      done
      ;;
     script | scripts | groovy)
      find "${JANUS_CONFIG_DIR}" -type f -name '*.groovy' | while read -r configFile
      do
        echo '== GROOVY SCRIPTS ================================'
        echo "- file: $configFile "
        echo '-----------------------------------------------'
        cat "$configFile"
      done
      ;;
     *)
       echo "== UNKNOWN ITEM ${ITEM} ========================"
      ;;
    esac
  done

  case "${JG_ACTION:-run}" in
   run)
    echo '==================================================='
    echo 'running and awaiting storage <' "${JANUS_STORAGE_TIMEOUT}"
    if [ -n "${JANUS_STORAGE_TIMEOUT:-}" ]
    then
      yq eval '.graphs' "${GREMLIN_YAML}" | while IFS=: read -r JANUS_GRAPH_NAME JANUS_PROPS_FILE
      do
        F="$(mktemp --suffix .groovy)"
        echo 'graph = JanusGraphFactory.open(' "${JANUS_PROPS_FILE}" ')' > "$F"
        echo 'waiting for graph database : ' "${JANUS_GRAPH_NAME}"
        timeout "${JANUS_STORAGE_TIMEOUT}s" bash -c \
          "until bin/gremlin.sh -e \"$F\" > /dev/null 2>&1; do echo \"waiting for storage: \"${JANUS_GRAPH_NAME}\"...\"; sleep 5; done"
        rm -f "$F"
      done
    else
      sleep 60
    fi
    /usr/local/bin/load-init-db.sh &
    exec "${JANUS_HOME}/bin/gremlin-server.sh" "${GREMLIN_YAML}"
    ;;
   *)
    echo "action unknown ${JG_ACTION} ; fail"
    ;;
  esac
fi

if [ -n "${GREMLIN_REMOTE_HOSTS:-}" ]
then
  echo 'override hosts for remote connections with Gremlin Console'
  sed -i "s/hosts\s*:.*/hosts: [$GREMLIN_REMOTE_HOSTS]/" "${JANUS_HOME}/conf/remote.yaml"
fi

echo 'executing ' "$@"
exec "$@"
