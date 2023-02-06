package priv.alex.io

import org.yaml.snakeyaml.Yaml
import priv.alex.lexer.Lexical
import priv.alex.lexer.Lexicon
import priv.alex.lexer.token.TokenType
import priv.alex.logger.Logger
import java.io.File

@Logger
class LexicalReader(file: File) : Reader {

    private val yaml: Map<String, Any>

    init {
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

    fun readLexicon(): ArrayList<Lexicon> {
        val res = ArrayList<Lexicon>()
        yaml.forEach { (k, v) ->
            try {
                val tokenType = TokenType.valuesOf(k)
                v as Map<String, String>
                val t = ArrayList<Lexical>()
                v.forEach { t.add(Lexical(tokenType, it.key, it.value)) }
                res.add(Lexicon(tokenType,t))
            } catch (e: Exception) {
                log.error("An error occurred during reading the lexical file")
                throw RuntimeException(e.message, e.cause)
            }
        }
        return res
    }

}