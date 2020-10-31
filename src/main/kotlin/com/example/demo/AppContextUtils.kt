package com.example.demo

import org.springframework.context.ApplicationContext

object AppContextUtils {
    private var _context: ApplicationContext? = null
    fun setContext(_context: ApplicationContext) = apply {
        this._context = _context
    }

    fun getContext(): ApplicationContext {
        if (this._context == null) throw Exception("application context load failed!")
        return this._context!!
    }

    fun invoker(
            beanName: String,
            methodName: String,
            parameterTypes: List<Class<*>> = listOf(),
            parameterArgs: List<Any?> = listOf(),
    ): Any? {
        val bean = getContext().getBean(beanName)
        val method = bean.javaClass.getMethod(methodName, *parameterTypes.toTypedArray())
        return method.invoke(bean, *parameterArgs.toTypedArray())
    }
}