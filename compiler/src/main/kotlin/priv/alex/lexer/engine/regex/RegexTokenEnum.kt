package priv.alex.lexer.engine.regex

enum class RegexTokenEnum (val value: String) {
    L(" "),
    ANY("."),
    AT_BOL("^"),
    AT_EOL("$"),
    CCL_START("["),
    CCL_END("]"),
    OPEN_CURLY("{"),
    CLOSE_CURLY("}"),
    OPEN_PAREN("("),
    CLOSE_PAREN(")"),
    CLOSURE("*"),
    DASH("-"),
    OPTIONAL("?"),
    OR("|"),
    PLUS_CLOSE("+"),
    CUTOFF("!"),
    EOF("EOF")
}
