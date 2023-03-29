package priv.alex.parser.engine.cc

/**
 * CanonicalCluster
 *
 * @property item The Canonical Cluster Item of Canonical Cluster
 * @property ccId The id of Canonical Cluster
 * @constructor Create  Canonical Cluster
 */
data class CanonicalCluster(val item: Set<CanonicalClusterItem>, val ccId: Int) : Iterable<CanonicalClusterItem>,
    Cloneable {

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other.javaClass != javaClass) return false
        other as CanonicalCluster
        if (other.item.size != item.size) return false
        if (!other.item.containsAll(item)) return false
        return true
    }

    override fun hashCode(): Int {
        return item.toString().hashCode()
    }

    override fun iterator(): Iterator<CanonicalClusterItem> {
        return item.iterator()
    }

    public override fun clone(): CanonicalCluster {
        val i = HashSet<CanonicalClusterItem>()
        item.forEach { i.add(it.clone()) }
        return CanonicalCluster(i, ccId)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(ccId).append("\n")
        item.forEach {
            builder.append(it).append("\n")
        }
        builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }
}