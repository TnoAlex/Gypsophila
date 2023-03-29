package priv.alex.parser

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Symbol deserializer
 *
 * @constructor Create Symbol deserializer
 */
class SymbolDeserializer:JsonDeserializer<Symbol> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Symbol {
        val jsonObject = json.asJsonObject
        val content = jsonObject.get("content").asString
        if (content.startsWith("<") && content.endsWith(">")){
            return Gson().fromJson(json,NonTerminator::class.java)
        }else {
            return Gson().fromJson(json,Terminator::class.java)
        }
    }
}