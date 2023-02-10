package priv.alex.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.cooccurring
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int

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


    val lexicalFile by option("-il", help = "Lexical file path").file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    )
    val productOutput by LexerOutPutConfig().cooccurring()

    override fun run() {
        TODO("Not yet implemented")
    }
}