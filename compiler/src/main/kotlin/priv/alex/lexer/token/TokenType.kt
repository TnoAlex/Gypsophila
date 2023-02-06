package priv.alex.lexer.token

import priv.alex.logger.Logger

@Logger
enum class TokenType {
    KEYWORDS,
    IDENTIFIER,
    OPERATOR,
    LITERAL,
    SEPARATOR,
    QUALIFIER;
    companion object {
        fun valuesOf(str: String): TokenType {
            return when (str) {
                "Keywords" -> KEYWORDS
                "Identifier" -> IDENTIFIER
                "Operator" -> OPERATOR
                "Literal" -> LITERAL
                "Qualifier" -> QUALIFIER
                "Separator" -> SEPARATOR
                else-> {
                    log.info("Incomprehensible Token")
                    throw RuntimeException("Incomprehensible Token")
                }
            }
        }
    }
}