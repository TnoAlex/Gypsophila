package priv.alex.io

import org.yaml.snakeyaml.Yaml
import priv.alex.logger.Logger
import priv.alex.parser.*
import java.io.File

@Logger
class ParserReader(file: File) : Reader {

    private val yaml: List<Any>

    init {
        log.info("Load lexical rules <- ${file.name}")
        if (!file.isFile) {
            log.error("Illegal documents")
            throw RuntimeException("Illegal documents")
        }
        try {
            yaml = (Yaml().loadAll(file.reader(Charsets.UTF_8)) as Iterable<Any>).toList()
        } catch (e: Exception) {
            log.error("Unable to load syntax")
            throw RuntimeException(e.message, e.cause)
        }
    }


    fun readParser(): Pair<Production, List<Production>> {
        val res = HashSet<Production>(32)
        assert(yaml.size == 2) { "An incomprehensible syntax file" }
        val entryPointMap = yaml[0] as Map<*, *>
        assert(entryPointMap.keys.size == 1 && entryPointMap.values.size == 1) { "Syntax that contains multiple starting characters is not supported" }

        val entryPointHead = ProductionHead(NonTerminator(entryPointMap.keys.first() as String))
        val symbols = ArrayList<Symbol>(4)

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

        (yaml[1] as Map<*, *>).forEach { (k, v) ->
            k as String
            val head = ProductionHead(NonTerminator(k))
            v as List<*>
            v.forEach {
                val symbol = ArrayList<Symbol>(4)
                it as String
                it.split(" ").forEach { s ->
                    if (NonTerminator.isNonTerminator(s))
                        symbol.add(NonTerminator(s))
                    else
                        symbol.add(Terminator(s))
                }
                res.add(Production(head, ProductionBody(symbol)))
            }
        }
        return Pair(entryPoint, res.toList())
    }
}