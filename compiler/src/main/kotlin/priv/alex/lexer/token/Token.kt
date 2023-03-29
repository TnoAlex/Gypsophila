package priv.alex.lexer.token

import priv.alex.core.NoArg
import java.io.Serializable

/**
 * Token
 *
 * @property type Token Type
 * @property rawText Token raw text
 * @property textRange raw text pos
 * @property name Token name
 * @constructor Create Token
 */
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