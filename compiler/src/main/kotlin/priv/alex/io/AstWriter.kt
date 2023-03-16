package priv.alex.io

import org.jgrapht.graph.DefaultEdge
import org.jgrapht.nio.dot.DOTExporter
import priv.alex.ast.AST
import priv.alex.ast.ASTNode
import priv.alex.logger.Logger
import java.io.File
import java.io.FileWriter
import java.io.StringWriter

@Logger
class AstWriter : Writer {
    override fun writeTo(file: File, obj: Any) {
        try {
            val ast = obj as AST
            if (!file.exists())
                file.createNewFile()
            val export = DOTExporter<ASTNode, DefaultEdge>()
            val stringWriter = StringWriter()
            export.exportGraph(ast.graph, stringWriter)
            val writer = FileWriter(file)
            writer.use {
                it.write(stringWriter.toString())
            }
        } catch (e: Exception) {
            log.error("An irreversible error occurred during the output process")
            throw RuntimeException(e.message, e.cause)
        }
    }
}