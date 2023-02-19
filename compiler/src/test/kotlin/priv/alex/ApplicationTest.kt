package priv.alex

import priv.alex.code.CodeFile
import priv.alex.gui.RegexGraphAdapter
import priv.alex.io.LexicalReader
import priv.alex.lexer.engine.automaton.DFA
import priv.alex.lexer.engine.automaton.NFABuilder
import priv.alex.lexer.token.Token
import priv.alex.lexer.token.TokenBuilder
import java.io.File
import javax.swing.JFrame

fun main(){
//    testDFA()
//    codeReaderTest()
    nfaBuilderTest()
//    val nfa  = NFA("a([a-z])*")
//    val builder = DFABuilder(nfa)
//    val jFrame = RegexGraphAdapter(builder.build())
//    val  p = "\"[ -~]*\""
//    val dfa = DFA(p)
//    val jFrame = RegexGraphAdapter(dfa.dfa )
//    jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//    jFrame.pack()
//    jFrame.isVisible = true
}

private fun nfaBuilderTest(){
    val builder = NFABuilder("\"[ -~]*\"")
    val jFrame = RegexGraphAdapter(builder.build())
    jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    jFrame.pack()
    jFrame.isVisible = true
}

private fun codeReaderTest(){
    val file = CodeFile(File("F:\\scratch_1.cc"))
    val lexicalReader = LexicalReader(File("F:\\scratch.yml"))
    val lexer = lexicalReader.readLexicon()
    val tokenBuilder = TokenBuilder(lexer)
    val res = ArrayList<Token>()
}

private fun testDFA(){
    val dfa =DFA("\"[ -~]*\"")
    val res = dfa.match("\"main")
    println(res)
}