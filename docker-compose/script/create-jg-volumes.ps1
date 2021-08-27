
# tag::server-volumes[]
docker volume create --driver local --name=jg-corpus-data
docker volume create --driver local --name=jg-product-data
docker volume create --driver local --name=jg-cql-data
docker volume create --driver local --name=jg-es-data
# end::server-volumes[]


# tag::client-volumes[]
docker volume create --driver local --name=jg-scripts
# end::client-volumes[]
