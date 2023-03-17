package priv.alex.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import priv.alex.ast.AST
import priv.alex.code.CodeFile
import priv.alex.io.AnalyzeTableWriter
import priv.alex.io.AstWriter
import priv.alex.io.LexicalWriter
import priv.alex.lexer.token.TokenBuilder
import priv.alex.lexer.token.TokenFile
import priv.alex.lexer.token.TokenLine
import priv.alex.logger.Logger
import priv.alex.parser.engine.lr.LRAnalyzer
import java.io.File

@Logger
class PipeLine(
    private val tokenBuilder: TokenBuilder,
    private val lrAnalyzer: LRAnalyzer,
    private val outPutFilePath: String
) {
    suspend fun process(codeFile: CodeFile) = coroutineScope {
        val tokenBuildTask = async(Dispatchers.Default) { tokenBuildTask(codeFile) }
        val writerTask = async(Dispatchers.IO) { tokenWriterTask(tokenBuildTask.await()) }
        val syntaxAnalyseTask = async(Dispatchers.Default) { syntaxAnalyseTask(writerTask.await()) }
        val astWriterTask = async(Dispatchers.IO) { astWriterTask(syntaxAnalyseTask.await()) }
        astWriterTask.await()
    }

    private fun tokenBuildTask(codeFile: CodeFile): TokenFile {
        val tokenLines = ArrayList<TokenLine>(codeFile.lines.size)
        codeFile.lines.forEach {
            tokenLines.add(tokenBuilder.buildToken(it))
        }
        return TokenFile(tokenLines, codeFile.fileName)
    }

    private fun tokenWriterTask(tokenFile: TokenFile): TokenFile {
        if (ProcessorGlobalConfig.tokenOutput) {
            val writer = LexicalWriter()
            try {
                writer.writeTo(File(outPutFilePath + File.separator + tokenFile.fileName), tokenFile)
            } catch (e: RuntimeException) {
                log.error("It is not possible to write out the token sequence -> ${tokenFile.fileName}")
            }
        }
        if (ProcessorGlobalConfig.analyticsTableOutput) {
            val writer = AnalyzeTableWriter()
            try {
                writer.writeTo(File(outPutFilePath + File.separator + "analyseTable.json"), lrAnalyzer.analyseTable)
            } catch (e: RuntimeException) {
                log.error("It is not possible to write out the analyse table -> analyseTable.json")
            }
        }
        return tokenFile
    }

    private fun syntaxAnalyseTask(tokenFile: TokenFile): AST {
        return lrAnalyzer.analyze(tokenFile)
    }

    private fun astWriterTask(ast: AST) {
        val writer = AstWriter()
        try {
            writer.writeTo(File(outPutFilePath + File.separator + ast.buildBy.split(".")[0] + ".ast"), ast)
        } catch (e: RuntimeException) {
            log.error("It is not possible to write out the ast -> ${ast.buildBy}")
        }
    }
}
