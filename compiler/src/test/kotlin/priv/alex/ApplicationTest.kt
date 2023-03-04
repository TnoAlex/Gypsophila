package priv.alex

import priv.alex.code.CodeFile
import priv.alex.gui.CcGraphAdapter
import priv.alex.gui.RegexGraphAdapter
import priv.alex.io.LexicalReader
import priv.alex.io.ParserReader
import priv.alex.lexer.engine.automaton.DFA
import priv.alex.lexer.engine.automaton.NFABuilder
import priv.alex.lexer.token.TokenBuilder
import priv.alex.lexer.token.TokenFile
import priv.alex.lexer.token.TokenLine
import priv.alex.parser.engine.cc.CanonicalClusterBuilder
import priv.alex.parser.engine.lr.LRAnalyzer
import java.io.File
import javax.swing.JFrame

fun main() {
    testLrAnalyzer()
//    testParser()
//    testDFA()
//    codeReaderTest()
//    nfaBuilderTest()
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

private fun nfaBuilderTest() {
    val builder = NFABuilder("(\\-[0-9]|[0-9])([0-9]*\\.[0-9]+)")
    val jFrame = RegexGraphAdapter(builder.build())
    jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    jFrame.pack()
    jFrame.isVisible = true
}

private fun codeReaderTest() {
    val file = CodeFile(File("F:\\scratch_1.cc"))
    val lexicalReader = LexicalReader(File("F:\\scratch.yml"))
    val lexer = lexicalReader.readLexicon()
    val tokenBuilder = TokenBuilder(lexer)
}

private fun testDFA() {
    val dfa = DFA("\"[ -~]*\"")
    val res = dfa.match("\"main")
    println(res)
}

private fun testParser() {
    val reader = ParserReader(File("F:\\testC\\test.yml"))
    val res = reader.readParser()
    val builder = CanonicalClusterBuilder(res.first, res.second)
    val jFrame = CcGraphAdapter(builder.build())
    jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    jFrame.pack()
    jFrame.isVisible = true
    println()
}

private fun testLrAnalyzer() {
    val code = CodeFile(File("F:\\testC\\scratch_1.cc"))
    val lexicalReader = LexicalReader(File("F:\\testC\\scratch.yml"))
    val parserReader = ParserReader(File("F:\\testC\\parser.yml"))
    val lexer = lexicalReader.readLexicon()
    val syntax = parserReader.readParser()
    val tokenLines = ArrayList<TokenLine>()
    val tokenBuilder = TokenBuilder(lexer)
    code.lines.forEach {
        tokenLines.add(tokenBuilder.buildToken(it))
    }
    val tokenFile = TokenFile(tokenLines, code.fileName)
    val builder = CanonicalClusterBuilder(syntax.first, syntax.second)
    val cc = builder.build()
    val analyser = LRAnalyzer(builder.productions.toSet(), cc, builder.broadeningSyntax)
    val ast = analyser.analyze(tokenFile)
    println()
}