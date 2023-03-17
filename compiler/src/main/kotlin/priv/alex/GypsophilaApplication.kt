package priv.alex

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.file
import priv.alex.core.Processor
import priv.alex.core.ProcessorGlobalConfig

class Gypsophila : CliktCommand() {

    private val codeFile by option("--input", help = "Source code to be processed").file(
        mustExist = true,
        mustBeReadable = true
    )
        .required()
        .check {
            if (it.isDirectory) it.listFiles()!!
                .isNotEmpty() else true
        }

    private val lexicalFile by option("--tp", help = "Lexical file path").file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    ).required()

    private val isGenerateLexer by option(help = "Whether to output token file").switch("--sl" to true).default(false)

    private val syntaxFile by option("--sp", help = "Syntax file path").file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    )

    private val isGenerateAnalyseTable by option(help = "Whether to output predictive analytics tables").switch("--sa" to true)
        .default(false)

    override fun run() {
        ProcessorGlobalConfig.sourceFile = codeFile
        ProcessorGlobalConfig.lexerFile = lexicalFile
        ProcessorGlobalConfig.tokenOutput = isGenerateLexer
        ProcessorGlobalConfig.syntaxFile = syntaxFile
        ProcessorGlobalConfig.analyticsTableOutput = isGenerateAnalyseTable
    }
}

fun main(args: Array<String>) {
    Gypsophila().main(args)
    Processor().run()
}


