package priv.alex.lexer.engine.fsm

import org.jgrapht.Graph
import priv.alex.lexer.engine.regex.RegexLexer
import priv.alex.lexer.engine.regex.RegexToken
import priv.alex.lexer.engine.regex.RegexTokenEnum.*


internal class NFABuilder(pattern: String) : FSMBuilder() {

    private val lexer = RegexLexer(pattern)

    init {
        addNode()
    }

    /*
    group ::= ("(" expr ")")*
    expr ::= factor_conn ("|" factor_conn)*
    factor_conn ::= factor | factor factor*
    factor ::= (term | term ("*" | "+" | "?"))*
    term ::= char | "[" char "-" char "]" | .
     */


    /**
     * group ::= ("(" expr ")"*|+|?|)*
     */

    override fun build(): Graph<Int, RegexEdge> {
        group()
        val unreachable = HashSet<Int>()
        for (v in graph.vertexSet()) {
            if (graph.inDegreeOf(v) == 0 && v != 0) {
                unreachable.add(v)
                val edge = graph.edgesOf(v).toMutableList()
                while (edge.isNotEmpty()) {
                    val e = edge.first()
                    if (graph.inDegreeOf(e.target) == 1) {
                        unreachable.add(e.target)
                        edge.addAll(graph.outgoingEdgesOf(e.target))
                    }
                    edge.removeAt(0)
                }
            }
        }
        unreachable.forEach {
            graph.removeVertex(it)
        }
        return graph
    }

    private fun group() {
        var token = lexer.advance()
        var utilEnd: Int
        var nextStart: Int
        var pos: Pair<Int, Int> = Pair(0, 0)

        if (token.type == OPEN_PAREN) {
            lexer.advance()
            pos = expression()
            utilEnd = pos.second
            token = lexer.currentToken
            if (token.type == CLOSE_PAREN)
                token = lexer.advance()
        } else {
            utilEnd = expression().second
            token = lexer.currentToken
        }

        while (true) {
            addNode()
            graph.addEdge(utilEnd, currentNode, RegexEdge(true))
            utilEnd = currentNode
            handleGroupClosure(pos)
            token = lexer.currentToken
            if (token.type == OPEN_PAREN) {
                lexer.advance()
                pos = expression()
                nextStart = pos.first
                graph.addEdge(utilEnd, nextStart, RegexEdge(true))
                utilEnd = pos.second
                token = lexer.advance()
                if (token.type == CLOSE_PAREN) {
                    token = lexer.advance()
                }
            } else if (token.type == EOF)
                return
            else {
                pos = expression()
                nextStart = pos.first
                graph.addEdge(utilEnd, nextStart, RegexEdge(true))
                utilEnd = pos.second
            }
        }
    }

    private fun handleGroupClosure(pos: Pair<Int, Int>) {
        val token = lexer.currentToken
        when (token.type) {
            PLUS_CLOSE -> {
                lexer.advance()
                graph.addEdge(pos.second, pos.first, RegexEdge(true))
            }

            CLOSURE -> {
                lexer.advance()
                graph.addEdge(graph.incomingEdgesOf(pos.first).first().source,graph.outgoingEdgesOf(pos.second).first().target, RegexEdge(true))
                graph.addEdge(pos.second, pos.first, RegexEdge(true))
            }

            OPTIONAL -> {
                lexer.advance()
                graph.addEdge(graph.incomingEdgesOf(pos.first).first().source,graph.outgoingEdgesOf(pos.second).first().target, RegexEdge(true))
            }

            else -> {
                return
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
        var token = lexer.currentToken
        while (token.type == OR) {
            addNode()
            lexer.advance()
            pos = factorConnection()
            graph.addEdge(bifurcate, pos.first, RegexEdge(true))
            graph.addEdge(pos.second, crossPos, RegexEdge(true))
            token = lexer.currentToken
        }
        return Pair(utilStart, crossPos)
    }

    /*
     *   factor_conn ::= factor | factor factor*
     *
     */
    private fun factorConnection(): Pair<Int, Int> {
        var utilsEnd = currentNode
        val utilStart = currentNode

        var token = lexer.currentToken
        if (isConnectable(token)) {
            val pos = factor()
            utilsEnd = pos.second
            token = lexer.currentToken
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
                lexer.advance()
                matchStarClosure()
            }

            PLUS_CLOSE -> {
                lexer.advance()
                matchPlusClosure()
            }

            OPTIONAL -> {
                lexer.advance()
                matchQuestionClosure()
            }

            else -> {
                Pair(currentNode - 1, currentNode)
            }
        }
    }

    private fun finishSignChar(token: RegexToken): Boolean {
        val nc = listOf(
            CLOSURE,
            PLUS_CLOSE,
            OPTIONAL,
            CLOSE_PAREN,
            EOF,
            OPEN_PAREN,
            AT_EOL,
            EOF,
            CLOSURE,
            PLUS_CLOSE,
            CCL_END,
            AT_BOL,
            OR
        )
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

