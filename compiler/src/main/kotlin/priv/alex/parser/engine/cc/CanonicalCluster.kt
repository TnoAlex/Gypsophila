package priv.alex.parser.engine.cc

data class CanonicalCluster(val item: Set<CanonicalClusterItem>) : Iterable<CanonicalClusterItem>, Cloneable {
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
        return CanonicalCluster(i)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        item.forEach {
            builder.append(it).append("\n")
        }
        builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }
}