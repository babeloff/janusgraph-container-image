
$JG_SCRIPT_DIR="$(Get-Location)/query/groovy"
mkdir $JG_SCRIPT_DIR

# tag::initialize[]
docker run --rm `
  --name jg_client_init `
  --network jg-network `
  --mount type=bind,target=/opt/janusgraph/scripts,source=$JG_SCRIPT_DIR,readonly `
  --env GREMLIN_REMOTE_HOSTS=jce-jg `
  janusgraph/janusgraph:2021.10.6 `
  ./bin/gremlin.sh `
    --verbose `
    --execute /opt/janusgraph/scripts/initial_load.groovy
# end::initialize[]
