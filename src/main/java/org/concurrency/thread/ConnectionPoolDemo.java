package org.concurrency.thread;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 连接池示例代码
 *
 */
public class ConnectionPoolDemo {

    /**
     * 初始化连接池，设置连接数为10
     */
    static ConnectionPool pool = new ConnectionPool(10);
    /**
     * CountDownLatch保证所有的ConnectionRunner线程同时开始
     */
    static CountDownLatch start = new CountDownLatch(1);
    /**
     * CountDownLatch保证所有的ConnectionRunner线程结束后，main主线程才继续执行
     */
    static CountDownLatch end;

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 50;
        end = new CountDownLatch(threadCount);
        int count = 20;
        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();
        AtomicLong threadNum = new AtomicLong();

        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new ConnectionRunner(count, got, notGot), "ConnectionPool-Thread-" + threadNum.incrementAndGet());
            t.start();
        }
        //所有ConnectionRunner线程同时开始
        start.countDown();
        //等待所有ConnectionRunner线程结束
        end.await();

        System.out.println("Total invoke: " + (threadCount * count));
        System.out.println("Got connection: " + got);
        System.out.println("Not Got connection: " + notGot);
    }

    static class ConnectionRunner implements Runnable {

        /**
         * 循环获取次数
         */
        int count;
        /**
         * 统计获取到连接的次数
         */
        AtomicInteger got;
        /**
         * 统计未获取到连接的次数
         */
        AtomicInteger notGot;

        public ConnectionRunner(int count, AtomicInteger got, AtomicInteger notGot) {
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (count > 0) {
                try {
                    //尝试获取连接，设置超时时间为1000毫秒
                    Connection connection = pool.fetchConnection(1000);
                    if (connection != null) {
                        //获取到连接后的处理
                        System.out.println(Thread.currentThread().getName() + " got the connection @count-" + count);
                        try {
                            connection.createStatement();
                            connection.commit();
                        } finally {
                            pool.releaseConnection(connection);
                            got.incrementAndGet();
                        }
                    } else {
                        //未获取到连接后的处理
                        notGot.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    count--;
                }
            }
            end.countDown();
        }
    }

}
