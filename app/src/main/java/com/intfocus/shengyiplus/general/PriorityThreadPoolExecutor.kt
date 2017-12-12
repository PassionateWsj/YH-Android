package com.intfocus.shengyiplus.general

import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

/**
 * @author liuruilin
 * @data 2017/11/30
 * @describe 带有开始/暂停功能的线程池
 */
class PriorityThreadPoolExecutor(
        corePoolSize: Int,
        maximumPoolSize: Int,
        keepAliveTime: Long,
        unit: TimeUnit,
        workQueue: PriorityBlockingQueue<Runnable>,
        factory: ThreadFactory
): ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, factory) {
    private var isPause = false
    private var pauseLock = ReentrantLock()
    private var unPaused = pauseLock.newCondition()

    /**
     * 线程池任务执行前调用
     */
    override fun beforeExecute(t: Thread?, r: Runnable?) {
        super.beforeExecute(t, r)
    }

    /**
     * 线程池任务执行完毕后调用
     */
    override fun afterExecute(r: Runnable?, t: Throwable?) {
        super.afterExecute(r, t)
    }

    fun pause() {
        pauseLock.lock()
        try {
            isPause = true
        }
        finally {
            pauseLock.unlock()
        }
    }

    fun resume() {
        pauseLock.lock()
        try {
            isPause = false
            unPaused.signalAll()
        }
        finally {
            pauseLock.unlock()
        }
    }

    /**
     * 线程池终止后调用
     */
    override fun terminated() {
        super.terminated()
    }
}
