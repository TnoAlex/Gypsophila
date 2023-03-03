package priv.alex.parser.engine.lr

import org.jgrapht.Graph
import priv.alex.ast.AST
import priv.alex.ast.ASTNode
import priv.alex.lexer.token.Token
import priv.alex.lexer.token.TokenFile
import priv.alex.logger.Logger
import priv.alex.parser.Production
import priv.alex.parser.Symbol
import priv.alex.parser.engine.cc.CanonicalCluster
import priv.alex.parser.engine.cc.CanonicalClusterEdge
import java.util.*

@Logger
class LRAnalyzer(productions: Set<Production>, cc: Graph<CanonicalCluster, CanonicalClusterEdge>) {

    private val analyseStack = ArrayDeque<Int>()

    //最右推导逆过程
    private var astNode: ASTNode? = null
    private val symbolStack = ArrayDeque<Pair<Symbol, Token?>>()
    private val productionMap: HashMap<Int, Production>
    private val analyseTable: LRTable

    init {
        val builder = LRAnalyzerBuilder(productions)
        productionMap = builder.productionMap
        analyseTable = builder.build(cc)
    }

    fun analyze(tokenFile: TokenFile): AST {
        log.info("Parser ${tokenFile.fileName}")
        analyseStack.push(0)
        tokenFile.tokens.forEach {
            it.tokens.forEach { token ->
                val action = analyseTable.action(analyseStack.peek(), token) ?: let {
                    log.error("The parser cannot understand this token, please check whether the lexical used matches the syntax")
                    throw RuntimeException("Incomprehensible tokens")
                }
                if (action.first.action == Action.SHIFT) {
                    analyseStack.push(action.first.actionTarget)
                    symbolStack.push(Pair(action.second, token))
                }
                if (action.first.action == Action.REDUCE) {
                    reduce(action)
                }
            }
        }
        val lastAction = analyseTable.action(analyseStack.peek(), null) ?: let {
            log.error("The current grammar cannot parse this token sequence")
            throw RuntimeException("Grammar mismatch")
        }
        if (lastAction.first.action == Action.ACCEPT) {
            log.info("${tokenFile.fileName} -> Accept")
            analyseStack.clear()
            symbolStack.clear()
        } else {
            log.info("The current grammar cannot parse this token sequence")
            throw RuntimeException("Grammar mismatch")
        }
        //清空分析栈
        return AST(astNode!!)
    }

    private fun reduce(action: Pair<LRAction, Symbol>) {
        analyseStack.pop()
        val production = productionMap[action.first.actionTarget]!!
        val goto = analyseTable.goto(production.head.content) ?: let {
            log.error("The GOTO target cannot be obtained through the current generation during the reduce process")
            throw RuntimeException("Unattainable GOTO target")
        }
        analyseStack.push(goto)
        val popSymbol = ArrayDeque<Pair<Symbol, Token?>>()
        for (i in 0 until production.body.content.size) {
            popSymbol.push(symbolStack.pop())
        }
        val node = ASTNode(Pair(production.head.content, null))
        while (popSymbol.isNotEmpty()) {
            node.addChild(ASTNode(popSymbol.pop()))
        }
        astNode = if (astNode == null)
            node
        else {
            node.addChild(astNode!!)
            node
        }
        symbolStack.push(Pair(production.head.content, null))
    }

}