package priv.alex.io

import com.google.gson.GsonBuilder
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import priv.alex.ast.ASTNode
import priv.alex.ast.AstGraphAdapter
import priv.alex.core.OpenEdge
import priv.alex.logger.Logger
import priv.alex.parser.Symbol
import priv.alex.parser.SymbolDeserializer
import java.io.File
import java.io.FileInputStream
import javax.swing.JFrame

/**
 * Ast reader
 *
 * @property file Serialize the AST to be read into the file
 * @constructor Create Ast reader
 */
@Logger
class AstReader(val file: File) : Reader {

    /**
     * Show ast
     */
    fun showAst() {
        try {
            val astJson = ByteArray(file.length().toInt())
            val astFileStream = FileInputStream(file)
            astFileStream.use {
                it.read(astJson)
            }
            val gson =
                GsonBuilder().serializeNulls().registerTypeAdapter(Symbol::class.java, SymbolDeserializer()).create()
            val json = String(astJson, Charsets.UTF_8)
            val astMap = gson.fromJson(json, HashMap::class.java)
            val graph: Graph<ASTNode, DefaultEdge> = DefaultDirectedGraph(DefaultEdge::class.java)
            (astMap["vertices"] as Collection<*>).forEach {
                val vertex = gson.fromJson(gson.toJson(it),ASTNode::class.java)
                graph.addVertex(vertex)
            }
            (astMap["edges"] as Collection<*>).forEach { it ->
                val edge = gson.fromJson(gson.toJson(it),OpenEdge::class.java)
                val target = graph.vertexSet().first { v->v.nodeId == (edge.target as Map<String,Any>)["nodeId"] }
                val source = graph.vertexSet().first { v->v.nodeId == (edge.source as Map<String,Any>)["nodeId"] }
                graph.addEdge(source,target,OpenEdge())
            }
            val jFrame = AstGraphAdapter(graph)
            jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            jFrame.pack()
            jFrame.isVisible = true
        }catch (e:Exception){
            log.error("Can not read ${file.name}")
        }
    }
}