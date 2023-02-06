package priv.alex.lexer.token

import priv.alex.logger.Logger

@Logger
enum class TokenType {
    KEYWORDS,
    IDENTIFIER,
    OPERATOR,
    LITERAL,
    SPECIAL_CHAR;


    companion object {
        fun valueOf(str: String): TokenType {
            return when (str) {
                "Keywords" -> KEYWORDS
                "Identifier" -> IDENTIFIER
                "Operator" -> OPERATOR
                "Literal" -> LITERAL
                "Special Char" -> SPECIAL_CHAR
                else-> {
                    log.info("Incomprehensible Token")
                    throw RuntimeException("Incomprehensible Token")
                }
            }
        }
    }
}