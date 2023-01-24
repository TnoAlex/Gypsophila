package priv.alex.lexer.engine.fsm

import org.jgrapht.Graph

class NFA(regex: String) {

    private val nfa: Graph<Int, RegexEdge>
    val endPoint: Int
    val startPoint: Int = 0

    init {
        val builder = NFABuilder(regex)
        nfa = builder.build()
        endPoint = nfa.edgeSet().last().target
    }

    fun epsilonClosure(status: Set<Int>): HashSet<Int> {
        val set = HashSet<Int>()
        status.forEach {
            val outEdge = nfa.outgoingEdgesOf(it)
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
        return set
    }

    fun moveTo(status: Set<Int>): HashMap<Alphabet, MutableSet<Int>> {
        val res = HashMap<Alphabet, MutableSet<Int>>()
        status.forEach {v->
            val edge = nfa.outgoingEdgesOf(v).filter { e -> !e.epsilon }
            edge.forEach{e->
                res[Alphabet(e)]?:res.put(Alphabet(e), mutableSetOf(v))?.add(v)
            }
        }
        return res
    }

}