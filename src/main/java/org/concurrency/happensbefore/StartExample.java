package org.concurrency.happensbefore;

/**
 * @author kyan
 * @date 2019/10/14
 */
public class StartExample {

    private int x = 0;
    private int y = 1;
    private boolean flag = false;

    public void write() {
        System.out.println("x: " + x);
        System.out.println("y: " + y);
        System.out.println("flag: " + flag);
    }

    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getName() + " BEGIN");

        StartExample startExample = new StartExample();
        Thread t1 = new Thread(startExample::write, "thread-1");
        startExample.x = 10;
        startExample.y = 20;
        startExample.flag = true;
        t1.start();

        System.out.println(Thread.currentThread().getName() + " END");
//        运行结果
//        main BEGIN
//        main END
//        x: 10
//        y: 20
//        flag: true
        //start规则保证了主线程在执行t1.start()之前对共享变量的修改，接下来在线程t1开始之后都将确保对线程t1可见。
    }
}
