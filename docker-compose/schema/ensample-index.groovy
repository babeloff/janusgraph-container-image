
/**
 * https://javadoc.io/doc/org.janusgraph/janusgraph-core/latest/org/janusgraph/core/schema/JanusGraphManagement.html
 */
graph.tx().commit()

indexList = [ ['byId_Index', '[]ID', 'unique'],
              ['byName_Index', '[]Name', 'bag'] ]

/**
 * Show the results
 */
mgmt = graph.openManagement()
mgmt.printSchema()
//mgmt.printIndexes()
mgmt.commit()

graph.getOpenTransactions().forEach { tx ->  tx.rollback() }
