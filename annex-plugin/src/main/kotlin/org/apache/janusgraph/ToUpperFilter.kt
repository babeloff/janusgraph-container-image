package org.apache.janusgraph

import org.apache.tools.ant.filters.BaseFilterReader
import org.apache.tools.ant.filters.ChainableReader
import java.io.IOException
import java.io.Reader

/**
 * A custom filter to convert test to uppercase
 */
class ToUpperFilter : BaseFilterReader, ChainableReader {
    constructor() {}
    constructor(`in`: Reader?) : super(`in`) {}

    override fun chain(rdr: Reader): Reader {
        val filter = ToUpperFilter(rdr)
        filter.project = project
        return filter
    }

    @Throws(IOException::class)
    override fun read(): Int {
        val ch = super.read()
        return Character.toUpperCase(ch)
    }
}