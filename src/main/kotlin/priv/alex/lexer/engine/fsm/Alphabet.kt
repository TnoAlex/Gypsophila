package priv.alex.lexer.engine.fsm

class Alphabet(edge: RegexEdge) {
    private var cSet: Set<Char>? = null
    private var cChar: Char? = null
    private var invert: Boolean? = null
    private var epsilon:Boolean = false

    init {
        cChar = edge.cChar
        cSet = edge.cSet
        invert = edge.invert
        epsilon = edge.epsilon
    }
    constructor():this(RegexEdge(false))

    companion object {
        fun toEdge(alphabet: Alphabet): RegexEdge {
            return RegexEdge(alphabet.cChar,alphabet.cSet,alphabet.invert,alphabet.epsilon)
        }

    }
}