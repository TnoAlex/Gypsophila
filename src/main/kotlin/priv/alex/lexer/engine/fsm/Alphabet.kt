package priv.alex.lexer.engine.fsm

class Alphabet(set: Set<Char>?, char: Char?, iv: Boolean?) {
    private var cSet: Set<Char>? = null
    private var cChar: Char? = null
    private var invert: Boolean? = null

    init {
        if (char != null)
            cChar = char
        else {
            cSet = set
            invert = iv
        }
    }

    fun match(edge: RegexEdge): Boolean {
        return if(edge.cChar!=null){
            edge.cChar == cChar
        } else
            edge.cSet == cSet && edge.invert == invert
    }
}