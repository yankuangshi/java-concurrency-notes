package org.concurrency.thread;

import org.concurrency.util.SleepUtils;

import java.util.concurrent.TimeUnit;

/**
 * 线程中断和终止测试
 *
 * @author kyan
 * @date 2019/05/07
 */
public class ThreadInterruptedStatusDemo {

    public static void main(String[] args) {
        testInterruptedStatusCleared();
        testInterruptedStatusCleared2();
        testInterruptedStatusClearedAndThenReset();
        testInterruptedStatusUncleared();
        testInterruptedStatusUncleared2();
    }

    /**
     * 测试抛出InterruptedException，Java虚拟机会先将该线程的中断标识清除
     *
     * @throws InterruptedException
     */
    public static void testInterruptedStatusCleared() {
        Thread sleepThread = new Thread(new SleepRunnable(), "SleepThread");
        sleepThread.start();
        //休眠5s，让线程充分运行
        SleepUtils.second(5);
        sleepThread.interrupt();

        //should print
        //throw InterruptedException
        //Thread: SleepThread interrupted status: false
    }

    /**
     * 测试抛出InterruptedException，Java虚拟机清除中断标识位后，重新设置中断标识位
     * @throws InterruptedException
     */
    public static void testInterruptedStatusClearedAndThenReset() {
        Thread sleepThread = new Thread(new SleepRunnable2(), "SleepThread2");
        sleepThread.start();
        //休眠5s，让线程充分运行
        SleepUtils.second(5);
        sleepThread.interrupt();

        //should print
        //Thread: SleepThread2 interrupted status: true
    }

    /**
     * 调用静态方法Thread.interrupted()会清除当前线程的中断标识
     *
     * @throws InterruptedException
     */
    public static void testInterruptedStatusCleared2() {
        Thread busyThread = new Thread(new BusyRunnable(),"BusyThread");
        busyThread.start();
        SleepUtils.second(5);
        busyThread.interrupt();

        //should print
        //Thread: BusyThread interrupted status: false
    }

    /**
     * 中断标识不会被清除
     * @throws InterruptedException
     */
    public static void testInterruptedStatusUncleared() {
        Thread busyThread = new Thread(new BusyRunnable2(), "BusyThread2");
        busyThread.start();
        SleepUtils.second(5);
        busyThread.interrupt();

        //should print
        //Thread: BusyThread2 interrupted status: true
    }

    public static void testInterruptedStatusUncleared2() {
        Thread busyThread = new Thread(new BusyRunnable3(), "BusyThread3");
        //为了让busyThread能够随main线程结束而终止，设为Daemon线程
        busyThread.setDaemon(true);
        busyThread.start();
        //休眠5s，让busyThread充分运行
        SleepUtils.second(5);
        busyThread.interrupt();
        System.out.println("Thread: " + busyThread.getName() + " interrupted status: " + busyThread.isInterrupted());
        //休眠2s，结束main线程，同时终止busyThread线程
        SleepUtils.second(2);

        //should print
        //Thread: BusyThread3 interrupted status: true
    }

    static class SleepRunnable implements Runnable {

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                //在抛出InterruptedException之前，Java虚拟机会先将该线程的中断标识位清除
                System.out.println("throw InterruptedException");
            } finally {
                System.out.println("Thread: " + Thread.currentThread().getName()
                        +  " interrupted status: " + Thread.currentThread().isInterrupted());
            }
        }
    }

    static class SleepRunnable2 implements Runnable {

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                //在抛出InterruptedException之前，Java虚拟机会先将该线程的中断标识位清除
                //线程再次中断自己
                Thread.currentThread().interrupt();
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
                //Thread.interrupted静态方法会清除中断标识位
//                System.out.println("Thread: " + Thread.currentThread().getName() + " run");
            }
            System.out.println("Thread: " + Thread.currentThread().getName()
                    + " interrupted status: " + Thread.currentThread().isInterrupted());
        }
    }

    static class BusyRunnable2 implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                //Thread实例的isInterrupted()方法不会清除中断标识位
//                System.out.println("Thread: " + Thread.currentThread().getName() + " run");
            }
            System.out.println("Thread: " + Thread.currentThread().getName()
                    + " interrupted status: " + Thread.currentThread().isInterrupted());
        }
    }

    static class BusyRunnable3 implements Runnable {

        @Override
        public void run() {
            for (;;) {}
        }
    }

}

