package priv.alex.lexer.engine.fsm

import org.jgrapht.graph.DefaultEdge
import priv.alex.lexer.token.TokenEnum

class StateTransitionCondition(val condition: TokenEnum) : DefaultEdge()