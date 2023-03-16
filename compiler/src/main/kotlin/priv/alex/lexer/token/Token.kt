package priv.alex.lexer.token

import priv.alex.core.NoArg
import java.io.Serializable

@NoArg
data class Token(
    val type: TokenType,
    val rawText: String,
    val textRange: Pair<Int, Int>,
    val name: String
) : Serializable {
    override fun toString(): String =
        "$type(rawText = $rawText,position = ${textRange.second})"
}