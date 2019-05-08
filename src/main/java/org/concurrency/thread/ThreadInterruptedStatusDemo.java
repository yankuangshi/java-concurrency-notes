package org.concurrency.thread;

import java.util.concurrent.TimeUnit;

/**
 * 线程中断和终止测试
 *
 * @author kyan
 * @date 2019/05/07
 */
public class ThreadInterruptedStatusDemo {

    public static void main(String[] args) throws InterruptedException {
        testInterruptedStatusCleared();
//        testInterruptedStatusCleared2();
//        testInterruptedStatusWithoutCleared();
    }

    /**
     * 抛出InterruptedException，Java虚拟机会先将该线程的中断标识清除
     *
     * @throws InterruptedException
     */
    public static void testInterruptedStatusCleared() throws InterruptedException {
        Thread sleepThread = new Thread(new SleepRunnable(), "SleepThread");
        sleepThread.start();
        //休眠5s，让线程充分运行
        TimeUnit.SECONDS.sleep(5);
        sleepThread.interrupt();

        //should print
        //throw InterruptedException
        //Thread: SleepThread interrupted status: false
    }

    /**
     * 调用静态方法Thread.interrupted()会清除当前线程的中断标识
     *
     * @throws InterruptedException
     */
    public static void testInterruptedStatusCleared2() throws InterruptedException {
        Thread busyThread = new Thread(new BusyRunnable(),"BusyThread");
        busyThread.start();
        TimeUnit.SECONDS.sleep(5);
        busyThread.interrupt();

        //should print
        //Thread: BusyThread interrupted status: false
    }

    /**
     * 中断标识不会被清除
     * @throws InterruptedException
     */
    public static void testInterruptedStatusWithoutCleared() throws InterruptedException {
        Thread busyThread = new Thread(new BusyRunnable2(), "BusyThread2");
        busyThread.start();
        TimeUnit.SECONDS.sleep(5);
        busyThread.interrupt();

        //should print
        //Thread: BusyThread2 interrupted status: true
    }

    static class SleepRunnable implements Runnable {

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("throw InterruptedException");
            } finally {
                System.out.println("Thread: " + Thread.currentThread().getName()
                        +  " interrupted status: " + Thread.currentThread().isInterrupted());
            }
        }
    }

    static class BusyRunnable implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                //The interrupted status of the thread is cleared by this method
            }
            System.out.println("Thread: " + Thread.currentThread().getName()
                    + " interrupted status: " + Thread.currentThread().isInterrupted());
        }
    }

    static class BusyRunnable2 implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
            }
            System.out.println("Thread: " + Thread.currentThread().getName()
                    + " interrupted status: " + Thread.currentThread().isInterrupted());
        }
    }

}

