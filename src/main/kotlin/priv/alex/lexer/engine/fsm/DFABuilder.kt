package priv.alex.lexer.engine.fsm

import org.jgrapht.Graph


internal class DFABuilder(private val nfa: NFA) : FSMBuilder() {

    private val nodeMap = HashMap<Set<Int>, Int>()
    val endPoint = HashSet<Int>()
    private var startPoint = 0

    init {
        addNode()
    }

    override fun build(): Graph<Int, RegexEdge> {
        deterministic()
        genEndPoint()
        minimize()
        genEndPoint()
        reachable()
        return graph
    }

    private fun genEndPoint() {
        nodeMap.filter { it.key.contains(nfa.endPoint) }.forEach { (_, v) -> endPoint.add(v) }
    }


    private fun deterministic(flag: Boolean = false) {
        val stateSet = HashMap<Set<Int>, Boolean>()

        val rawStatus:MutableList<HashSet<Int>> = if (flag){
            val oldDFA = clone()
            clear()
            addNode()
            mutableListOf(nfa.epsilonClosure(setOf(startPoint),oldDFA))
        }

        else
            mutableListOf(nfa.epsilonClosure(setOf(startPoint)))

        var moveStatus = HashMap<Alphabet, MutableSet<Int>>()
        stateSet[rawStatus.first()] = false
        nodeMap[rawStatus.first()] = currentNode
        var baseNode = 0

        while (stateSet.any { !it.value } || moveStatus.isNotEmpty()) {
            if (moveStatus.isEmpty()) {
                moveStatus = nfa.moveTo(rawStatus.first())
                baseNode = nodeMap[rawStatus.first()]!!
                stateSet[rawStatus.first()] = true
                rawStatus.removeAt(0)
            }

            var usingKey: Alphabet? = null
            run block@{
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
                        graph.addEdge(baseNode, sourceNode, Alphabet.toEdge(k))
                    }
                    return@block
                }
            }
            moveStatus.remove(usingKey)
        }
    }

    private fun minimize() {
        reverse(endPoint)
        startPoint = currentNode
        endPoint.clear()
        deterministic(true)
        genEndPoint()
        reverse(endPoint)
        startPoint = currentNode
        endPoint.clear()
        deterministic(true)
    }

}