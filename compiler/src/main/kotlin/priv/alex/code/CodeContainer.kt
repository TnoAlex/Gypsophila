package priv.alex.code

import priv.alex.logger.Logger
import java.io.File
import kotlin.system.exitProcess

@Logger
class CodeContainer(file: File) {

    private val codeFiles = ArrayList<CodeFile>()
    private var position = 0

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

    fun advance(): CodeFile? {
        return if (position <= codeFiles.size) {
            position++
            log.info("Processing ${codeFiles[position - 1].fileName} ...")
            codeFiles[position - 1]
        } else {
            log.info("Processing complete")
            null
        }
    }
}