package priv.alex.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.eagerOption
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.switch
import com.github.ajalt.clikt.parameters.types.file
import priv.alex.core.ProcessorGlobalConfig

class ParserCommand : CliktCommand() {
    init {
        eagerOption("-h") {
            throw PrintMessage(commandHelp)
        }
    }


    private val syntaxFile by option("-is", help = "Syntax file path").file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    )

    private val isGenerateAnalyseTable by option(help = "Whether to output predictive analytics tables").switch("-sa" to true)
        .default(false)

    override fun run() {
        ProcessorGlobalConfig.syntaxFile = syntaxFile
        ProcessorGlobalConfig.analyticsTableOutput = isGenerateAnalyseTable
    }
}