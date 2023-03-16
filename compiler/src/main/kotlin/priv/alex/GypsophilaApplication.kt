package priv.alex

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.eagerOption
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import priv.alex.cli.LexerCommand
import priv.alex.cli.ParserCommand
import priv.alex.core.Processor
import priv.alex.core.ProcessorGlobalConfig

class GypsophilaApplication : CliktCommand() {

    init {
        eagerOption("-h") {
            throw PrintMessage(commandHelp)
        }
    }

    private val codeFile by option("-i", help = "Source code to be processed").file(
        mustExist = true,
        mustBeReadable = true
    )
        .required()
        .check {
            if (it.isDirectory) it.listFiles()!!
                .isNotEmpty() else true
        }

    override val commandHelp: String
        get() = "To view the subcommand Help, use [Name -h]"

    override fun run() {
        ProcessorGlobalConfig.sourceFile = codeFile
    }
}

fun main(args: Array<String>) {
    GypsophilaApplication()
        .subcommands(LexerCommand(), ParserCommand())
        .main(args)
    Processor().run()
}


