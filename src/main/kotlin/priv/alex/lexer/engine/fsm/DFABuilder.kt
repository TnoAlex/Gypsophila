package priv.alex.lexer.engine.fsm

import org.jgrapht.Graph


class DFABuilder(nfa: NFA):FSMBuilder() {

    override fun build(): Graph<Int, RegexEdge> {
        return graph
    }


    private fun deterministic(){

    }
}