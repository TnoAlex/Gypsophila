package priv.alex.parser

data class ProductionBody(val content: List<Symbol>) : Cloneable{

    private var projectPos = 0
    var endProject = false
        private set

    fun advance(): Symbol {
        return if (projectPos == content.lastIndex){
            endProject = true
            content[projectPos]
        }else{
            projectPos++
            content[projectPos]
        }
    }
    override fun clone(): ProductionBody =  ProductionBody(content)

}
