## Java线程的生命周期

### [线程的状态][1]

```java
public class Thread implements Runnable {
    
    public enum State {
            NEW,
            RUNNABLE,
            BLOCKED,
            WAITING,
            TIMED_WAITING,
            TERMINATED;
        }
}
```

| 状态名称 | 状态说明 |
| ------- | ------- |
| NEW | 初始状态，线程被构建，但还没有调用start()方法 |
| RUNNABLE | 可运行状态，表示当前线程可能正在运行，也有可能正在等待CPU时间片，Java线程系统将操作系统中的就绪（Ready）和运行（Running）两种状态统称为"可运行"|
| BLOCK | 阻塞状态，表示当前线程阻塞于锁 |
| WAITING | 无限期等待状态，表示当前线程进入等待状态，进入该状态的线程需要等待其他线程做出一些特定动作（通知或中断）|
| TIMED_WAITING | 有限期等待状态，无需等待其他线程做出特定动作，在一定时间期限后会被系统自动唤醒 |
| TERMINATED | 终止状态，表示当前线程执行完毕，可能是任务完成之后自己结束，也可能是产生异常而结束 |

*只要 Java 线程处于 BLOCKED、WAITING、TIMED_WAITING 这三种状态之一，那么这个线程就永远没有CPU的使用权*

### 线程状态的变迁

线程状态的生命周期可以简化为下图：

```sh

    +------------+
    |     NEW    |   
    +------------+
           ↓          +---------------+
    +------------+    |    BLOCKED    |
    |  RUNNABLE  | ←→ |    WAITING    |
    +------------+    | TIMED_WAITING |
           ↓          +---------------+
    +------------+
    | TERMINATED |
    +------------+    

```

1. [NEW 到 RUNNABLE][2]

通过调用线程对象的`start()`方法

2. [RUNNABLE 与 BLOCKED 的转换][3]

只有一种场景会触发这种转换，那就是线程等待`synchronized`的隐式锁。`synchronized`修饰的方法、代码块同一时刻只允许一个线程执行，其他线程只能等待，
这种情况下，等待的线程就会从 RUNNABLE 转换到 BLOCKED；而当等待的线程获得`synchronized`的隐式锁后，就会从 BLOCKED 转换到 RUNNABLE。

3. [RUNNABLE 与 WAITING 的转换][4]

三种场景触发这种转换：

| 进入方式 | 退出方式 |
| --- | --- |
| 获得 synchronized 隐式锁的线程，调用不带超时参数的 `Object.wait()` 方法 | 其他线程调用 `Object.notify()` / `Object.notifyAll()` 唤醒线程 |
| 调用不带超时参数的 `Thread.join()` 方法 | 被调用的线程执行完毕 |
| 调用 `LockSupport.park()` 方法 | 其他线程调用 `LockSupport.unpark(Thread thread)` 唤醒目标线程 | 

4. [RUNNABLE 与 TIMED_WAITING 的转换][5]

五种场景触发这种转换：

| 进入方式 | 退出方式 |
| --- | --- |
| 调用 `Thread.sleep(long millis)` 方法 | 时间结束 |
| 获得 synchronized 隐式锁的线程，调用带超时参数的 `Object.wait(long timeout)` 方法 | 时间结束 / 其他线程调用 `Object.notify()` / `Object.notifyAll()` 唤醒线程 |
| 调用带超时参数的 `Thread.join(long millis)` 方法 | 时间结束 / 被调用的线程执行完毕 |
| 调用带超时参数的 `LockSupport.parkNanos(Object blocker, long deadline)` 方法 | `LockSupport.unpark(Thread thread)` |
| 调用带超时参数的 `LockSupport.parkUntil(long deadline)` 方法 | `LockSupport.unpart(Thread thread)` |

5. RUNNABLE 到 TERMINATED

线程执行完 `run()` 方法后，会自动转换成 TERMINATED 状态；如果执行 `run()` 的时候抛出异常，也会导致线程终止。

*注意：如果我们想人为终止线程，Thread类里面提供了一个`stop()`方法，但是已被标注为`@Deprecated`，正确的方法应该是调用`interrupt()`方法*

[示例代码](../../java/org/concurrency/thread/ThreadState.java)

## 线程的使用

### 线程的初始化

通过源码分析一个线程的初始化：

```java
public class Thread {
    
    /**
     * 初始化线程
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        this.name = name;
        //当前线程是该线程的父线程
        Thread parent = currentThread();
        
        //...中间省略...
        
        //设置线程组
        this.group = g;
        //将当前线程的daemon、priority属性设置为父线程相同的属性
        this.daemon = parent.isDaemon();        //是否为守护线程
        this.priority = parent.getPriority();   //线程优先级
        if (security == null || isCCLOverridden(parent.getClass()))
            this.contextClassLoader = parent.getContextClassLoader();
        else
            this.contextClassLoader = parent.contextClassLoader;    //设置为父线程的contextClassLoader
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        this.target = target;                                       //设置runnable
        setPriority(priority);
        //复制父线程的inheritableThreadLocals
        if (parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals =
                ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        this.stackSize = stackSize;

        tid = nextThreadID();   //设置新线程ID
    }
}
```

由此可见，一个新线程（子线程）的初始化是继承了父线程是否为daemon、优先级、contextClassLoader 和 inheritableThreadLocals，
同时分配一个唯一ID来标识新线程。至此，一个新线程就初始化完成，在堆内存中等待运行。

### 线程的实现

三种方式实现线程：

1. 实现 Runnable 接口
    
    需要实现`run()`方法，并通过 Thread 调用`start()`方法来运行`run()`方法

2. 继承Thread类
    
    同样也需要重写`run()`方法，因为 Thread 实际是实现了 Runnable 接口
    
```java
public class Thread {
    private Runnable target;
    @Override
    public void run() {
        if (target != null) {
            target.run();
        }
    }
}
```

3. 实现 Callable 接口

    需要实现`call()`方法，与 Runnable 相比，Callable可以有返回值，返回值通过 FutureTask 进行封装
    
```java
public interface Callable<V> {
    V call() throws Exception;
}
```

Runnable和Thread的选择

1. java不支持多重继承，如果继承了Thread类就无法再继承其他类，但是java支持实现多个接口

2. 如果类只需要可执行就可以，继承整个Thread类开销太大

[示例代码](../../java/org/concurrency/thread/ThreadImpl.java)


### 线程的中断和终止

通过调用线程的`interrupt()`中断线程，如果该线程处于 WAITING 或 TIMED_WAITING 状态时（如调用了`Object.wait()`、`Object.wait(long)`、
`Object.join()`、`Object.join(long)`、`sleep(long)`等），那么会抛出`InterruptedException`，并且*中断状态会被清除*

→ [参看interrupt()文档][6]

如果一个线程的`run()`方法执行一个无限循环，并且没有执行会抛出`InterruptedException`的操作，那么调用该线程的`interrupt()`方法就无法使线程提前结束。
但是`interrupt()`方法会设置一个中断状态，此时线程可以通过`isInterrupted()`或者调用静态方法`Thread.interrupted()`来检查线程是否被中断。

*其中`Thread.interrupted()`方法会清除当前线程的中断状态。*

[示例代码](../../java/org/concurrency/thread/InterruptedStatus.java)

### 线程间的协作



[1]: https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.State.html
[2]: https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.State.html#RUNNABLE
[3]: https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.State.html#BLOCKED
[4]: https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.State.html#WAITING
[5]: https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.State.html#TIMED_WAITING
[6]: https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#interrupt--
