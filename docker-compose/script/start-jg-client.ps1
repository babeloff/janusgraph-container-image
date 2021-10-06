

# tag::compose-run[]
Set-Location ./docker/janusgraph-client/
docker compose run jg-client
# end::compose-run[]

$JG_SCRIPT_DIR="$(Get-Location)/query/groovy"
mkdir $JG_SCRIPT_DIR

# tag::run[]
docker run --rm `
  --name jg-client `
  --network jg-network `
  --mount type=bind,source=${JG_SCRIPT_DIR},target=/opt/janusgraph/scripts,readonly `
  --env GREMLIN_REMOTE_HOSTS=jce-jg `
  --interactive --tty `
  janusgraph/janusgraph:2021.10.6 `
  ./bin/gremlin.sh
# end::run[]
