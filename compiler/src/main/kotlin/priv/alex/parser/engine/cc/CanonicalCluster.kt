package priv.alex.parser.engine.cc

data class CanonicalCluster(val item: Set<CanonicalClusterItem>) : Iterable<CanonicalClusterItem> {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other.javaClass != javaClass) return false
        other as CanonicalCluster
        if (other.item != item) return false
        return true
    }

    override fun hashCode(): Int {
        return item.hashCode()
    }

    override fun iterator(): Iterator<CanonicalClusterItem> {
        return item.iterator()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        item.forEach {
            builder.append(it).append("\n")
        }
        builder.removeSuffix("\n")
        return builder.toString()
    }
}