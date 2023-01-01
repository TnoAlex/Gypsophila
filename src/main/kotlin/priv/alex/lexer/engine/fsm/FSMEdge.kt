package priv.alex.lexer.engine.fsm

import org.jgrapht.graph.DefaultEdge

class FSMEdge(val cChar: Char?, val cSet: Set<Char>?): DefaultEdge(){

    var epsilon:Boolean = false

    constructor(epsilon: Boolean) : this(null,null){
        this.epsilon = epsilon
    }

    constructor():this(false)

}