
/**
 * https://javadoc.io/doc/org.janusgraph/janusgraph-core/latest/org/janusgraph/core/schema/JanusGraphManagement.html
 */
indexList = [ ['byId_Index', '[]ID', 'unique'],
              ['byName_Index', '[]Name', 'bag'] ]

/**
 * Clear the open transactions
 */
graph.getOpenTransactions().forEach { tx ->  tx.commit() }
