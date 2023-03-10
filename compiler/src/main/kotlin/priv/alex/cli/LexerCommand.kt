package priv.alex.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.cooccurring
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.file
import priv.alex.core.LexerProcessor

class LexerCommand : CliktCommand() {
    init {
        eagerOption("-h") {
            throw PrintMessage(commandHelp)
        }
    }
    inner class LexerOutPutConfig : OptionGroup() {
        val isGenerateLexer by option(help = "Whether to output a lexical file").switch("-sl" to true).default(false)
        val lexicalOutPutFile by option("-slp", help = "Lexical file output location").file(
            mustExist = true,
            canBeFile = false,
            canBeDir = true
        ).required()
    }


    private val lexicalFile by option("-il", help = "Lexical file path").file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    ).required()
    private val productOutput by LexerOutPutConfig().cooccurring()

    override fun run() {
        val lexerProcessor = LexerProcessor(lexicalFile,null,false)
        productOutput?.let {
            lexerProcessor.lexicalOutputFile = it.lexicalOutPutFile
            lexerProcessor.needOutput = it.isGenerateLexer
        }
        lexerProcessor.analyse()
    }
}