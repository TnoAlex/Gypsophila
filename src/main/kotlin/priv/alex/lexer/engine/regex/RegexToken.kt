package priv.alex.lexer.engine.regex


class RegexToken(val type: RegexTokenEnum) {

    var value = type.value[0]
        private set

    constructor(value: String) : this(RegexTokenEnum.L) {
        this.value = value[0]
    }


    companion object {
        fun getToken(char: String): RegexToken {
            for (token in RegexTokenEnum.values()) {
                if (token.value == char)
                    return RegexToken(token)
            }
            return RegexToken(char)
        }
    }
}