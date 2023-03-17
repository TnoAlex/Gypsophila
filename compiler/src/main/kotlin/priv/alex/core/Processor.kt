package priv.alex.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import priv.alex.code.CodeContainer
import priv.alex.io.LexicalReader
import priv.alex.io.ParserReader
import priv.alex.lexer.token.TokenBuilder
import priv.alex.logger.Logger
import priv.alex.parser.engine.cc.CanonicalClusterBuilder
import priv.alex.parser.engine.lr.LRAnalyzer
import java.io.File

@Logger
class Processor {
    init {
        log.info(
            "\n" +
                    "  ________                                 .__    .__.__          \n" +
                    " /  _____/___.__.______  __________ ______ |  |__ |__|  | _____   \n" +
                    "/   \\  __<   |  |\\____ \\/  ___/  _ \\\\____ \\|  |  \\|  |  | \\__  \\  \n" +
                    "\\    \\_\\  \\___  ||  |_> >___ (  <_> )  |_> >   Y  \\  |  |__/ __ \\_\n" +
                    " \\______  / ____||   __/____  >____/|   __/|___|  /__|____(____  /\n" +
                    "        \\/\\/     |__|       \\/      |__|        \\/             \\/ "
        )
    }

    private val codeContainer = CodeContainer(ProcessorGlobalConfig.sourceFile!!)
    private val lexer = LexicalReader(ProcessorGlobalConfig.lexerFile!!).readLexicon()
    private val syntax = ParserReader(ProcessorGlobalConfig.syntaxFile!!).readParser()
    private val tokenBuilder = TokenBuilder(lexer)

    fun run() {

        log.info("Build pipeline ...")
        val ccBuilder = CanonicalClusterBuilder(syntax.first, syntax.second)
        val cc = ccBuilder.build()
        val lrAnalyzer = LRAnalyzer(ccBuilder.productions.toSet(), cc, ccBuilder.acceptProduction)
        val outPutFilePath = ProcessorGlobalConfig.sourceFile!!.parent + File.separator + "build"
        if (!File(outPutFilePath).exists())
            File(outPutFilePath).mkdir()
        log.info("Done")
        runBlocking {
            val pipeLine = PipeLine(tokenBuilder, lrAnalyzer, outPutFilePath)
            var codeFile = codeContainer.advance()
            launch(Dispatchers.Default) {
                while (codeFile != null) {
                    pipeLine.process(codeFile!!)
                    codeFile = codeContainer.advance()
                }
            }
        }
    }

}

