package priv.alex.gui

import priv.alex.lexer.engine.fsm.DFABuilder
import priv.alex.lexer.engine.fsm.NFA
import priv.alex.lexer.engine.fsm.NFABuilder
import javax.swing.JFrame

fun main(){
//    nfaBuilderTest()
    val nfa  = NFA("abc|bc|ad")
    val builder = DFABuilder(nfa)
    val jFrame = RegexGraphAdapter(builder.build())
    jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    jFrame.pack()
    jFrame.isVisible = true
}

private fun nfaBuilderTest(){
    val builder = NFABuilder("abc|bc|ad")
    val jFrame = RegexGraphAdapter(builder.build())
    jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    jFrame.pack()
    jFrame.isVisible = true
}