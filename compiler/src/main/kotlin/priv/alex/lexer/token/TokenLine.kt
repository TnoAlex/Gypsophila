package priv.alex.lexer.token

import priv.alex.core.NoArg
import java.io.Serializable

/**
 * Token line
 *
 * @property position token line position
 * @property tokens the tokens of line
 * @constructor Create empty Token line
 */
@NoArg
data class TokenLine(val position: Int, val tokens: List<Token>) : Serializable {
    override fun toString(): String {
        val sb = StringBuilder()
        tokens.forEach {
            sb.append(it.toString()).append(" ")
        }
        return "line $position ${sb.slice(0..sb.length - 2)}${System.lineSeparator()}"
    }
}
