package priv.alex.parser.engine.lr

import org.jgrapht.Graph
import priv.alex.ast.AST
import priv.alex.ast.ASTNode
import priv.alex.lexer.token.Token
import priv.alex.lexer.token.TokenFile
import priv.alex.logger.Logger
import priv.alex.parser.EOF
import priv.alex.parser.NonTerminator
import priv.alex.parser.Production
import priv.alex.parser.Symbol
import priv.alex.parser.engine.cc.CanonicalCluster
import priv.alex.parser.engine.cc.CanonicalClusterEdge
import java.util.*

/**
 * Lr analyzer
 *
 * @property acceptProduction The production that can be accpeted
 * @constructor
 *
 * @param productions production list
 * @param cc CanonicalCluster graph
 */
@Logger
class LRAnalyzer(
    productions: Set<Production>,
    cc: Graph<CanonicalCluster, CanonicalClusterEdge>,
    private val acceptProduction: Production
) {

    private val analyseStack = ArrayDeque<Int>()

    //最右推导逆过程
    private val astStack = ArrayDeque<ASTNode>()
    private val symbolStack = ArrayDeque<Pair<Symbol, Token?>>()
    private val productionMap: HashMap<Int, Production>
    val analyseTable: LRTable

    init {
        val builder = LRAnalyzerBuilder(productions, acceptProduction)
        productionMap = builder.productionMap
        analyseTable = builder.build(cc)
    }

    /**
     * Analyze
     *
     * @param tokenFile The file of Token
     * @return AST of input
     */
    fun analyze(tokenFile: TokenFile): AST {
        log.info("Parser ${tokenFile.fileName}")
        analyseStack.push(0)
        symbolStack.push(Pair(EOF(), null))
        val ast = AST(tokenFile.fileName)
        tokenFile.tokens.forEach {
            var index = 0
            val token = ArrayList(it.tokens)
            while (index < it.tokens.size) {
                val action = analyseTable.action(analyseStack.peek(), token[index]) ?: let {
                    log.error("The parser cannot understand this token, please check whether the lexical used matches the syntax")
                    log.debug("Analyze Stack roof is ${analyseStack.peek()},token is ${token[index]}")
                    throw RuntimeException("Incomprehensible tokens")
                }
                if (action.first.action == Action.SHIFT) {
                    analyseStack.push(action.first.actionTarget)
                    symbolStack.push(Pair(action.second, token[index]))
                    astStack.push(ASTNode(Pair(action.second, token[index])))
                    index++
                } else {
                    reduce(action, ast)
                }
            }
        }
        var lastAction = analyseTable.action(analyseStack.peek(), null) ?: let {
            log.error("The current syntax cannot parse this token sequence")
            throw RuntimeException("Syntax mismatch")
        }
        while (lastAction.first.action != Action.ACCEPT) {
            reduce(lastAction, ast)
            lastAction = analyseTable.action(analyseStack.peek(), null) ?: let {
                log.error("The current syntax cannot parse this token sequence")
                throw RuntimeException("Syntax mismatch")
            }
        }
        if (lastAction.first.action == Action.ACCEPT) {
            log.info("${tokenFile.fileName} -> Accept")
            analyseStack.clear()
            symbolStack.clear()
            astStack.clear()
        } else {
            log.info("The current syntax cannot parse this token sequence")
            throw RuntimeException("Syntax  mismatch")
        }
        //清空分析栈
        return ast
    }

    private fun reduce(action: Pair<LRAction, Symbol>, ast: AST) {
        val production = productionMap[action.first.actionTarget]!!
        if (production.head.content != NonTerminator("<EMPTY>")) {
            val node = ASTNode(Pair(production.head.content, null))
            ast.addChild(null, node)
            if (production.body.content.all { it != NonTerminator("<EMPTY>") }) {
                val t = ArrayDeque<ASTNode>()
                production.body.content.forEach { _ ->
                    analyseStack.pop()
                    symbolStack.pop()
                    t.push(astStack.pop())
                }
                t.forEach {
                    ast.addChild(node, it)
                }
            }
            symbolStack.push(Pair(production.head.content, null))
            astStack.push(node)
        }
        if (production.body.content.all { it == NonTerminator("<EMPTY>") })
            analyseStack.pop()
        if (production != acceptProduction) {
            val goto = analyseTable.goto(analyseStack.peek(), production.head.content) ?: let {
                log.error("The GOTO target cannot be obtained through the current generation during the reduce process")
                throw RuntimeException("Unattainable GOTO target")
            }
            analyseStack.push(goto)
        }
    }

}