package com.intfocus.template.general

/**
 * @author liuruilin
 * @data 2017/11/30
 * @describe 带优先级的 Runnable 接口
 */
abstract class PriorityRunnable(private var priority: Int) : Runnable, Comparable<PriorityRunnable> {
    init {
        if (priority < 0) {
            throw IllegalAccessException()
        }
    }

    override fun compareTo(other: PriorityRunnable): Int  = when {
        priority < other.priority -> 1
        priority > other.priority -> -1
        else -> 0
    }

    override fun run() {
        doSth()
    }

    abstract fun doSth()
}
