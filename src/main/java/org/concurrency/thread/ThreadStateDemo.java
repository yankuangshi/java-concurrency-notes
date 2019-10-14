package org.concurrency.thread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 验证Java线程状态
 *
 * @author kyan
 * @date 2019/05/07
 */
public class ThreadStateDemo {

    public static void main(String[] args) {
        //初始化两个BlockThread线程，其中一个获得锁，另一个处于BLOCKED阻塞状态
        BlockThread bt1 = new BlockThread("BlockThread-1");
        BlockThread bt2 = new BlockThread("BlockThread-2");
        WaitingThread wt = new WaitingThread("WaitingThread");
        TimedWaitingThread twt = new TimedWaitingThread("TimedWaitingThread");
        BlockedInIOThread blockedInIOThread = new BlockedInIOThread("BlockedInIOThread");
        BlockedInSocketThread blockedInSocketThread = new BlockedInSocketThread("BlockedInSocketThread");
        bt1.start();
        bt2.start();
        wt.start();
        twt.start();
        blockedInIOThread.start();
        blockedInSocketThread.start();

        System.out.println("Thread: " + bt1.getName() + " current state: " + bt1.getState().toString());
        System.out.println("Thread: " + bt2.getName() + " current state: " + bt2.getState().toString());
        System.out.println("Thread: " + wt.getName() + " current state: " + wt.getState().toString());
        System.out.println("Thread: " + twt.getName() + " current state: " + twt.getState().toString());
        System.out.println("Thread: " + blockedInIOThread.getName() + " current state: " + blockedInIOThread.getState().toString());
        System.out.println("Thread: " + blockedInSocketThread.getName() + " current state: " + blockedInSocketThread.getState().toString());

//        should print
//        Thread: BlockThread-1 current state: RUNNABLE
//        Thread: BlockThread-2 current state: BLOCKED                  //BlockThread-1和BlockThread-2状态可能会交换
//        Thread: WaitingThread current state: WAITING
//        Thread: TimedWaitingThread current state: TIMED_WAITING
//        Thread: BlockedInIOThread current state: RUNNABLE
//        Thread: BlockedInSocketThread current state: RUNNABLE

    }

    /**
     * 模拟BLOCK状态的线程
     */
    static class BlockThread extends Thread {

        public BlockThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            //线程在BlockThread.class上加锁后，无限循环不释放锁
            synchronized (BlockThread.class) {
                for (;;) {
                }
            }
        }
    }

    /**
     * 模拟WAITING状态的线程
     */
    static class WaitingThread extends Thread {

        public WaitingThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            //线程在WaitingThread.class上加锁后，无限时间地等待
            synchronized (WaitingThread.class) {
                try {
                    WaitingThread.class.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * 模拟TIMED_WAITING状态的线程
     */
    static class TimedWaitingThread extends Thread {

        public TimedWaitingThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            //无限循环睡眠
            for (;;) {
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 模拟正在阻塞式I/O操作时的线程（状态是RUNNABLE）
     * 操作是阻塞式的，但是
     */
    static class BlockedInIOThread extends Thread {

        public BlockedInIOThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            System.out.println("Input: " + input);
        }
    }

    /**
     * 模拟网络阻塞操作时的线程（状态也是RUNNABLE）
     */
    static class BlockedInSocketThread extends Thread {

        public BlockedInSocketThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            ServerSocketChannel ssc = null;
            try {
                ssc = ServerSocketChannel.open().bind(
                        new InetSocketAddress(10086)
                );
                while (true) {
                    //阻塞的accept方法
                    SocketChannel socket = ssc.accept();
                    //TODO
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    ssc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
