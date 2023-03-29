package priv.alex.io

import com.google.gson.Gson
import priv.alex.lexer.token.TokenFile
import priv.alex.logger.Logger
import java.io.File
import java.io.FileWriter

/**
 * Lexical writer
 *
 * @constructor Create Lexical writer
 */
@Logger
class LexicalWriter : Writer {
    /**
     * Write to
     *
     * @param file The path of the file to write
     * @param obj The Token file to be written to the file
     */
    override fun writeTo(file: File, obj: Any) {
        try {
            val tokenFile = obj as TokenFile
            val json = Gson().toJson(tokenFile)
            val writer = FileWriter(file)
            log.info("Write token sequence to ${tokenFile.fileName}")
            writer.use {
                it.write(json)
            }
        } catch (e: Exception) {
            log.error("An irreversible error occurred during the output process")
            throw RuntimeException(e.message, e.cause)
        }

    }

}