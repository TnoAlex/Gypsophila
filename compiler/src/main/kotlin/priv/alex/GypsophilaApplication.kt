package priv.alex

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.file
import priv.alex.core.Processor
import priv.alex.core.ProcessorGlobalConfig
import priv.alex.io.AstReader
import kotlin.system.exitProcess

class Gypsophila : CliktCommand() {
    private val showAst by option("-show", "--show_ast", help = "Show Ast").file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    )

    private val codeFile by option("-i", "--input", help = "Source code to be processed").file(
        mustExist = true,
        mustBeReadable = true
    ).check {
            if (it.isDirectory) it.listFiles()!!
                .isNotEmpty() else true
        }

    private val lexicalFile by option("-lp", "--lexer_path", help = "Lexical file path").file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    )

    private val isGenerateLexer by option(help = "Whether to output token file").switch("-sl" to true).default(false)

    private val syntaxFile by option("-sp", "--syntax_path", help = "Syntax file path").file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    )

    private val isGenerateAnalyseTable by option(help = "Whether to output predictive analytics tables").switch("-sa" to true)
        .default(false)

    override fun run() {
        if (showAst != null){
            val astReader = AstReader(showAst!!)
            astReader.showAst()
        }else{
            require(codeFile!=null)
            require(lexicalFile!=null)
            require(syntaxFile!=null)
            ProcessorGlobalConfig.sourceFile = codeFile
            ProcessorGlobalConfig.lexerFile = lexicalFile
            ProcessorGlobalConfig.tokenOutput = isGenerateLexer
            ProcessorGlobalConfig.syntaxFile = syntaxFile
            ProcessorGlobalConfig.analyticsTableOutput = isGenerateAnalyseTable
            Processor().run()
        }
    }
}

fun main(args: Array<String>) {
    Gypsophila().main(args)
}


