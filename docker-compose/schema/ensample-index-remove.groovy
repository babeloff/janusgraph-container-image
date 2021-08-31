
/**
 * https://javadoc.io/doc/org.janusgraph/janusgraph-core/latest/org/janusgraph/core/schema/JanusGraphManagement.html
 */

/**
 *  Make each index
 *
 *  (ixName, propName, modifier) = indexList[0]
 */
//
//indexList.forEach { ixName, propName, modifier ->
//    mgmt = graph.openManagement()
//    if (! mgmt.containsGraphIndex(ixName)) {
//        mgmt.commit()
//        return;
//    }
//    gindex = mgmt.getGraphIndex(ixName)
//    mgmt.updateIndex(gindex, SchemaAction.DISABLE_INDEX).get()
//    mgmt.commit()
//}
//
///**
// *  Wait for the status of each index to change from ENABLED to DISABLED
// */
//indexList.forEach { ixName, propName, modifier ->
//    report = ManagementSystem.awaitGraphIndexStatus(graph, ixName).status(SchemaStatus.DISABLED).call()
//}

/**
 *  Remove the index
 */
indexList.forEach { ixName, propName, modifier ->
    mgmt = graph.openManagement()
    ix = mgmt.getGraphIndex(ixName)
    mgmt.updateIndex(ix, SchemaAction.REMOVE_INDEX)
    mgmt.commit()
}
