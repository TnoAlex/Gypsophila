package priv.alex.lexer.engine.fsm

import org.jgrapht.Graph

class DFA(regex: String) {

    private val dfa: Graph<Int, RegexEdge>
    private val startPoint = 0
    private val endPoint:HashSet<Int>

    init {
        val nfa = NFA(regex)
        val builder = DFABuilder(nfa)
        dfa = builder.build()
        endPoint = builder.endPoint
    }



}