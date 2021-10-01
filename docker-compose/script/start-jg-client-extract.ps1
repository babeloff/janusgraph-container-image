
$projectdir = "C:/Users/fred/IdeaProjects/pulsar-devops"
$JG_SCRIPT_DIR="$projectdir/graph_query/groovy"
$JG_CORPUS_DIR="$projectdir/trial/src/test/resources"

# tag::extraction[]
docker run --rm `
  --name janusgraph_client `
  --network jg-network `
  --mount type=bind,source=$JG_SCRIPT_DIR,target=/opt/janusgraph/scripts `
  --mount type=bind,source=$JG_CORPUS_DIR,target=/opt/janusgraph/corpus `
  -e GREMLIN_REMOTE_HOSTS=jce-jg `
  -it janusgraph/janusgraph:0.6.0 `
  ./bin/gremlin.sh
# end::extraction[]


