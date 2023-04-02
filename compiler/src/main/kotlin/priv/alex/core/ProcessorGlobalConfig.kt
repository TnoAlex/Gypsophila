package priv.alex.core

import java.io.File

/**
 * Processor global config
 *
 * @constructor Create Processor global config
 */
object ProcessorGlobalConfig {
    var sourceFile: File? = null
    var tokenOutput: Boolean = false
    var syntaxFile: File? = null
    var lexerFile: File? = null
    var analyticsTableOutput: Boolean = false
}