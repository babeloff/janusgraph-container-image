
mgmt = graph.openManagement()
mgmt.printSchema()
mgmt.commit()

mgmt = graph.openManagement()
[
        '[]Definition',
        '[]Formula',
].forEach {labelName ->
    if (!mgmt.containsVertexLabel(labelName)) {
        mgmt.makeVertexLabel(labelName).make()
    }
}
mgmt.commit()

mgmt = graph.openManagement()
['inside', 'root'].forEach {labelName ->
    if (!mgmt.containsEdgeLabel(labelName)) {
        mgmt.makeEdgeLabel(labelName).multiplicity(org.janusgraph.core.Cardinality.MANY2ONE).make()
    }
}
mgmt.commit()

mgmt = graph.openManagement()
[
        'copy',
        'source',
        'target',
].forEach {labelName ->
    if (!mgmt.containsEdgeLabel(labelName)) {
        mgmt.makeEdgeLabel(labelName).multiplicity(org.janusgraph.core.Cardinality.MULTI).make()
    }
}
mgmt.commit()

mgmt = graph.openManagement()
[
        'Created_Using_Template',
        'Template_Configuration',
].forEach {propName ->
    if (!mgmt.containsPropertyKey(propName)) {
        mgmt.makePropertyKey(propName).dataType(Boolean.class).cardinality(org.janusgraph.core.Cardinality.SINGLE).make()
    }
}
mgmt.commit()

mgmt = graph.openManagement()
[
        '[]ID',
        '[]Name',
        '_partition',
        'description',
        'graph.graphname',
        'status',
].forEach {propName ->
    if (!mgmt.containsPropertyKey(propName)) {
        mgmt.makePropertyKey(propName).dataType(String.class).cardinality(org.janusgraph.core.Cardinality.SINGLE).make()
    }
}
mgmt.commit()



