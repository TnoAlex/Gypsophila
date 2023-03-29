package priv.alex.lexer.token

import priv.alex.code.CodeLine
import priv.alex.lexer.Lexical
import priv.alex.lexer.engine.automaton.DFA
import priv.alex.logger.Logger
import java.util.*

/**
 * Token builder
 *
 * @constructor
 *
 * @param lexicons Lexical collections
 */
@Logger
class TokenBuilder(lexicons: HashMap<TokenType, ArrayList<Lexical>>) {

    private val keywords = lexicons[TokenType.KEYWORDS]!!
    private val identifier = lexicons[TokenType.IDENTIFIER]!!
    private val operator = lexicons[TokenType.OPERATOR]!!.associate { it.value!! to it.name }
    private val literal = lexicons[TokenType.LITERAL]!!
    private val qualifier = lexicons[TokenType.QUALIFIER]!!
    private val separator = lexicons[TokenType.SEPARATOR]!!.associate { it.value!! to it.name }
    private val comment = lexicons[TokenType.COMMENT]!!

    private val separators: ArrayList<String> = (operator.keys.toList() + separator.keys.toList()) as ArrayList<String>

    /**
     * Build token
     *
     * @param line The line of source Code
     * @return Token Lines of source Code
     */
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
                    var token = classification(t)
                    while (token.first && token.second == null && tindex < words.size) {
                        tindex++
                        t += words[tindex]
                        token = classification(t)
                    }
                    if (token.second == null) {
                        if (words[index] in operator.keys) {
                            res.add(
                                Token(
                                    TokenType.OPERATOR,
                                    words[index],
                                    Pair(line.position, index),
                                    operator[words[index]]!!
                                )
                            )
                        } else if (words[index] in separator.keys) {
                            res.add(
                                Token(
                                    TokenType.SEPARATOR,
                                    words[index],
                                    Pair(line.position, index),
                                    separator[words[index]]!!
                                )
                            )
                        }
                    } else {
                        res.add(Token(token.second!!.first, t, Pair(line.position, index), token.second!!.second))
                        index = tindex
                    }

                } else {
                    if (words[index] in operator) {
                        res.add(
                            Token(
                                TokenType.OPERATOR,
                                words[index],
                                Pair(line.position, index),
                                operator[words[index]]!!
                            )
                        )
                    } else if (words[index] in separator) {
                        res.add(
                            Token(
                                TokenType.SEPARATOR,
                                words[index],
                                Pair(line.position, index),
                                separator[words[index]]!!
                            )
                        )
                    }
                }
            } else {
                val token = classification(words[index])
                if (!token.first) {
                    log.error("You have an error in your input;In line ${line.position},close to \'${words[index]}\'")
                    throw RuntimeException("Unrecognized symbol")
                } else {
                    var tt = words[index]
                    var tindex = index + 1
                    var ttoken = token
                    while ((token.second == null || token.second!!.first == TokenType.LITERAL) && tindex < words.size) {
                        tt += words[tindex]
                        ttoken = classification(tt)
                        tindex++
                        if (ttoken.first && ttoken.second != null)
                            break
                    }
                    if (!ttoken.first || ttoken.second == null || ttoken == token) {
                        res.add(
                            Token(
                                token.second!!.first,
                                words[index],
                                Pair(line.position, index),
                                token.second!!.second
                            )
                        )
                    } else {
                        res.add(
                            Token(
                                ttoken.second!!.first,
                                tt,
                                Pair(line.position, index),
                                ttoken.second!!.second
                            )
                        )
                        index = tindex - 1
                    }
                }
            }
            index++
        }
        return TokenLine(line.position, Collections.unmodifiableList(res))
    }

    private fun classification(string: String): Pair<Boolean, Pair<TokenType, String>?> {
        var state = false

        comment.forEach {
            val status = it.dfa!!.match(string)
            if (status == DFA.DFAStatus.ACCEPT)
                return Pair(true, Pair(TokenType.COMMENT, it.name))
            else if (status == DFA.DFAStatus.INCOMPLETE)
                state = true
        }

        literal.forEach {
            val status = it.dfa!!.match(string)
            if (status == DFA.DFAStatus.ACCEPT)
                return Pair(true, Pair(TokenType.LITERAL, it.name))
            else if (status == DFA.DFAStatus.INCOMPLETE)
                state = true

        }
        qualifier.forEach {
            val status = it.dfa!!.match(string)
            if (status == DFA.DFAStatus.ACCEPT) {
                return Pair(true, Pair(TokenType.QUALIFIER, it.name))
            } else if (status == DFA.DFAStatus.INCOMPLETE)
                state = true
        }
        keywords.forEach {
            val status = it.dfa!!.match(string)
            if (status == DFA.DFAStatus.ACCEPT) {
                return Pair(true, Pair(TokenType.KEYWORDS, it.name))
            } else if (status == DFA.DFAStatus.INCOMPLETE)
                state = true
        }
        identifier.forEach {
            val status = it.dfa!!.match(string)
            if (status == DFA.DFAStatus.ACCEPT) {
                return Pair(true, Pair(TokenType.IDENTIFIER, it.name))
            } else if (status == DFA.DFAStatus.INCOMPLETE)
                state = true
        }
        if (state)
            return Pair(true, null)
        return Pair(false, null)

    }
}