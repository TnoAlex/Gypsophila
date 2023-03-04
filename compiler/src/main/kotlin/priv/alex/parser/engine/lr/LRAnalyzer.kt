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

@Logger
class LRAnalyzer(
    productions: Set<Production>,
    cc: Graph<CanonicalCluster, CanonicalClusterEdge>,
    acceptProduction: Production
) {

    private val analyseStack = ArrayDeque<Int>()

    //最右推导逆过程
    private var ast = AST()
    private val astStack = ArrayDeque<Pair<NonTerminator, ASTNode>>()
    private val symbolStack = ArrayDeque<Pair<Symbol, Token?>>()
    private val productionMap: HashMap<Int, Production>
    private val analyseTable: LRTable

    init {
        val builder = LRAnalyzerBuilder(productions, acceptProduction)
        productionMap = builder.productionMap
        analyseTable = builder.build(cc)
    }

    fun analyze(tokenFile: TokenFile): AST {
        log.info("Parser ${tokenFile.fileName}")
        analyseStack.push(0)
        symbolStack.push(Pair(EOF(), null))
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
            log.error("The current syntax cannot parse this token sequence")
            throw RuntimeException("Syntax mismatch")
        }
        if (lastAction.first.action == Action.ACCEPT) {
            log.info("${tokenFile.fileName} -> Accept")
            analyseStack.clear()
            symbolStack.clear()
        } else {
            log.info("The current syntax cannot parse this token sequence")
            throw RuntimeException("Syntax  mismatch")
        }
        //清空分析栈
        return ast
    }

    private fun reduce(action: Pair<LRAction, Symbol>) {
        analyseStack.pop()
        val production = productionMap[action.first.actionTarget]!!
        val goto = analyseTable.goto(analyseStack.peek(), production.head.content) ?: let {
            log.error("The GOTO target cannot be obtained through the current generation during the reduce process")
            throw RuntimeException("Unattainable GOTO target")
        }
        analyseStack.push(goto)
        val popSymbol = ArrayDeque<Pair<Symbol, Token?>>()
        for (i in 0 until production.body.content.size) {
            popSymbol.push(symbolStack.pop())
        }
        val node = ASTNode(Pair(production.head.content, null))
        ast.addChild(null, node)
        astStack.push(Pair(production.head.content, node))
        while (popSymbol.isNotEmpty()) {
            val symbol = popSymbol.pop()
            if (symbol.first is NonTerminator) {
                val child = astStack.firstOrNull { symbol.first == it.first } ?: let {
                    log.error("The syntax tree node cannot be mounted to the specified location")
                    throw RuntimeException("NullPointer exception")
                }
                ast.link(node, child.second)
            } else {
                ast.addChild(node, ASTNode(symbol))
            }
        }
        symbolStack.push(Pair(production.head.content, null))
    }

}