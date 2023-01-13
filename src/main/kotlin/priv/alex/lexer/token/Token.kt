package priv.alex.lexer.token

class Token(
    val type: String,
    val rawText: String,
    val textRange: Pair<Int, Int>,
    val factoryId: String,
    val tokenId: String
)