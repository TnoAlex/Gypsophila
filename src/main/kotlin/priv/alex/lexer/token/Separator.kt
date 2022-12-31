package priv.alex.lexer.token

enum class Separator : TokenEnum {
    DQM{
        override val value: String
            get() ="\""
       },
    SQM{
        override val value: String
            get() = "\'"
    },
    LBRACES{
        override val value: String
            get() = "{"
    },
    RBRACES{
        override val value: String
            get() = "}"
    },
    LPARENTHESES{
        override val value: String
            get() = "("
    },
    RPARENTHESES{
        override val value: String
            get() = ")"
    },
    SEMICOLON{
        override val value: String
            get() = ";"
    }
}