package priv.alex.lexer.token

enum class KeyWords : TokenEnum {
    EPSILON{
        override val value: String
            get() = "ϵ"
           },
    IF,
    ELSE,
    ELIF,
    FOR,
    WHILE,
    FUN,
    RETURN;
    enum class DataType:TokenEnum{
        INT,
        FLOAT,
        CHAR,
        STRING,
        BOOLEAN
    }
}