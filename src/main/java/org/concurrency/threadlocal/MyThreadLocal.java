package org.concurrency.threadlocal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义ThreadLocal的实现
 *
 * @author kyan
 * @date 2019/5/13
 */
public class MyThreadLocal<T> {

    Map<Thread, T> locals = new ConcurrentHashMap<>();

    /**
     * 获取当前线程的本地变量
     */
    T get() {
        return locals.get(Thread.currentThread());
    }

    /**
     * 设置当前线程的本地变量
     * @param value
     */
    void set(T value) {
        locals.put(Thread.currentThread(), value);
    }
}
