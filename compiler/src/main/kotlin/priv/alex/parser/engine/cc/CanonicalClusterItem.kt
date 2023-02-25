package priv.alex.parser.engine.cc

import priv.alex.parser.Production
import priv.alex.parser.Symbol

data class CanonicalClusterItem(val production: Production, val sc: HashSet<Symbol>) {

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other.javaClass != javaClass) return false
        other as CanonicalClusterItem
        if (other.production != production || other.sc != sc) return false
        return true
    }

    override fun hashCode(): Int {
        var result = production.hashCode()
        result = 31 * result + sc.hashCode()
        return result
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(production.toString()).append(" ")
        sc.forEach {
            builder.append(it).append("/")
        }
        builder.removeSuffix("/")
        return builder.toString()
    }
}