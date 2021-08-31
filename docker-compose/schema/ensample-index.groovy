
/**
 * https://javadoc.io/doc/org.janusgraph/janusgraph-core/latest/org/janusgraph/core/schema/JanusGraphManagement.html
 */

mgmt = graph.openManagement()
mgmt.printIndexes()
mgmt.commit()

indexList = [ ['byIdComposite', '[]ID', 'unique'],
              ['byNameComposite', '[]Name', 'bag'] ]

/**
 *  Make each index
 */
mgmt = graph.openManagement()
indexList.forEach { ixName, propName, modifier ->
    if (! mgmt.containsGraphIndex(ixName)) {
        pk = mgmt.getPropertyKey(propName)
        switch (modifier) {
            case 'unique':
                mgmt .buildIndex ( ixName, Vertex.class )
                        .addKey ( pk )
                        .unique()
                        .buildCompositeIndex ( )
            default:
                mgmt .buildIndex ( ixName, Vertex.class )
                        .addKey ( pk )
                        .buildCompositeIndex ( )
        }
    }
}
mgmt.commit()

mgmt = graph.openManagement()
name = mgmt.getPropertyKey('name')
god = mgmt.getVertexLabel('god')
mgmt.buildIndex('byNameAndLabel', Vertex.class)
        .addKey(name)
        .indexOnly(god).buildCompositeIndex()
mgmt.commit()

/**
 *  Wait for each index to be accepted
 */
indexList.forEach { ixName, propName ->
    ManagementSystem.awaitGraphIndexStatus(graph, ixName).call()
}

/**
 *  Reindex each existing data
 */
mgmt = graph.openManagement()
indexList.forEach { ixName, propName ->
    ix = mgmt.getGraphIndex(ixName)
    mgmt.updateIndex(ix, SchemaAction.REINDEX).get()
}
mgmt.commit()
