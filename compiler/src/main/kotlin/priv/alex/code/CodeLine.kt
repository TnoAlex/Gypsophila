package priv.alex.code

data class CodeLine(val position: Int, val content: String) {
    val length: Int = content.length

    fun split(separator: ArrayList<String>): ArrayList<String> {
        val words = ArrayList<String>()
        val t = StringBuilder()
        var i = 0
        while (i < content.length) {
            if (content[i] == '.') {
                if (t.all { it in '0'..'9' }){
                    t.append('.')
                    i++
                }

            }
            if (content[i] == ' ' && t.isEmpty()){
                i++
            }
            if (content[i] == ' ' && t.isNotEmpty()){
                words.add(t.toString().trim())
                t.clear()
                i++
            }
            if (content[i].toString() in separator) {
                if (t.isNotBlank()) {
                    words.add(t.toString().trim())
                    t.clear()
                }
                if (i + 1 < content.length && asciiSeparator(content[i + 1])) {
                    if (content.slice(i..i + 1) in separator) {
                        words.add(content.slice(i..i + 1))
                        i++
                    }
                    else {
                        words.add(content.slice(i..i))
                    }
                } else {
                    words.add(content.slice(i..i))
                }

            } else {
                t.append(content[i])
            }
            i++
        }
        return words
    }

    private fun asciiSeparator(char: Char): Boolean {
        if (char < '0' || (char in ':'..'@') || (char in '['..'`') || char > 'z')
            return true
        return false
    }
}