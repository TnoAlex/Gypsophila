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

    fun match(edge: RegexEdge): Boolean {
        return if (edge.epsilon == this.epsilon)
            true
        else if (edge.cChar ==null && edge.cSet==null && cChar == null &&cSet ==null )
            true
        else if (edge.cChar != null) {
            edge.cChar == cChar
        } else
            edge.cSet == cSet && edge.invert == invert
    }
    companion object {
        fun toEdge(alphabet: Alphabet): RegexEdge {
            return RegexEdge(alphabet.cChar,alphabet.cSet,alphabet.invert,alphabet.epsilon)
        }

    }
}