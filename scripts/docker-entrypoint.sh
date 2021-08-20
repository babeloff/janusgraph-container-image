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
JANUS_PROPS="${JANUS_CONFIG_DIR}/janusgraph.properties"

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

  JANUS_PROPS_SRC="conf/gremlin-server/${JANUS_PROPS_TEMPLATE:-janusgraph-server}.properties"
  if cp "${JANUS_PROPS_SRC}" "${JANUS_PROPS}"
  then
    echo 'copied ' "${JANUS_PROPS_SRC}"
  else
    echo 'failed to copy ' "${JANUS_PROPS_SRC}"
    ls "conf/gremlin-server/"
  fi

  chown -R "$(id -u):$(id -g)" "${JANUS_DATA_DIR}" "${JANUS_CONFIG_DIR}"
  chmod 700 "${JANUS_DATA_DIR}" "${JANUS_CONFIG_DIR}"
  chmod -R 600 "${JANUS_CONFIG_DIR}"/*

  echo 'apply configuration from environment'
  while IFS='=' read -r env_var_key
  do
    env_var_val="${!env_var_key}"

    # GREMLIN_SERVER__*
    if [[ "${env_var_key}" =~ GREMLIN_SERVER__([[:alnum:]_]+) ]]
    then
      env_var_key=${BASH_REMATCH[1]}
      echo 'update gremlin server ' "$env_var_key" ' with ' "${env_var_val}"
      yq eval "${env_var_val}" "${GREMLIN_YAML}" --prettyPrint --inplace

    # JANUS_PROPS__*
    elif [[ "${env_var_key}" =~ JANUS_PROPS__([[:alnum:]]+)_([[:graph:]]+) ]] && [[ -n ${env_var_val} ]]
    then
      env_graph=${BASH_REMATCH[1]}
      env_var_name=${BASH_REMATCH[2]}

      echo 'update graph name ' "$env_graph" ', and property key ' "$env_var_name"
      janus_props_graph="${JANUS_CONFIG_DIR}/janusgraph-${env_graph}.properties"
      if ! [[ -f "${janus_props_graph}" ]]
      then
        cp "${JANUS_PROPS}" "${janus_props_graph}"
      fi
      if  [[ ${env_var_val} =~ ^([[:graph:]]+)[[:space:]]*[=][[:space:]]*(.+)$ ]]
      then
        env_prop_key=${BASH_REMATCH[1]}
        env_prop_val=${BASH_REMATCH[2]}
        if grep -q -E "^\s*${env_prop_key}\s*=\.*" "${JANUS_PROPS}"
        then
          echo 'update janusgraph property ' "$env_prop_key" ' -> ' "$env_prop_val"
          sed --regexp-extended --in-place "s#^(\s*${env_prop_key}\s*=).*#\\1${env_prop_val}#" "${janus_props_graph}"
        else
          echo 'append janusgraph property ' "$env_prop_key" ' -> ' "$env_prop_val"
          echo "${env_prop_key}=${env_prop_val}" >> "${janus_props_graph}"
        fi
      fi

    # GREMLIN_GROOVY__*
    elif [[ "${env_var_key}" =~ GREMLIN_GROOVY__([[:alnum:]_]+) ]]
    then
      env_var_key=${BASH_REMATCH[1]}
      echo 'define gremlin script ' "$env_var_key" ' with ' "${env_var_val}"
      echo "${env_var_val}" > "${JANUS_CONFIG_DIR}/${env_var_key}.groovy"

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
