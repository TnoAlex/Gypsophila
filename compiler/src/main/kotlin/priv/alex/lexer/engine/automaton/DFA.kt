package priv.alex.lexer.engine.automaton

import org.jgrapht.Graph
import priv.alex.core.NoArg
import priv.alex.logger.Logger
import java.io.Serializable
import java.util.*
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties

@NoArg
@Logger
class DFA(val pattern: String) : Cloneable, Serializable {

    enum class DFAStatus{
        ACCEPT,
        REFUSE,
        INCOMPLETE
    }


    val dfa: Graph<Int, RegexEdge>
    private val startPoint = 0
    private val endPoint: Set<Int>


    init {
        val nfa = NFA(pattern)
        val builder = DFABuilder(nfa)
        dfa = builder.build()
        endPoint = Collections.unmodifiableSet(builder.endPoint)
    }


    fun match(str: String): DFAStatus {
        var currentNode = startPoint
        str.forEach {c->
            val edge = dfa.outgoingEdgesOf(currentNode)
            edge.filter { it.match(c) }.let {
                if (it.isEmpty()) return DFAStatus.REFUSE
                else{
                    // 优先匹配单字符
                    currentNode = if (it.none { e -> e.cChar != null })
                        it.first().target
                    else{
                        it.first{e-> e.cChar!=null}.target
                    }
                }
            }
        }
        return if (endPoint.contains(currentNode))
            DFAStatus.ACCEPT
        else
            DFAStatus.INCOMPLETE
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