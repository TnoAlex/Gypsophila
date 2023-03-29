package priv.alex.io

import priv.alex.code.CodeLine
import priv.alex.logger.Logger
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * Code reader
 *
 * @property file The source file to be read into
 * @constructor Create Code reader
 */
@Logger
class CodeReader(private val file: File) : Reader {

    private var fc: FileChannel? = null
    private val buffer = ByteBuffer.allocate(BUFFER_SIZE)
    private var notTextFileFlag = false

    init {
        openChannel()
    }

    private fun openChannel() {
        if (!file.isFile) {
            log.error("Illegal documents")
            throw RuntimeException("Illegal documents")
        }
        try {
            fc = RandomAccessFile(file, "r").channel
        } catch (e: IOException) {
            log.error("Got File Channel Failed")
            throw RuntimeException(e.message, e.cause)
        }
    }

    /**
     * Read lines
     *
     * @return lines of source file
     */
    fun readLines(): ArrayList<CodeLine> {
        fc ?: openChannel()
        val lines = ArrayList<CodeLine>(FILE_LINE_SIZE)
        var pos = 0
        var incompleteLine = false
        var overflowBuffer = ByteArray(0)
        try {
            while (fc!!.read(buffer) != -1) {
                if (buffer.position() < BUFFER_SIZE) {
                    var tempBuffer: ByteArray
                    //缓冲未读满，默认为一行
                    if (!incompleteLine) {
                        tempBuffer = ByteArray(buffer.position())
                        buffer.flip()
                        buffer.get(tempBuffer)
                        checkBuffer(tempBuffer)
                        buffer.clear()
                        if (tempBuffer.contentEquals("\n".toByteArray()) || tempBuffer.contentEquals("\r".toByteArray())
                            || tempBuffer.contentEquals("\r\n".toByteArray())
                        )
                            continue
                    }
                    //缓冲未满，但是上次溢出
                    else {
                        incompleteLine = false
                        tempBuffer = overflowBuffer.copyOf(BUFFER_SIZE + buffer.position() + 1)
                        overflowBuffer = ByteArray(BUFFER_SIZE)
                        val t = ByteArray(buffer.position())
                        buffer.flip()
                        buffer.get(t)
                        checkBuffer(t)
                        buffer.clear()
                        if (t.first().toInt() == 10)
                            System.arraycopy(t, 1, tempBuffer, BUFFER_SIZE, t.size)
                        else
                            System.arraycopy(t, 0, tempBuffer, BUFFER_SIZE, t.size)
                    }
                    if (tempBuffer.contains(10) || tempBuffer.contains(13)) {
                        val res = handleMultiline(tempBuffer, pos)
                        lines.addAll(res.first)
                        pos = res.third
                        if (!res.second.all { it.toInt() == 0 }) {
                            incompleteLine = true
                            overflowBuffer = ByteArray(BUFFER_SIZE)
                            System.arraycopy(res.second, 0, overflowBuffer, 0, res.second.size)
                        }
                    } else {
                        lines.add(CodeLine(pos, trim(tempBuffer)))
                        pos++
                    }
                }
                //缓冲读满,不能确定
                else {
                    var tempBuffer = ByteArray(BUFFER_SIZE)
                    var copyPos = 0
                    if (incompleteLine) {
                        copyPos = overflowBuffer.indexOfFirst { it.toInt() == 0 }
                        if (copyPos == -1)
                            copyPos = overflowBuffer.size
                        tempBuffer = overflowBuffer.copyOf(copyPos + buffer.position() + 1)
                    }
                    val t = ByteArray(BUFFER_SIZE)
                    buffer.flip()
                    buffer.get(t)
                    checkBuffer(t)
                    buffer.clear()
                    incompleteLine = false
                    System.arraycopy(t, 0, tempBuffer, copyPos, t.size)
                    if (!tempBuffer.contains(10) && !tempBuffer.contains(13)) {
                        incompleteLine = true
                        overflowBuffer = ByteArray(tempBuffer.size)
                        System.arraycopy(tempBuffer, 0, overflowBuffer, 0, tempBuffer.size)
                    } else {
                        val res = handleMultiline(tempBuffer, pos)
                        lines.addAll(res.first)
                        pos = res.third
                        if (!res.second.all { it.toInt() == 0 }) {
                            incompleteLine = true
                            overflowBuffer = ByteArray(BUFFER_SIZE)
                            System.arraycopy(res.second, 0, overflowBuffer, 0, res.second.size)
                        }
                    }
                }
            }
            if (!overflowBuffer.all { it.toInt() == 0 }) {
                lines.add(CodeLine(lines.lastIndex + 1, String(overflowBuffer).trim()))
            }
        } catch (e: Exception) {
            log.error("An accident occurred during the read")
            throw RuntimeException(e.message, e.cause)
        }
        fc!!.close()
        fc = null
        return lines.filter { !it.content.toByteArray().all { b -> b.toInt() == 0 } } as ArrayList<CodeLine>
    }

    private fun handleMultiline(buffer: ByteArray, startPos: Int): Triple<ArrayList<CodeLine>, ByteArray, Int> {
        var pos = startPos
        val lines = ArrayList<CodeLine>()
        var t = ByteArray(BUFFER_SIZE)
        var f = false
        var tp = 0
        for (c in buffer.indices) {
            if (f) {
                f = false
                continue
            }
            val b = buffer[c].toInt()
            if (b == 10 || b == 13) {
                if (b == 13 && c + 1 < buffer.size && buffer[c + 1].toInt() == 10) f = true
                lines.add(CodeLine(pos, trim(t)))
                pos++
                t = ByteArray(BUFFER_SIZE)
                tp = 0
                continue
            } else {
                if (tp == t.lastIndex) {
                    t = t.copyOf(BUFFER_SIZE + tp + 1)
                }
                t[tp] = buffer[c]
                tp++
            }
        }
        return if (tp == 0)
            Triple(lines, ByteArray(0), pos)
        else
            Triple(lines, t.sliceArray(IntRange(0, tp - 1)), pos)
    }

    private fun checkBuffer(array: ByteArray) {
        if (!notTextFileFlag && array.contains(0)) {
            log.warn("${file.name} -> This file is most likely not a text file, and there is a high probability that something will go wrong next!")
            notTextFileFlag = true
        }
    }

    private fun trim(array: ByteArray): String {
        val newArray = ArrayList<Byte>()
        array.forEach {
            if (it.toInt() != 0)
                newArray.add(it)
            else
                return@forEach
        }
        return String(newArray.toByteArray(), DEFAULT_CHARSET).trim()
    }


    companion object {
        private const val BUFFER_SIZE = 512
        private const val FILE_LINE_SIZE = 32
        private val DEFAULT_CHARSET = Charsets.UTF_8
    }
}