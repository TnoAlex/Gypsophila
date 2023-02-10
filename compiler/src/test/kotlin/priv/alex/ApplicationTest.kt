package priv.alex

import priv.alex.gui.RegexGraphAdapter
import priv.alex.code.CodeFile
import priv.alex.io.LexicalReader
import priv.alex.lexer.engine.fsm.DFA
import priv.alex.lexer.engine.fsm.DFABuilder
import priv.alex.lexer.engine.fsm.NFA
import priv.alex.lexer.engine.fsm.NFABuilder
import priv.alex.lexer.token.Token
import priv.alex.lexer.token.TokenBuilder
import java.io.File
import javax.swing.JFrame

fun main(){
//    testDFA()
    codeReaderTest()
//    nfaBuilderTest()
//    val nfa  = NFA("a([a-z])*")
//    val builder = DFABuilder(nfa)
//    val jFrame = RegexGraphAdapter(builder.build())
    val  p = "(\\-[1-9]|[0-9])(0-9)*"
    val dfa = DFA(p)
    val jFrame = RegexGraphAdapter(dfa.dfa )
    jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    jFrame.pack()
    jFrame.isVisible = true
}

private fun nfaBuilderTest(){
    val builder = NFABuilder("(\\-[1-9]|[0-9])(0-9)*")
    val jFrame = RegexGraphAdapter(builder.build())
    jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    jFrame.pack()
    jFrame.isVisible = true
}

private fun codeReaderTest(){
    val file = CodeFile(File("C:\\Users\\NicoAlex\\AppData\\Roaming\\JetBrains\\IntelliJIdea2022.3\\scratches\\scratch_1.cc"))
    val lexicalReader = LexicalReader(File("C:\\Users\\NicoAlex\\AppData\\Roaming\\JetBrains\\IntelliJIdea2022.3\\scratches\\scratch.yml"))
    val lexer = lexicalReader.readLexicon()
    val tokenBuilder = TokenBuilder(lexer)
    val res = ArrayList<Token>()
    file.lines.forEach {
        res.addAll(tokenBuilder.buildToken(it))
    }
}

private fun testDFA(){
    val dfa =DFA("(_|[A-Z]|[a-z])([A-Z]|[a-z]|[0-9])*")
    val res = dfa.match("main")
    println(res)
}