package priv.alex.io

import com.google.gson.Gson
import priv.alex.ast.AST
import priv.alex.logger.Logger
import java.io.File
import java.io.FileWriter

/**
 * Ast writer
 *
 * @constructor Create Ast writer
 */
@Logger
class AstWriter : Writer {
    /**
     * Write to
     *
     *@param file The path of the file to write
     *@param obj The AST to be written to the file
     */
    override fun writeTo(file: File, obj: Any) {
        try {
            val ast = obj as AST
            if (!file.exists())
                file.createNewFile()
            val writer = FileWriter(file)
            val jsonMap = HashMap<String, Any>()
            jsonMap["vertices"] = ast.graph.vertexSet()
            jsonMap["edges"] = ast.graph.edgeSet()
            val json = Gson().toJson(jsonMap)
            log.info("Write AST to ${ast.buildBy.split(".")[0]}.ast")
            writer.use {
                it.write(json)
            }
        } catch (e: Exception) {
            log.error("An irreversible error occurred during the output process")
            throw RuntimeException(e.message, e.cause)
        }
    }
}

