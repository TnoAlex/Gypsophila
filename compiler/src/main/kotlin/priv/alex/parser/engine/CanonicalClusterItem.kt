package priv.alex.parser.engine

import priv.alex.parser.Production
import priv.alex.parser.Symbol

data class CanonicalClusterItem(val production: Production, val sc:List<Symbol>)