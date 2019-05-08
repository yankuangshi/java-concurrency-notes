package org.concurrency.thread;

import java.util.concurrent.TimeUnit;

/**
 * 线程终止测试
 *
 * @author kyan
 * @date 2019/5/7
 */
public class ThreadShutdownDemo {


    public static void main(String[] args) throws InterruptedException {
        Server myServer = new Server();
        Thread serverThread = new Thread(myServer, "server-thread");
        serverThread.start();

        //Stop the server thread
        System.out.println(Thread.currentThread().getName() + " is stopping server thread");
        //Thread's stop() method has been deprecated!!!
//        serverThread.stop();
        myServer.stop();
        //Let's wait to see server thread stopped
        TimeUnit.MILLISECONDS.sleep(200);
        System.out.println(Thread.currentThread().getName() + " is finished now");

        //should print
        //Server is running...
        //Server is running...
        //Server is running...
        //Server is running...
        //Server is running...
        //Server is running...
        //Server is running...
        //main is stopping server thread
        //Server is running...
        //main is finished now
    }

    static class Server implements Runnable {
        private volatile boolean exit = false;

        @Override
        public void run() {
            while (!exit) {
                System.out.println("Server is running...");
            }
        }

        public void stop() {
            exit = true;
        }
    }
}
