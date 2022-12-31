package priv.alex

import priv.alex.cli.LexerCommand
import priv.alex.cli.ParserCommand
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class GypsophilaApplication : CliktCommand() {
    override fun run() {

    }
}

fun main(args: Array<String>) =
    GypsophilaApplication().subcommands(
    LexerCommand(),
    ParserCommand()
    ).main(args)