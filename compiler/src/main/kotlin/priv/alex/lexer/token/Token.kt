package priv.alex.lexer.token

data class Token(
    val type: TokenType,
    val rawText: String,
    val textRange: Pair<Int, Int>,
    val name: String
) {
    override fun toString(): String =
        "$type(rawText = $rawText,position = ${textRange.second})"
}