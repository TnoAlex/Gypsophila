package priv.alex.parser.engine.cc

data class CanonicalCluster(val item: List<CanonicalClusterItem>, val ccId: Int) {
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
}