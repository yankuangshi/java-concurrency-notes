package org.concurrency.thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Thread.join()方法使用样例
 *
 * 该样例中有11个线程main线程和Thread-0，Thread-1，...，Thread-9
 * 除了main线程，每个线程都拥有前一个线程的引用，需要等待前一个线程的终止，类似多米诺骨牌：
 * Thread-0等待main线程结束
 * Thread-1等待Thread-0结束
 * Thread-2等待Thread-1结束
 * ...
 * Thread-9等待Thread-8结束
 *
 * 所以期望的输出结果应该是：
 *
 * main结束
 * Thread-0结束
 * Thread-1结束
 * ...
 * Thread-9结束
 *
 * @author kyan
 * @date 2019/5/10
 */
public class JoinDemo {

    public static void main(String[] args) throws InterruptedException {
        //main thread
        System.out.println(Thread.currentThread().getName() + " running @"
                + new SimpleDateFormat("HH:mm:ss").format(new Date()));
        Thread prevThread = Thread.currentThread();
        for (int i = 0; i < 10; i++) {
            //每一个线程都拥有前一个线程的引用，需要等待前一个线程终止，才能从等待中返回
            Thread t = new Thread(new Domino(prevThread), "Thread-" + String.valueOf(i));
            t.start();
            prevThread = t;
        }
        TimeUnit.SECONDS.sleep(5);
        System.out.println(Thread.currentThread().getName() + " terminate @"
                + new SimpleDateFormat("HH:mm:ss").format(new Date()));

        //打印以下结果（时间会根据不同运行时间不同）
        //main running @10:59:03
        //main terminate @10:59:08
        //Thread-0 terminate @10:59:09
        //Thread-1 terminate @10:59:10
        //Thread-2 terminate @10:59:11
        //Thread-3 terminate @10:59:12
        //Thread-4 terminate @10:59:13
        //Thread-5 terminate @10:59:14
        //Thread-6 terminate @10:59:15
        //Thread-7 terminate @10:59:16
        //Thread-8 terminate @10:59:17
        //Thread-9 terminate @10:59:18
    }

    static class Domino implements Runnable {
        private Thread prevThread;
        public Domino(Thread prevThread) {
            this.prevThread = prevThread;
        }

        @Override
        public void run() {
            try {
                //wait until prev thread end
                prevThread.join();
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " terminate @"
                    + new SimpleDateFormat("HH:mm:ss").format(new Date()));
        }
    }
}
