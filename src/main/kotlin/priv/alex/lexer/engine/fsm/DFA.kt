package priv.alex.lexer.engine.fsm

class DFA {

    fun buildDFA(regex: String): DFA {
        val nfa = NFA(regex)
        return parserDFA(nfa)
    }

    private fun parserDFA(nfa: NFA): DFA {
        return DFA()
    }

}