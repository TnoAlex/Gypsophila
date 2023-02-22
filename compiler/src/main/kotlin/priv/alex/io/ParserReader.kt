package priv.alex.io

import org.yaml.snakeyaml.Yaml
import priv.alex.logger.Logger
import priv.alex.parser.*
import java.io.File
import java.util.*

@Logger
class ParserReader(file: File) : Reader {

    private val yaml: Iterable<Any>

    init {
        log.info("Load lexical rules <- ${file.name}")
        if (!file.isFile) {
            log.error("Illegal documents")
            throw RuntimeException("Illegal documents")
        }
        try {
            yaml = Yaml().loadAll(file.reader(Charsets.UTF_8)) as Iterable<Any>
        } catch (e: Exception) {
            log.error("Unable to load syntax")
            throw RuntimeException(e.message, e.cause)
        }
    }


    fun readParser(): Pair<Production, MutableList<Production>> {
        val res = ArrayList<Production>(32)
        yaml as List<Any>
        assert(yaml.size == 2) { "An incomprehensible syntax file" }
        val entryPointMap = yaml[0] as Map<*, *>
        assert(entryPointMap.keys.size == 1 && entryPointMap.values.size == 1) { "Syntax that contains multiple starting characters is not supported" }

        val entryPointHead = ProductionHead(NonTerminator(entryPointMap.keys.first() as String))
        val symbols = ArrayList<Symbol>(8)

        entryPointMap.values.forEach {
            it as String
            it.split(" ").forEach { s ->
                if (NonTerminator.isNonTerminator(s))
                    symbols.add(Terminator(s))
                else
                    symbols.add(NonTerminator(s))
            }
        }
        val entryPoint = Production(entryPointHead, ProductionBody(symbols))
        symbols.clear()

        (yaml[1] as Map<*, *>).forEach { (k, v) ->
            k as String
            val head = ProductionHead(NonTerminator(k))
            v as List<*>
            v.forEach {
                it as String
                it.split(" ").forEach { s ->
                    if (NonTerminator.isNonTerminator(s))
                        symbols.add(Terminator(s))
                    else
                        symbols.add(NonTerminator(s))
                }
                res.add(Production(head, ProductionBody(symbols)))
                symbols.clear()
            }
        }
        return Pair(entryPoint, Collections.unmodifiableList(res))
    }
}