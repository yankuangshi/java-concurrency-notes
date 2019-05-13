package org.concurrency.threadlocal;

import org.concurrency.util.ConcurrentUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SimpleDateFormat线程安全的使用方法
 *
 * 方法1：通过synchronized关键字加锁
 * 方法2：把SimpleDateFormat作用局部变量
 * 方法3：用ThreadLocal封装SimpleDateFormat，保证每个线程有各自的SimpleDateFormat对象
 *
 * @author kyan
 * @date 2019/5/13
 */
public class SafeSimpleDateFormatDemo {

    // ========= 方法1：synchronized关键字 =========
    // 通过synchronized加锁来保证线程安全，但是线程会阻塞，缺点是并发量大的时候会影响性能

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatDate(Date date) {
        synchronized (sdf) {
            return sdf.format(date);
        }
    }

    public static Date parse(String dateStr) throws ParseException {
        synchronized (sdf) {
            return sdf.parse(dateStr);
        }
    }

    public static void testWithSynchronized() throws InterruptedException {
        System.out.println("====== Test with synchronized ======");
        ExecutorService pool = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 20; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        try {
                            System.out.println(parse("2019-05-13 14:00:00"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        ConcurrentUtil.stop(pool);
    }
    // ========= 方法1：end ==========


    // ========= 方法2：把SimpleDateFormat定义为局部变量 =========
    // 把SimpleDateFormat定义为局部变量，因为局部变量避免了资源的竞争，所以是线程安全的

    public static String formatDate2(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Date parse2(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(dateStr);
    }

    public static void testWithLocalSimpleDateFormat() throws InterruptedException {
        System.out.println("====== Test with local SimpleDateFormat ======");
        ExecutorService pool = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 20; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        try {
                            System.out.println(parse2("2019-05-13 14:00:00"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        ConcurrentUtil.stop(pool);
    }
    // ========== 方法2：end =================

    // ========== 方法3：ThreadLocal =========
    // ThreadLocal可以保证每个线程都有各自单独的SimpleDateFormat对象，保证线程之间不会对同一个SimpleDateFormat有竞争，所以线程也是安全的

    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
        //重写initialValue
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    public static String formatDate3(Date date) {
        return threadLocal.get().format(date);
    }
    
    public static Date parse3(String dateStr) throws ParseException {
        return threadLocal.get().parse(dateStr);
    }

    public static void testWithThreadLocal() {
        System.out.println("====== Test with ThreadLocal ======");
        ExecutorService pool = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 20; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        try {
                            System.out.println(parse3("2019-05-13 14:00:00"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        ConcurrentUtil.stop(pool);
    }
    // ========= 方法3：end ========

    // ========= 方法4：ThreadLocal & DateUtils工具类 =======

    private static ThreadLocal<SimpleDateFormat> tl = new ThreadLocal<>();

    static class DateUtils implements Runnable {
        private String dateStr;

        public DateUtils(String dateStr) {
            this.dateStr = dateStr;
        }

        @Override
        public void run() {
            if (tl.get() == null) {
                tl.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            } else {
                try {
                    Date date = tl.get().parse(this.dateStr);
                    System.out.println(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void testWithThreadLocalAndDateUtils() {
        System.out.println("====== Test with ThreadLocal&DateUtils ======");
        ExecutorService pool = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 200; i++) {
            pool.submit(new DateUtils("2019-05-13 14:00:00"));
        }
        ConcurrentUtil.stop(pool);
    }

    public static void main(String[] args) throws InterruptedException {
//        testWithSynchronized();
//        testWithLocalSimpleDateFormat();
//        testWithThreadLocal();
        testWithThreadLocalAndDateUtils();
    }

}
