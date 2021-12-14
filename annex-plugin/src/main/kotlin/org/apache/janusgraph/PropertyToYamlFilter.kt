package org.apache.janusgraph

import org.apache.tools.ant.filters.BaseFilterReader
import org.apache.tools.ant.filters.ChainableReader
import java.io.IOException
import java.io.Reader

/**
 * A custom filter to convert test to uppercase
 */
class PropertyToYamlFilter : BaseFilterReader {
    constructor() {}
    constructor(input: Reader?) : super(input) {}

    @Throws(IOException::class)
    override fun read(): Int {
        val ch = super.read()
        return Character.toUpperCase(ch)
    }
}