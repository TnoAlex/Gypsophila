package priv.alex.io

import org.yaml.snakeyaml.Yaml
import priv.alex.lexer.Lexical
import priv.alex.lexer.token.TokenType
import priv.alex.logger.Logger
import java.io.File

@Logger
class LexicalReader(file: File) : Reader {

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
            log.error("Unable to load lexicon")
            throw RuntimeException(e.message, e.cause)
        }
    }

    fun readLexicon(): HashMap<TokenType, ArrayList<Lexical>> {
        val res = HashMap<TokenType,ArrayList<Lexical>>(32)
        yaml.forEach { (k, v) ->
            try {
                val tokenType = TokenType.enumOf(k)
                v as Map<String, Any>
                val t = ArrayList<Lexical>()
                v.forEach {
                    t.add(Lexical(tokenType, it.key, (it.value as Map<String, String?>)["value"],
                        (it.value as Map<String, String?>)["regex"]))
                }
                res[tokenType] = t
            } catch (e: Exception) {
                log.error("An error occurred during reading the lexical file")
                throw RuntimeException(e.message, e.cause)
            }
        }
        return res
    }

}