package priv.alex.lexer.engine.fsm

import org.jgrapht.Graph
import org.jgrapht.graph.builder.GraphTypeBuilder
import priv.alex.lexer.engine.regex.RegexLexer
import priv.alex.lexer.engine.regex.RegexToken
import priv.alex.lexer.engine.regex.RegexTokenEnum.*


class NFABuilder(pattern: String) {

    private val graph =
        GraphTypeBuilder.directed<Int, RegexEdge>().allowingMultipleEdges(true).allowingSelfLoops(true).weighted(false)
            .buildGraph()!!

    private val lexer = RegexLexer(pattern)

    private var id = 0
    private var currentNode = 0

    init {
        addNode()
    }

    private fun addNode() {
        graph.addVertex(id)
        currentNode = id
        id += 1
    }


    /*
    group ::= ("(" expr ")")*
    expr ::= factor_conn ("|" factor_conn)*
    factor_conn ::= factor | factor factor*
    factor ::= (term | term ("*" | "+" | "?"))*
    term ::= char | "[" char "-" char "]" | .
     */


    /**
     * group ::= ("(" expr ")")*
     */

    fun build(): Graph<Int, RegexEdge> {
        group()
        return graph
    }

    private fun group() {
        var token = lexer.advance()
        var utilEnd: Int
        var nextStart: Int

        if (token.type == OPEN_PAREN) {
            utilEnd = expression().second
            token = lexer.currentToken
            if (token.type == CLOSE_PAREN)
                lexer.advance()
        } else
            utilEnd = expression().second

        while (true) {
            var utilRange: Pair<Int, Int>
            if (lexer.currentToken.type == OPEN_PAREN) {
                utilRange = expression()
                nextStart = utilRange.first
                graph.addEdge(utilEnd, nextStart, RegexEdge(true))
                utilEnd = utilRange.second
                token = lexer.advance()
                if (token.type == CLOSE_PAREN) {
                    lexer.advance()
                }
            } else if (token.type == EOF)
                return
            else {
                utilRange = expression()
                nextStart = utilRange.first
                graph.addEdge(utilEnd, nextStart,RegexEdge(true))
                utilEnd = utilRange.second
            }
        }
    }

    /**
     *  expr ::= factor_conn ("|" factor_conn)*
     */

    private fun expression(): Pair<Int, Int> {
        addNode()
        val crossPos = currentNode
        addNode()
        val utilStart = currentNode
        val bifurcate = currentNode
        graph.addEdge(currentNode - 2, bifurcate, RegexEdge(true))
        var pos = factorConnection()
        graph.addEdge(pos.second, crossPos, RegexEdge(true))
        val token = lexer.currentToken
        while (token.type == OR) {
            val nowPos = pos
            pos = factorConnection()
            graph.addEdge(bifurcate, nowPos.first, RegexEdge(true))
            graph.addEdge(pos.second, crossPos, RegexEdge(true))
        }
        return Pair(utilStart, crossPos)
    }

    /*
     *   factor_conn ::= factor | factor factor*
     *
     */
    private fun factorConnection(): Pair<Int, Int> {
        var utilsEnd = currentNode
        var utilStart = currentNode

        var token = lexer.advance()
        if (isConnectable(token)) {
            val pos = factor()
            utilStart = pos.first
            utilsEnd = pos.second
            token = lexer.advance()
        }
        while (isConnectable(token)) {
            utilsEnd = factor().second
            if (lexer.currentToken.type == CLOSE_PAREN)
                break
            token = lexer.advance()
        }
        return Pair(utilStart, utilsEnd)
    }

    /**
     *  factor ::= (term | term ("*" | "+" | "?"))*
     */
    private fun factor(): Pair<Int, Int> {
        var token: RegexToken
        do {
            term()
            token = lexer.advance()
        } while (finishSignChar(token))
        return when (token.type) {
            CLOSURE -> {
                matchStarClosure()
            }

            PLUS_CLOSE -> {
                matchPlusClosure()
            }

            OPTIONAL -> {
                matchQuestionClosure()
            }

            else -> {
                Pair(currentNode - 1, currentNode)
            }
        }
    }

    private fun finishSignChar(token: RegexToken): Boolean {
        val nc = listOf(CLOSURE, PLUS_CLOSE, OPTIONAL,CLOSE_PAREN,EOF)
        return !nc.contains(token.type)
    }

    private fun isConnectable(token: RegexToken): Boolean {
        val nc = listOf(OPEN_PAREN, CLOSE_PAREN, AT_EOL, EOF, CLOSURE, PLUS_CLOSE, CCL_END, AT_BOL, OR)
        return !nc.contains(token.type)
    }

