package org.concurrency.happensbefore;

/**
 * @author kyan
 * @date 2019/10/14
 */
public class JoinExample {

    private int x = 0;
    private int y = 1;
    private boolean flag = false;

    public void write() {
        this.x = 10;
        this.y = 20;
        this.flag = true;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " BEGIN");

        JoinExample joinExample = new JoinExample();
        Thread t1 = new Thread(joinExample::write, "thread-1");
        t1.start();

        t1.join();

        System.out.println("x: " + joinExample.x);
        System.out.println("y: " + joinExample.y);
        System.out.println("flag: " + joinExample.flag);
        System.out.println(Thread.currentThread().getName() + " END");
//        运行结果
//        main BEGIN
//        x: 10
//        y: 20
//        flag: true
//        main END
        //join规则保证了主线程执行t1.join()并成功返回后，线程t1中对共享变量的修改都会主线程可见
    }
}
