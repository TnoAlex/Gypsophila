package priv.alex.io

import com.google.gson.Gson
import priv.alex.logger.Logger
import priv.alex.parser.engine.lr.LRTable
import java.io.File
import java.io.FileWriter

@Logger
class AnalyzeTableWriter : Writer {
    override fun writeTo(file: File, obj: Any) {
        try {
            val tokenFile = obj as LRTable
            if (!file.exists())
                file.createNewFile()
            val json = Gson().toJson(tokenFile)
            val writer = FileWriter(file)
            writer.use {
                it.write(json)
            }
        } catch (e: Exception) {
            log.error("An irreversible error occurred during the output process")
            throw RuntimeException(e.message, e.cause)
        }
    }
}