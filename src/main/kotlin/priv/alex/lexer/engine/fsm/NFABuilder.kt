package priv.alex.lexer.engine.fsm

import org.jgrapht.graph.builder.GraphTypeBuilder
import priv.alex.lexer.engine.regex.RegexLexer
import priv.alex.lexer.engine.regex.RegexToken
import priv.alex.lexer.engine.regex.RegexTokenEnum

class NFABuilder(private val pattern: String) {

    private val graph =
        GraphTypeBuilder.directed<Int, FSMEdge>().allowingMultipleEdges(true).allowingSelfLoops(true).weighted(false)
            .buildGraph()!!

    private val lexer = RegexLexer(pattern)


    fun term() {

    }

    private fun matchAnySignChar(source: Int, id: Int) {
        graph.addVertex(id)
        graph.addEdge(source, id, FSMEdge(true))
        graph.addVertex(id + 1)
        graph.addEdge(id, id + 1, FSMEdge())
    }

    private fun matchSignChar(token: RegexToken, source: Int, id: Int) {
        graph.addVertex(id)
        graph.addEdge(source, id, FSMEdge(true))
        graph.addVertex(id + 1)
        graph.addEdge(id, id + 1, FSMEdge(token.value, null))
    }

    private fun matchSetChar(token: RegexToken, source: Int, id: Int) {
        var nextToken: RegexToken = token
        graph.addVertex(id)
        graph.addEdge(source, id, FSMEdge(true))
        graph.addVertex(id + 1)
        val cSet = ArrayList<Char>()

        while (nextToken != RegexToken(RegexTokenEnum.CCL_END)) {
            var chars = ArrayList<Char>()
            chars.add(nextToken.value)
            nextToken = lexer.advance()
            if(nextToken == RegexToken(RegexTokenEnum.DASH) && nextToken != RegexToken(RegexTokenEnum.CCL_END)){
                val oldToken = nextToken
                nextToken = lexer.advance()
                if(nextToken ==  RegexToken(RegexTokenEnum.CCL_END))
                    throw RuntimeException("Unexpected Regex Syntax")
                else{
                    chars = handleDash(oldToken.value,nextToken.value)
                }
            }
            cSet.addAll(chars)
            lexer.advance()
        }
        graph.addEdge(id,id+1,FSMEdge(null, cSet.toSet()))
    }

    private fun matchNiSetChar() {

    }

    private fun handleDash(startC: Char, endC: Char): ArrayList<Char> {
        val res = ArrayList<Char>()
        for (c in endC.downTo(startC))
            res.add(c)
        return res
    }
}