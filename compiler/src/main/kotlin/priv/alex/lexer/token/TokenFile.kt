package priv.alex.lexer.token

import priv.alex.core.NoArg
import java.io.Serializable

@NoArg
data class TokenFile(val tokens: List<TokenLine>, var fileName: String) : Serializable {
    init {
        fileName = fileName.split(".").first() + ".tk"
    }
}
