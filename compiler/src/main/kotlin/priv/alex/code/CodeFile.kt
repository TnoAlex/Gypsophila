package priv.alex.code

import priv.alex.io.CodeReader
import priv.alex.logger.Logger
import java.io.File
import java.io.IOException

@Logger
class CodeFile(filePath: String) {

    val fileName: String
    val lines: ArrayList<CodeLine>
    private val file: File

    init {
        try {
            file = File(filePath)
            fileName = file.name
        } catch (e: IOException) {
            log.error("File access failed")
            throw RuntimeException("File does not exist or cannot be accessed")
        }
        log.info("Loading Source Code <- $fileName")
        lines = CodeReader(file).readLines()
        log.info("Done")
    }

}