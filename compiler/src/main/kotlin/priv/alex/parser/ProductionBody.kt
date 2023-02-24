package priv.alex.parser

data class ProductionBody(val content: List<Symbol>) : Cloneable{

    private var projectPos = 0
    private var currentPos = 0
    var endProject = false
        private set

    fun advance(): Symbol {
        return if (projectPos == content.size) {
            endProject = true
            content[projectPos]
        } else {
            currentPos = projectPos
            projectPos++
            content[currentPos]
        }
    }

    fun current(): Symbol {
        return content[currentPos]
    }

    fun stringEnd(): Boolean {
        if (projectPos >= content.lastIndex)
            return true
        return false
    }

    override fun clone(): ProductionBody = ProductionBody(content)

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        other as ProductionBody
        if (other.content != content) return false
        if (other.projectPos != projectPos) return false
        return true
    }

    override fun hashCode(): Int {
        var result = content.hashCode()
        result = 31 * result + projectPos
        result = 31 * result + endProject.hashCode()
        return result
    }
}
