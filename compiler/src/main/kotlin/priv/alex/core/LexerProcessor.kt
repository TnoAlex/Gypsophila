package priv.alex.core

import priv.alex.code.CodeContainer
import priv.alex.io.LexicalReader
import priv.alex.io.LexicalWriter
import priv.alex.lexer.token.TokenBuilder
import priv.alex.lexer.token.TokenFile
import priv.alex.lexer.token.TokenLine
import priv.alex.logger.Logger
import java.io.File
import java.util.*

@Logger
class LexerProcessor(lexicalFile: File,  var lexicalOutputFile: File?,  var needOutput: Boolean) {

    private val codeContainer = CodeContainer(ProcessorGlobalConfig.sourceFile!!)
    private val lexicalBuilder = LexicalReader(lexicalFile)
    private val tokenBuilder = TokenBuilder(lexicalBuilder.readLexicon())
    private val lexicalWriter = LexicalWriter()

    fun analyse() {
        val tokens = ArrayList<TokenFile>()
        val file = codeContainer.advance()
        while (file != null) {
            val lines = ArrayList<TokenLine>()
            file.lines.forEach {
                lines.add(tokenBuilder.buildToken(it))
            }
            tokens.add(TokenFile(Collections.unmodifiableList(lines), file.fileName))
        }
        if (needOutput){
            tokens.forEach {
                log.info("Write to -> ${it.fileName}")
                lexicalWriter.writeTo(lexicalOutputFile!!,it)
            }
        }
        log.info("Lexical analysis complete")
    }

}