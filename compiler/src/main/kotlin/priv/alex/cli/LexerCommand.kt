package priv.alex.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.file
import priv.alex.core.ProcessorGlobalConfig

class LexerCommand : CliktCommand() {
    init {
        eagerOption("-h") {
            throw PrintMessage(commandHelp)
        }
    }

    private val lexicalFile by option("-il", help = "Lexical file path").file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    ).required()

    private val isGenerateLexer by option(help = "Whether to output token file").switch("-sl" to true).default(false)

    override fun run() {
        ProcessorGlobalConfig.lexerFile = lexicalFile
        ProcessorGlobalConfig.tokenOutput = isGenerateLexer
    }
}