package priv.alex.io

import org.yaml.snakeyaml.Yaml
import priv.alex.logger.Logger
import priv.alex.parser.*
import java.io.File
import java.util.*

@Logger
class ParserReader (file:File) : Reader {

    private val yaml: Map<String, Any>

    init {
        log.info("Load lexical rules <- ${file.name}")
        if (!file.isFile) {
            log.error("Illegal documents")
            throw RuntimeException("Illegal documents")
        }
        try {
            yaml = Yaml().load(file.reader(Charsets.UTF_8)) as Map<String, Any>
        } catch (e: Exception) {
            log.error("Unable to load syntax")
            throw RuntimeException(e.message, e.cause)
        }
    }

    fun readParser(): List<Production> {
        val res = ArrayList<Production>(32)
        yaml.forEach { (k,v)->
            val head = ProductionHead(NonTerminator(k))
            val bodies = ArrayList<Symbol>(4)
            v as List<*>
            v.forEach {
                if (Terminator.isTerminator(it as String))
                    bodies.add(Terminator(it))
                else
                    bodies.add(NonTerminator(it))
            }
            res.add(Production(head, ProductionBody(Collections.unmodifiableList(bodies))))
        }
        return Collections.unmodifiableList(res)
    }
}