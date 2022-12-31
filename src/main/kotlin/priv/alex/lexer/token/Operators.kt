package priv.alex.lexer.token

enum class Operators : TokenEnum {
    ADD {
        override val value: String
            get() = "+"
    },
    MINUS {
        override val value: String
            get() = "-"
    },
    MULTIPLY {
        override val value: String
            get() = "*"
    },
    DIVIDED {
        override val value: String
            get() = "/"
    },
    SAL {
        override val value: String
            get() = "<<"
    },
    SAR {
        override val value: String
            get() = ">>"
    },
    MOD {
        override val value: String
            get() = "%"
    },
    EQU {
        override val value: String
            get() = "=="
    },
    NEQ {
        override val value: String
            get() = "!="
    },
    GT {
        override val value: String
            get() = ">"
    },
    LT {
        override val value: String
            get() = "<"
    },
    GEQ {
        override val value: String
            get() = ">="
    },
    LEQ {
        override val value: String
            get() = "<="
    },
    ASSIGN {
        override val value: String
            get() = "="
    }
}