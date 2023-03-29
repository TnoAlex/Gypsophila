package priv.alex.lexer.token

import priv.alex.core.NoArg
import java.io.Serializable

/**
 * Token file
 *
 * @property tokens The tokens of Token file
 * @property fileName The token file name
 * @constructor Create Token file
 */
@NoArg
data class TokenFile(val tokens: List<TokenLine>, var fileName: String) : Serializable {
    init {
        fileName = fileName.split(".").first() + ".tk"
    }
}
