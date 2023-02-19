package priv.alex.lexer.token

import priv.alex.code.CodeLine
import priv.alex.lexer.Lexical
import priv.alex.lexer.engine.automaton.DFA
import priv.alex.logger.Logger
import java.util.*

@Logger
class TokenBuilder(lexicons: HashMap<TokenType, ArrayList<Lexical>>) {

    private val keywords = lexicons[TokenType.KEYWORDS]!!
    private val identifier = lexicons[TokenType.IDENTIFIER]!!
    private val operator = lexicons[TokenType.OPERATOR]!!.map { it.value!! }
    private val literal = lexicons[TokenType.LITERAL]!!
    private val qualifier = lexicons[TokenType.QUALIFIER]!!
    private val separator = lexicons[TokenType.SEPARATOR]!!.map { it.value!! }
    private val comment = lexicons[TokenType.COMMENT]!!

    private val separators: ArrayList<String> = (operator + separator) as ArrayList<String>

    fun buildToken(line: CodeLine): TokenLine {
        val words = line.split(separators)
        var index = 0
        val res = ArrayList<Token>()
        while (index < words.size) {
            var t = words[index]
            if (words[index] in separators) {
                if (index + 1 < words.size) {
                    var tindex = index + 1
                    t += words[tindex]
                    var token = classification(t, true)
                    while (token.first && token.second == null && tindex< words.size){
                        tindex++
                        t += words[tindex]
                        token = classification(t,true)
                    }
                    if (token.second == null){
                        if (words[index] in operator) {
                            res.add(Token(TokenType.OPERATOR, words[index], Pair(line.position, index)))
                        } else if (words[index] in separators) {
                            res.add(Token(TokenType.SEPARATOR, words[index], Pair(line.position, index)))
                        }
                    }
                    else{
                        res.add(Token(token.second!!,t, Pair(line.position,index)))
                        index = tindex
                    }

                } else {
                    if (words[index] in operator) {
                        res.add(Token(TokenType.OPERATOR, words[index], Pair(line.position, index)))
                    } else if (words[index] in separators) {
                        res.add(Token(TokenType.SEPARATOR, words[index], Pair(line.position, index)))
                    }
                }
            } else {
                val token = classification(words[index], false)
                if (!token.first) {
                    log.error("You have an error in your input;In line ${line.position},close to \'${words[index]}\'")
                    throw RuntimeException("Unrecognized symbol")
                } else {
                    res.add(Token(token.second!!, words[index], Pair(line.position, index)))
                }
            }
            index++
        }
        return TokenLine(line.position,Collections.unmodifiableList(res))
    }

    private fun classification(string: String, flag: Boolean): Pair<Boolean, TokenType?> {

        comment.forEach {
            val status = it.dfa!!.match(string)
            if (status == DFA.DFAStatus.ACCEPT)
                return Pair(true, TokenType.COMMENT)
            else if (status == DFA.DFAStatus.INCOMPLETE && flag)
                return Pair(true, null)
        }

        literal.forEach {
            val status = it.dfa!!.match(string)
            if (status == DFA.DFAStatus.ACCEPT)
                return Pair(true, TokenType.LITERAL)
            else if (status == DFA.DFAStatus.INCOMPLETE && flag)
                return Pair(true, null)

        }
        qualifier.forEach {
            val status = it.dfa!!.match(string)
            if (status == DFA.DFAStatus.ACCEPT) {
                return Pair(true, TokenType.QUALIFIER)
            } else if (status == DFA.DFAStatus.INCOMPLETE && flag)
                return Pair(true, null)
        }
        keywords.forEach {
            val status = it.dfa!!.match(string)
            if (status == DFA.DFAStatus.ACCEPT) {
                return Pair(true, TokenType.KEYWORDS)
            } else if (status == DFA.DFAStatus.INCOMPLETE && flag)
                return Pair(true, null)
        }
        identifier.forEach {
            val status = it.dfa!!.match(string)
            if (status == DFA.DFAStatus.ACCEPT) {
                return Pair(true, TokenType.IDENTIFIER)
            } else if (status == DFA.DFAStatus.INCOMPLETE && flag)
                return Pair(true, null)
        }
        return Pair(false, null)

    }
}