package priv.alex.code

data class CodeLine(val position: Int, val content: String) {
    val length: Int = content.length
}