package org.concurrency.thread;

import java.sql.Connection;
import java.util.LinkedList;

/**
 * 用「等待超时模式」实现简单连接池示例
 */
public class ConnectionPool {

    /**
     * 双向队列维护连接
     */
    private LinkedList<Connection> pool = new LinkedList<>();

    /**
     * 构造函数 初始化连接池
     * @param initSize
     */
    public ConnectionPool(int initSize) {
        if (initSize > 0) {
            for (int i = 0; i < initSize; i++) {
                pool.add(ConnectionDriver.createConnection());
            }
        }
    }

    /**
     * 在millis内如果未获取到连接，则返回null
     * @param millis
     * @return
     * @throws InterruptedException
     */
    public Connection fetchConnection(long millis) throws InterruptedException {
        synchronized (pool) {
            //完全超时，即millis小于等于0时，如果此时连接池为空，获取到对象锁的线程会一直处于等待状态
            if (millis <= 0) {
                while (pool.isEmpty()) {
                    pool.wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + millis;
                long remaining = millis;
                //如果连接池为空 且 剩余等待时间>0，则继续等待wait(remaining)
                while (pool.isEmpty() && remaining > 0) {
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }
                Connection result = null;
                if (!pool.isEmpty()) {
                    result = pool.removeFirst();
                }
                return result;
            }
        }

    }

    /**
     * 释放连接池
     * @param connection
     */
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (pool) {
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

}
