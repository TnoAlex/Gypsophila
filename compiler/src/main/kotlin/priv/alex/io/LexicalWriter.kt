package priv.alex.io

import priv.alex.lexer.token.TokenFile
import priv.alex.logger.Logger
import java.io.File
import java.nio.ByteBuffer
import kotlin.math.ceil

@Logger
class LexicalWriter {
    fun writeTo(file: File, tokenFile: TokenFile) {
        try {
            val outputFile = File(file.path + File.separator + tokenFile.fileName)
            if (!outputFile.exists())
                outputFile.createNewFile()
            val fileChannel = outputFile.outputStream().channel
            val outputBuffer = ByteBuffer.allocate(OUTPUT_BUFFER_SIZE)
            var index = 0
            val lines = tokenFile.tokens
            while (index < lines.size){
                val bytes = lines[index].toString().toByteArray(CHARSET)
                if (bytes.size <= OUTPUT_BUFFER_SIZE){
                    outputBuffer.put(bytes)
                    outputBuffer.flip()
                    fileChannel.write(outputBuffer)
                    outputBuffer.clear()
                    index++
                }
                else{
                    val t = ceil(bytes.size/ OUTPUT_BUFFER_SIZE.toDouble()).toInt()
                    for (i in 0 until t){
                        outputBuffer.put(bytes,i* OUTPUT_BUFFER_SIZE, OUTPUT_BUFFER_SIZE)
                        outputBuffer.flip()
                        fileChannel.write(outputBuffer)
                        outputBuffer.clear()
                    }
                    index++
                }
            }
            fileChannel.close()
        } catch (e: Exception) {
            log.error("An irreversible error occurred during the output process")
            throw RuntimeException(e.message, e.cause)
        }

    }
    companion object{
        private const val OUTPUT_BUFFER_SIZE = 128
        private val CHARSET = Charsets.UTF_8
    }
}