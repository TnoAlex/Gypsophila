package priv.alex.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.options.eagerOption

class ParserCommand:CliktCommand() {
    init {
        eagerOption("-h") {
            throw PrintMessage(commandHelp)
        }
    }
    override fun run() {
        TODO("Not yet implemented")
    }
}