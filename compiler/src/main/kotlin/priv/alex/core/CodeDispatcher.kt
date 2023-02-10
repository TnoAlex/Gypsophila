package priv.alex.core

import priv.alex.code.CodeFile
import priv.alex.logger.Logger
import java.io.File
import kotlin.system.exitProcess

@Logger
class CodeDispatcher(file: File) {

    private val codeFiles = ArrayList<CodeFile>()

    init {
        if (file.isDirectory){
            file.listFiles()?.forEach {
                if (it.canRead())
                    codeFiles.add(CodeFile(it))
                else
                    log.warn("${it.name} -> Insufficient permissions to read")
            }?:let {
                log.error("Empty Directory!")
                throw RuntimeException("Empty Directory")
            }
        }
        if (file.isFile){
            codeFiles.add(CodeFile(file))
        }
        if (codeFiles.isEmpty()){
            log.warn("No files were read")
            exitProcess(0)
        }
    }
}