package priv.alex.lexer.engine.fsm

import org.jgrapht.graph.DefaultEdge

class RegexEdge(val cChar: Char?, val cSet: Set<Char>?) : DefaultEdge() {

    var epsilon: Boolean = false
        private set
    var invert: Boolean = false
        private set

    constructor(epsilon: Boolean) : this(null, null) {
        this.epsilon = epsilon
    }

    constructor() : this(false)

    constructor(invert: Boolean, cSet: Set<Char>) : this(null, cSet) {
        this.invert = invert
    }

    public override fun getSource(): Int {
        return super.getSource() as Int
    }

    public override fun getTarget(): Int {
        return super.getTarget() as Int
    }
}