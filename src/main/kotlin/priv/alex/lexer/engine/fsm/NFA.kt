package priv.alex.lexer.engine.fsm

import org.jgrapht.Graph

class NFA(regex: String) {

    val nfa: Graph<Int, RegexEdge>
    val endPoint: Int
    val startPoint: Int = 0
    private val alphabet: HashSet<Alphabet> = HashSet()

    init {
        val builder = NFABuilder(regex)
        nfa = builder.build()
        endPoint = nfa.edgeSet().last().target
        nfa.edgeSet().forEach {
            if (!it.epsilon)
                alphabet.add(Alphabet(it.cSet, it.cChar, it.invert))
        }
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
        status.forEach {
            val edge = nfa.outgoingEdgesOf(it).filter { e -> !e.epsilon }
            alphabet.forEach { a ->
                val vs = HashSet<Int>()
                edge.forEach { e ->
                    if (a.match(e))
                        vs.add(e.target)
                }
                if (vs.isNotEmpty()) {
                    res[a] ?: res.put(a, vs)?.addAll(vs)
                }
            }
        }
        return res
    }

}