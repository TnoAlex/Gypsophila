package priv.alex.parser

open class ProductionBody(val content: List<Symbol>) : Cloneable {

    var projectState = ProjectState.SHIFT
    var projectPos = 0
    var endProject = false

    fun advance(): Symbol {
        return if (projectPos == content.size) {
            endProject = true
            projectState = ProjectState.REDUCE
            EOF()
        } else {
            projectPos++
            if (projectPos == content.size) {
                endProject = true
                projectState = ProjectState.REDUCE
            }
            content[projectPos - 1]
        }
    }

    fun current(): Symbol {
        return if (projectPos == 0)
            content[0]
        else if (endProject)
            EOF()
        else
            content[projectPos]
    }

    fun currentNext(): Symbol? {
        return if (projectPos + 1 >= content.size)
            null
        else {
            content[projectPos + 1]
        }
    }

    public override fun clone(): ProductionBody {
        val res = ProductionBody(content)
        res.projectPos = projectPos
        res.endProject = endProject
        res.projectState = projectState
        return res
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        other as ProductionBody
        if (other.content.size != content.size) return false
        if (!other.content.containsAll(content)) return false
        if (other.projectPos != projectPos) return false
        return true
    }

    override fun hashCode(): Int {
        var result = content.hashCode()
        result = 31 * result + projectPos
        result = 31 * result + endProject.hashCode()
        return result
    }

    override fun toString(): String {
        val builder = StringBuilder()
        content.forEach {
            builder.append(it).append(" ")
        }
        builder.removeSuffix(" ")
        return builder.toString()
    }

    fun initState(): ProductionBody {
        val res = clone()
        res.projectPos = 0
        if (content.all { it == EmptySymbol() }) {
            res.projectState = ProjectState.REDUCE
            res.endProject = true
            res.projectPos = content.size
        } else {
            res.projectState = ProjectState.SHIFT
            res.endProject = false
        }

        return res
    }


}
