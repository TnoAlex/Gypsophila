package priv.alex.lexer.token

data class TokenFile(val tokens: List<TokenLine>, var fileName: String) {
    init {
        fileName = fileName.split(".").first() + ".tk"
    }
}
