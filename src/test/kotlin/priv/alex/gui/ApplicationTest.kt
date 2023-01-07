package priv.alex.gui

import priv.alex.lexer.engine.fsm.NFABuilder
import javax.swing.JFrame

fun main(){
    val builder  = NFABuilder("([A-F]+|[0-9]*|[a-f]?.bb)(\\[\\*\\+)")
    val jFrame = RegexGraphAdapter(builder.build())
    jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    jFrame.pack()
    jFrame.isVisible = true
}