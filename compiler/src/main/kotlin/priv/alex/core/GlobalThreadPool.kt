package priv.alex.core

import priv.alex.core.ProcessorGlobalConfig.isMultithreading
import priv.alex.core.ProcessorGlobalConfig.threadNumber
import priv.alex.logger.Logger
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.concurrent.thread
import kotlin.math.ceil

@Logger
class GlobalThreadPool private constructor() {

    var pool: ExecutorService? = null

    init {
        if (ProcessorGlobalConfig.isMultithreading) {
            pool = Executors.newFixedThreadPool(threadNumber)
            pool as ExecutorService
        }
    }

    fun runTask(task: () -> Any): ArrayList<Any> {
        val cutDown = CountDownLatch(threadNumber)
        val subTasks = ArrayList<Future<Any>>()
        for (i in 0 until threadNumber){
            pool!!.submit {  thread {
                task()
                cutDown.countDown()
            } }
        }
        try{
            cutDown.await()
        }catch (e:Exception){
            log.error("An irreversible error occurred in concurrent tasks")
            throw RuntimeException(e.message,e.cause)
        }
        val taskResult = ArrayList<Any>()
        subTasks.forEach {
            taskResult.add(it.get())
        }
        return taskResult

    }

    fun <T> runTask(task: (Any) -> Any, splittableArg: Collection<T>) : Any {
        if (isMultithreading){
            val subTaskSize = ceil(splittableArg.size/ threadNumber.toFloat()).toInt()
            val cutDown = CountDownLatch(threadNumber)
            val t = ArrayList<T>()
            val subTasks = ArrayList<Future<Any>>()
            var index = 0
            for (i in splittableArg.indices){
                if (index == subTaskSize){
                    index = 0
                    pool!!.submit {
                        thread {
                            task(t)
                            cutDown.countDown()
                        }
                    }
                    t.clear()
                }
                t.add(splittableArg.elementAt(i))
                index++
            }
            try{
                cutDown.await()
            }catch (e:Exception){
                log.error("An irreversible error occurred in concurrent tasks")
                throw RuntimeException(e.message,e.cause)
            }

            val taskResult = ArrayList<Any>()
            subTasks.forEach {
                taskResult.add(it.get())
            }
            return taskResult
        }else {
            return task(splittableArg)
        }
    }

    companion object {
        val INSTANCE by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GlobalThreadPool()
        }
    }
}
