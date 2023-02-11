package priv.alex.core

import java.io.File

object ProcessorGlobalConfig {
    var isMultithreading: Boolean = false
    var threadNumber: Int = 4
    var sourceFile : File? = null
}