    /**
     * term ::= char | "[" char "-" char "]" | .
     */
    private fun term() {
        val nextToken = lexer.currentToken
        if (nextToken.type == L) {
            matchSignChar(nextToken)
        } else if (nextToken.type == ANY) {
            matchAnySignChar()
        } else if (nextToken.type == CCL_START) {
            val next = lexer.advance()
            if (next.type == L) {
                matchSetChar(next)
            } else if (next.type == AT_BOL) {
                matchNiSetChar(lexer.advance())
            }
        }
    }

    /**
     * 匹配任意字符
     */
    private fun matchAnySignChar() {
        addNode()
        graph.addEdge(currentNode - 1, currentNode, RegexEdge(true))
        addNode()
        graph.addEdge(currentNode - 1, currentNode, RegexEdge())
    }

    /**
     * 匹配单个字符
     */
    private fun matchSignChar(token: RegexToken) {
        addNode()
        graph.addEdge(currentNode - 1, currentNode, RegexEdge(true))
        addNode()
        graph.addEdge(currentNode - 1, currentNode, RegexEdge(token.value, null))
    }

    /**
     * 匹配集合
     */
    private fun matchSetChar(token: RegexToken) {
        addNode()
        graph.addEdge(currentNode - 1, currentNode, RegexEdge(true))
        addNode()
        val cSet = handleDASH(token)
        graph.addEdge(currentNode - 1, currentNode, RegexEdge(null, cSet.toSet()))
    }

    /**
     * handle the case like [^a-z]
     */
    private fun matchNiSetChar(token: RegexToken) {
        addNode()
        graph.addEdge(currentNode - 1, currentNode, RegexEdge(true))
        addNode()
        val cSet = handleDASH(token)
        graph.addEdge(currentNode - 1, currentNode, RegexEdge(true, cSet.toSet()))
    }

    /**
     * 处理字符集合
     */
    private fun handleRange(startC: Char, endC: Char): ArrayList<Char> {
        val res = ArrayList<Char>()
        for (c in endC.downTo(startC))
            res.add(c)
        return res
    }

    /**
     * 处理字符集合
     */
    private fun handleDASH(token: RegexToken): Set<Char> {
        val cSet = ArrayList<Char>()
        var nextToken = token
        while (nextToken.type != CCL_END) {
            var chars = ArrayList<Char>()
            chars.add(nextToken.value)
            val oldToken = nextToken
            nextToken = lexer.advance()
            if (nextToken.type == DASH) {
                nextToken = lexer.advance()
                if (nextToken == RegexToken(CCL_END))
                    throw RuntimeException("Unexpected Regex Syntax")
                else {
                    chars = handleRange(oldToken.value, nextToken.value)
                    nextToken = lexer.advance()
                }
            }
            cSet.addAll(chars)
        }
        return cSet.toSet()
    }

    /**
     * 匹配*闭包
     * target > source
     *           _______E_______
     *          ⇣                ↑
     * |0|---->|1|---[0-9]---->|2|--E--->|3|
     * ↓                                  ↑
     * ----------------E-------------------
     */
    private fun matchStarClosure(): Pair<Int, Int> {
        val source = currentNode
        graph.addEdge(currentNode, currentNode - 1, RegexEdge(true))
        addNode()
        graph.addEdge(currentNode - 1, currentNode, RegexEdge(true))
        graph.addEdge(source - 2, currentNode, RegexEdge(true))
        return Pair(source - 2, currentNode)
    }

    /**
     * 匹配+闭包
     * target > source
     *           _______E_______
     *          ⇣                ↑
     * |0|---->|1|---[0-9]---->|2|---E-->|3|
     */
    private fun matchPlusClosure(): Pair<Int, Int> {
        val target = currentNode
        graph.addEdge(currentNode, currentNode - 1, RegexEdge(true))
        addNode()
        graph.addEdge(target, target + 1, RegexEdge(true))
        return Pair(target - 1, target + 1)
    }

    /**
     * 匹配?闭包
     * target > source
     * |0|---->|1|---[0-9]---->|2|---E-->|3|
     * ↓                                  ↑
     * ----------------E-------------------
     */
    private fun matchQuestionClosure(): Pair<Int, Int> {
        val source = currentNode
        addNode()
        graph.addEdge(currentNode - 1, currentNode, RegexEdge(true))
        graph.addEdge(source - 2, currentNode, RegexEdge(true))
        return Pair(source - 2, currentNode)
    }
}

