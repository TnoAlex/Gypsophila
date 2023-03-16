package priv.alex.io

import java.io.File

interface Writer {
    fun writeTo(file: File, obj: Any)
}