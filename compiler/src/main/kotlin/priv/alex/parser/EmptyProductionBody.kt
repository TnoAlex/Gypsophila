package priv.alex.parser

class EmptyProductionBody : ProductionBody(listOf(EmptySymbol())) {
    init {
        projectState = ProjectState.REDUCE
        endProject = true
        projectPos = 1
    }

    override fun toString(): String {
        return ""
    }
}
