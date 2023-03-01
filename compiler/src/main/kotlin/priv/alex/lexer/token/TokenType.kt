package priv.alex.lexer.token

import priv.alex.logger.Logger

@Logger
enum class TokenType {
    KEYWORDS,
    IDENTIFIER,
    OPERATOR,
    LITERAL,
    SEPARATOR,
    COMMENT,
    QUALIFIER;
    companion object {
        fun enumOf(str: String): TokenType {
            return when (str) {
                "Keywords" -> KEYWORDS
                "Identifier" -> IDENTIFIER
                "Operator" -> OPERATOR
                "Literal" -> LITERAL
                "Qualifier" -> QUALIFIER
                "Separator" -> SEPARATOR
                "Comment" -> COMMENT
                else -> {
                    log.info("Incomprehensible Token")
                    throw RuntimeException("Incomprehensible Token")
                }
            }
        }

        fun stringOf(type: TokenType): String {
            return when (type) {
                KEYWORDS -> "Keywords"
                IDENTIFIER -> "Identifier"
                OPERATOR -> "Operator"
                LITERAL -> "Literal"
                QUALIFIER -> "Qualifier"
                SEPARATOR -> "Separator"
                COMMENT -> "Comment"
            }
        }
    }
}