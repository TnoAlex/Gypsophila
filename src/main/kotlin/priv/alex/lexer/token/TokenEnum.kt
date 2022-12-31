package priv.alex.lexer.token

interface TokenEnum {
    val value: String
        get(){
            return this::class.simpleName.toString().lowercase()
        }
}