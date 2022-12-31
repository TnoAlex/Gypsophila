package priv.alex.lexer.engine.fsm

import org.jgrapht.graph.builder.GraphTypeBuilder

abstract class FSM {
    protected val automaton = GraphTypeBuilder.directed<String, StateTransitionCondition>().allowingMultipleEdges(true)
        .allowingSelfLoops(true).edgeClass(StateTransitionCondition::class.java).weighted(false).buildGraph()!!
}