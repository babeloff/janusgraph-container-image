
/**
 * https://javadoc.io/doc/org.janusgraph/janusgraph-core/latest/org/janusgraph/core/schema/JanusGraphManagement.html
 */
/**
 * Show the results
 */
mgmt = graph.openManagement()
mgmt.printSchema()
//mgmt.printIndexes()
mgmt.commit()

