package priv.alex.lexer.token

class Token(
    val type: TokenEnum,
    val rawText: String,
    val textRange: Pair<Int, Int>
)