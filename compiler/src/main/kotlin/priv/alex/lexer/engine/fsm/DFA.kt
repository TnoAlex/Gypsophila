package priv.alex.lexer.engine.fsm

import org.jgrapht.Graph
import priv.alex.logger.Logger
import priv.alex.noarg.NoArg
import java.io.Serializable
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties

@NoArg
@Logger
class DFA(val pattern: String) : Cloneable, Serializable {


    private val dfa: Graph<Int, RegexEdge>
    private val startPoint = 0
    private val endPoint: HashSet<Int>


    init {
        val nfa = NFA(pattern)
        val builder = DFABuilder(nfa)
        dfa = builder.build()
        endPoint = builder.endPoint
    }


    fun match(str: String): Boolean {
        var currentNode = startPoint
        str.forEach {c->
            val edge = dfa.outgoingEdgesOf(currentNode)
            edge.filter { it.match(c) }.let {
                if (it.isEmpty()) return false
                else{
                    currentNode = it.first().target
                }
            }
        }
        return endPoint.contains(currentNode)
    }

    override fun clone(): DFA {
        val propMap = HashMap<String, Any>()
        propMap[this::dfa.name] = dfa
        propMap[this::startPoint.name] = startPoint
        propMap[this::endPoint.name] = endPoint
        propMap[this::pattern.name] = pattern
        val clazz = DFA::class
        val newInstant = clazz.createInstance()
        clazz.declaredMemberProperties.forEach {
            (it as KMutableProperty1<DFA, Any?>).apply {
                set(newInstant, propMap[it.name])
            }
        }
        return newInstant
    }


}