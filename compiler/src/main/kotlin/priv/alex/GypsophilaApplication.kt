package priv.alex

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.cooccurring
import com.github.ajalt.clikt.parameters.options.*
import priv.alex.cli.LexerCommand
import priv.alex.cli.ParserCommand
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int

class GypsophilaApplication : CliktCommand() {

    inner class ProcessThreadConfig : OptionGroup(){
        val isMultithreading  by option(help = "Allows the program to multi-thread").switch("-mt" to true).default(false)
        val threadNumber by option("-mtn",help = "The number of parallel threads in the program").int().required()
    }
    init {
        eagerOption("-h") {
            throw PrintMessage(commandHelp)
        }
    }

    val codeFile by option("-i", help = "Source code to be processed").file(mustExist = true, mustBeReadable = true)
        .required()
        .check {
            if (it.isDirectory) it.listFiles()!!
                .isNotEmpty() else true
        }

    val threadConfig by ProcessThreadConfig().cooccurring()
    override val commandHelp: String
        get() = "To view the subcommand Help, use [Name -h]"

    override fun run() {

    }
}

fun main(args: Array<String>) =
    GypsophilaApplication()
        .subcommands(LexerCommand(), ParserCommand())
        .main(args)

