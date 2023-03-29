package priv.alex.io

import com.google.gson.Gson
import priv.alex.logger.Logger
import priv.alex.parser.engine.lr.LRTable
import java.io.File
import java.io.FileWriter

/**
 * Analyze table writer
 *
 * @constructor Create  Analyze table writer
 */
@Logger
class AnalyzeTableWriter : Writer {
    /**
     * Write to
     *
     * @param file The path of the file to write
     * @param obj The analysis table to be written to the file
     */
    override fun writeTo(file: File, obj: Any) {
        try {
            val lrTable = obj as LRTable
            if (!file.exists())
                file.createNewFile()
            val json = Gson().toJson(lrTable)
            val writer = FileWriter(file)
            log.info("Write analyze table to analyseTable.json")
            writer.use {
                it.write(json)
            }
        } catch (e: Exception) {
            log.error("An irreversible error occurred during the output process")
            throw RuntimeException(e.message, e.cause)
        }
    }
}