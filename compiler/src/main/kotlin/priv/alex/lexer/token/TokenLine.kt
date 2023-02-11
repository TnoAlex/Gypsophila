package priv.alex.lexer.token


data class TokenLine(val position:Int,val tokens:List<Token>){
    override fun toString(): String {
        val sb = StringBuilder()
        tokens.forEach{
            sb.append(it.toString()).append(" ")
        }
        return "line $position ${sb.slice(0..sb.length-2)}${System.lineSeparator()}"
    }
}
