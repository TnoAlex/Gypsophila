package priv.alex.gui

import priv.alex.lexer.engine.fsm.NFABuilder
import javax.swing.JFrame

fun main(){
    val builder  = NFABuilder("(a|b)*(aa|bb)(a|b)*")
    val jFrame = RegexGraphAdapter(builder.build())
    jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    jFrame.pack()
    jFrame.isVisible = true
}