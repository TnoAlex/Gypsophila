package priv.alex.io

import com.google.gson.Gson
import priv.alex.lexer.token.TokenFile
import priv.alex.logger.Logger
import java.io.File
import java.io.FileWriter

@Logger
class LexicalWriter : Writer {
    override fun writeTo(file: File, obj: Any) {
        try {
            val tokenFile = obj as TokenFile
            val outputFile = File(file.path + File.separator + tokenFile.fileName)
            if (!outputFile.exists())
                outputFile.createNewFile()
            val json = Gson().toJson(tokenFile)
            val writer = FileWriter(outputFile)
            writer.use {
                it.write(json)
            }
        } catch (e: Exception) {
            log.error("An irreversible error occurred during the output process")
            throw RuntimeException(e.message, e.cause)
        }

    }

}