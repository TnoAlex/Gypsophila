package priv.alex.lexer.engine.fsm

import org.jgrapht.Graph


internal class DFABuilder(private val nfa: NFA) : FSMBuilder() {

    val nodeMap = HashMap<Set<Int>, Int>()

    init {
        addNode()
    }

    override fun build(): Graph<Int, RegexEdge> {
        deterministic()
        return graph
    }


    private fun deterministic(){
        val stateSet = HashMap<Set<Int>, Boolean>()

        val rawStatus = mutableListOf(nfa.epsilonClosure(setOf(nfa.startPoint)))
        var moveStatus = HashMap<Alphabet, MutableSet<Int>>()
        stateSet[rawStatus.first()] = false
        nodeMap[rawStatus.first()] = currentNode
        var baseNode = 0

        while (stateSet.any { !it.value }) {
            if (moveStatus.isEmpty()) {
                moveStatus = nfa.moveTo(rawStatus.first())
                baseNode = nodeMap[rawStatus.first()]!!
                stateSet[rawStatus.first()] = true
                rawStatus.removeAt(0)
            }

            var usingKey: Alphabet? = null
            moveStatus.forEach { (k, v) ->
                usingKey = k
                val closure = nfa.epsilonClosure(v)
                if (stateSet.all { it.key != closure }) {
                    stateSet[closure] = false
                    addNode()
                    graph.addEdge(baseNode, currentNode, Alphabet.toEdge(k))
                    rawStatus.add(closure)
                    nodeMap[rawStatus.last()] = currentNode
                } else {
                    val sourceNode = nodeMap[closure]
                    if (!graph.edgeSet().contains(Alphabet.toEdge(k))) {
                        graph.addEdge(baseNode, sourceNode, Alphabet.toEdge(k))
                    } else
                        return@forEach
                }
                return@forEach
            }
            moveStatus.remove(usingKey)
        }
    }

}