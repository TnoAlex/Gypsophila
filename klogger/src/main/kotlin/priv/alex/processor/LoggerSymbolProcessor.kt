package priv.alex.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import priv.alex.logger.Logger
import kotlin.concurrent.thread


private val LOGGER_CLASS = requireNotNull(Logger::class.qualifiedName)

internal class LoggerSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        LoggerSymbolProcessor(environment)
}


internal class LoggerSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(LOGGER_CLASS)
        val ret = mutableListOf<KSAnnotated>()
        symbols.toList().forEach {
            if (!it.validate())
                ret.add(it)
            else
                it.accept(LoggerVisitor(environment), Unit)
        }
        return ret
    }
}


internal class LoggerVisitor(private val environment: SymbolProcessorEnvironment) : KSVisitorVoid() {


    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val packageName = classDeclaration.containingFile!!.packageName.asString()//获取这个类的包名
        val className = classDeclaration.simpleName.asString()//获取类名
        val companion = classDeclaration.declarations.filter { it.simpleName.asString() == "Companion" }.toList()
        thread {
            synchronized(environment.codeGenerator){
                val file = environment.codeGenerator.createNewFile(//创建新的文件(默认.kt)
                    Dependencies(
                        true,
                        classDeclaration.containingFile!!
                    ), packageName, "Extend\$Logger\$$className"
                )
                file.write("package $packageName\n\n".toByteArray())
                file.write("val ${className}.log : org.slf4j.Logger\n".toByteArray())
                file.write("\tget() = org.slf4j.LoggerFactory.getLogger($className::class.java) \n\n".toByteArray())
                if (companion.isNotEmpty()){
                    file.write("val ${className}.Companion.log : org.slf4j.Logger\n".toByteArray())
                    file.write("\tget() = org.slf4j.LoggerFactory.getLogger($className.Companion::class.java)".toByteArray())
                }

                file.close()
            }
        }
    }

}