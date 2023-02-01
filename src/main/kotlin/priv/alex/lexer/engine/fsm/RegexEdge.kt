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

    constructor(edge: RegexEdge) : this(edge.cChar, edge.cSet, edge.invert, edge.epsilon)

    constructor(cChar: Char?, cSet: Set<Char>?, invert: Boolean?, epsilon: Boolean) : this(cChar, cSet) {
        if (invert != null)
            this.invert = invert
        this.epsilon = epsilon
    }

    constructor(invert: Boolean, cSet: Set<Char>) : this(null, cSet) {
        this.invert = invert
    }

    fun match(c: Char): Boolean {
        return if (cChar != null && c == cChar)
            true
        else if (cSet != null && !invert && cSet.contains(c))
            true
        else cSet != null && invert && !cSet.contains(c)
    }


    public override fun getSource(): Int {
        return super.getSource() as Int
    }

    public override fun getTarget(): Int {
        return super.getTarget() as Int
    }

    fun sameCondition(other: RegexEdge): Boolean {
        if (cChar != other.cChar) return false
        if (cSet != other.cSet) return false
        if (epsilon != other.epsilon) return false
        if (invert != other.invert) return false
        return true
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RegexEdge

        if (cChar != other.cChar) return false
        if (cSet != other.cSet) return false
        if (epsilon != other.epsilon) return false
        if (invert != other.invert) return false
        if (source != other.source || target != other.target) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cChar?.hashCode() ?: 0
        result = 31 * result + (cSet?.hashCode() ?: 0)
        result = 31 * result + epsilon.hashCode()
        result = 31 * result + invert.hashCode()
        return result
    }


}