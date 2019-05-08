package org.concurrency.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 线程实现测试
 *
 * @author kyan
 * @date 2019/05/07
 */
public class ThreadImplDemo {

    /**
     * 方法1： 实现 Runnable 接口
     * 需要实现run()方法，并通过 Thread 调用start()方法来运行run()方法
     */
    public static void testWithImplementRunnable() {
        Thread thread = new Thread(new MyRunnable(), "Thread-A");

        thread.run();   //No new thread is created and the run() method is executed on the current thread(main thread)
        thread.start(); //start() creates a new thread and the run() method is executed

        //should print
        //Hello main
        //Hello Thread-A
    }

    /**
     * 继承Thread类
     * 同样也需要重写run()方法，因为 Thread 实际是实现了 Runnable 接口
     */
    public static void testWithExtendsThread() {
        MyThread myThread = new MyThread("Thread-B");
        myThread.start();

        //should print
        //Hello Thread: Thread-B
    }

    /**
     * 实现 Callable 接口
     * 需要实现call()方法，与 Runnable 相比，Callable可以有返回值，返回值通过 FutureTask 进行封装
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void testWithImplementCallable() throws ExecutionException, InterruptedException {
        MyCallable myCallable = new MyCallable(123);
        FutureTask<Integer> ft = new FutureTask<>(myCallable);
        Thread t = new Thread(ft, "Thread-C");
        t.start();
        System.out.println("Result: " + ft.get());

        //should print
        //Hello Thread: Thread-C
        //Result: 123
    }

    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("Hello Thread: " + Thread.currentThread().getName());
        }
    }

    static class MyThread extends Thread {

        public MyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println("Hello Thread: " + Thread.currentThread().getName());
        }
    }

    static class MyCallable implements Callable {

        private int result;

        public MyCallable(int result) {
            this.result = result;
        }

        @Override
        public Object call() throws Exception {
            System.out.println("Hello Thread: " + Thread.currentThread().getName());
            return result;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testWithImplementRunnable();
        testWithExtendsThread();
        testWithImplementCallable();
    }
}
