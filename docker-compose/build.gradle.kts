plugins {
    id("annex-docker-plugin")
}

dockerVolumes {
    create("JgCorpusData") {
        title.set("jg-corpus-data")
    }
    create("JgProductData") {
        title.set("jg-product-data")
    }
    create("JgCqlData") {
        title.set("jg-cql-data")
    }
    create("JgEsData") {
        title.set("jg-es-data")
    }
    create("JgScript") {
        title.set("jg-scripts")
    }
}

dockerNetworks {
    create("JgBridge") {
        title.set("jg-network")
    }
}
