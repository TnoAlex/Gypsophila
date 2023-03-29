package priv.alex.lexer.engine.automaton

import org.jgrapht.Graph

/**
 * Nfa
 *
 * @constructor
 *
 * @param regex Regex string
 */
class NFA(regex: String) {

    private val nfa: Graph<Int, RegexEdge>
    val endPoint: Int
    val startPoint: Int = 0


    init {
        val builder = NFABuilder(regex)
        nfa = builder.build()
        endPoint = nfa.vertexSet().last()
    }

    /**
     * Epsilon closure
     *
     * @param status set of nfa status
     * @return The state that an empty closure can reach
     */
    fun epsilonClosure(status: Set<Int>): HashSet<Int> {
        val set = HashSet<Int>()
        status.forEach {
            val outEdge = nfa.outgoingEdgesOf(it).toMutableSet()
            while (true) {
                val vertex = HashSet<Int>()
                outEdge.forEach { out ->
                    if (out.epsilon) {
                        vertex.add(out.target)
                    }
                }
                if (set.containsAll(vertex))
                    break
                else {
                    outEdge.clear()
                    val edge = HashSet<RegexEdge>()
                    vertex.forEach { v ->
                        edge.addAll(nfa.outgoingEdgesOf(v))
                    }
                    outEdge.addAll(edge)
                    set.addAll(vertex)
                }
            }
        }
        set.addAll(status)
        return set
    }

    /**
     * Move to
     *
     * @param status the set of dfa
     * @return The DFA in this group state can transition the state
     */
    fun moveTo(status: Set<Int>): HashMap<Alphabet, MutableSet<Int>> {
        val res = HashMap<Alphabet, MutableSet<Int>>()
        status.forEach { v ->
            val edge = nfa.outgoingEdgesOf(v).filter { e -> !e.epsilon }
            edge.forEach { e ->
                res[Alphabet(e)] ?: res.put(Alphabet(e), mutableSetOf(e.target))?.add(e.target)
            }
        }
        return res
    }

}