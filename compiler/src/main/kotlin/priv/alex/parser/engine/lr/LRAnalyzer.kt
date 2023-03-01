package priv.alex.parser.engine.lr

import org.jgrapht.Graph
import priv.alex.lexer.token.Token
import priv.alex.lexer.token.TokenFile
import priv.alex.logger.Logger
import priv.alex.parser.Production
import priv.alex.parser.engine.cc.CanonicalCluster
import priv.alex.parser.engine.cc.CanonicalClusterEdge
import java.util.*
import kotlin.collections.ArrayDeque

@Logger
class LRAnalyzer(productions: Set<Production>, cc: Graph<CanonicalCluster, CanonicalClusterEdge>) {

    private val analyseStack = Stack<Int>()
    private val astStack = ArrayDeque<Pair<Production, Token>>()
    private val productionMap: HashMap<Int, Production>
    private val analyseTable: LRTable

    init {
        val builder = LRAnalyzerBuilder(productions)
        productionMap = builder.productionMap
        analyseTable = builder.build(cc)
    }

    fun analyze(tokenFile: TokenFile): ArrayDeque<Pair<Production, Token>> {
        log.info("Parser ${tokenFile.fileName}")
        analyseStack.push(0)
        tokenFile.tokens.forEach {
            it.tokens.forEach { token ->
                val action = analyseTable.action(analyseStack.peek(), token) ?: let {
                    log.error("The parser cannot understand this token, please check whether the lexical used matches the syntax")
                    throw RuntimeException("Incomprehensible tokens")
                }
                if (action.action == Action.SHIFT)
                    analyseStack.push(action.actionTarget)
                if (action.action == Action.REDUCE) {
                    reduce(action, token)
                }
            }
        }
        val lastAction = analyseTable.action(analyseStack.peek(), null) ?: let {
            log.error("The current grammar cannot parse this token sequence")
            throw RuntimeException("Grammar mismatch")
        }
        if (lastAction.action == Action.ACCEPT) {
            log.info("${tokenFile.fileName} -> The parsing is complete")
            analyseStack.clear()
        }
        //清空分析栈
        val res = ArrayDeque(astStack)
        astStack.clear()
        return res
    }

    private fun reduce(action: LRAction, token: Token) {
        analyseStack.pop()
        val production = productionMap[action.actionTarget]!!
        val goto = analyseTable.goto(production.head.content) ?: let {
            log.error("The goto target cannot be obtained through the current generation during the reduce process")
            throw RuntimeException("Unattainable GOTO target")
        }
        analyseStack.push(goto)
        astStack.addLast(Pair(production, token))
    }

}