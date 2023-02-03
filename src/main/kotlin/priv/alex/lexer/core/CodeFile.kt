package priv.alex.lexer.core

import java.io.File
import java.io.IOException

class CodeFile(val filePath: String) {

    val fileName: String
    private val file: File

    init {
        try {
            file = File(filePath)
            fileName = file.name
        } catch (e: IOException) {
            throw RuntimeException("File does not exist or cannot be accessed")
        }

    }
}