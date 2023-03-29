package priv.alex.parser

/**
 * Empty production body
 *
 * @constructor Create Empty production body
 */
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
