package org.concurrency.thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 等待-通知机制的测试demo
 *
 * @author kyan
 * @date 2019/5/8
 */
public class WaitNotifyDemo {

    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread waitThread = new Thread(new Wait(), "wait-thread");
        waitThread.start();
        //sleep 2s
        TimeUnit.MILLISECONDS.sleep(2000);
        Thread notifyThread = new Thread(new Notify(), "notify-thread");
        notifyThread.start();

        //should print
        //wait-thread wait @ 15:15:11
        //notify-thread hold lock then notify @ 15:15:13 ==》时间相隔2s
        //wait-thread running @ 15:15:18                 ==》时间相隔5s
    }

    static class Wait implements Runnable {
        @Override
        public void run() {
            //加锁，获取lock对象的monitor
            synchronized (lock) {
                //获取到锁后，调用wait()方法，线程进入等待状态
                try {
                    System.out.println(Thread.currentThread().getName() + " wait @ "
                            + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    lock.wait();
                } catch (InterruptedException e) {
                }
                System.out.println(Thread.currentThread().getName() + " running @ "
                        + new SimpleDateFormat("HH:mm:ss").format(new Date()));
            }
        }
    }

    static class Notify implements Runnable {
        @Override
        public void run() {
            //加锁，获取lock对象的monitor
            synchronized (lock) {
                //获取到锁后，进行通知，通知时不会释放锁，直到当前线程释放了lock后，wait-thread才能从wait()方法返回
                System.out.println(Thread.currentThread().getName() + " hold lock then notify @ "
                        + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                lock.notifyAll();
                try {
                    TimeUnit.MILLISECONDS.sleep(5000);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
