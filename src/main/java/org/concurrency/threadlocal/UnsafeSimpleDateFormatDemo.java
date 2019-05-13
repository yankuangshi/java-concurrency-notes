package org.concurrency.threadlocal;

import org.concurrency.util.ConcurrentUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SimpleDateFormat是非线程安全的
 * 会导致日期的parse结果错误
 *
 * SimpleDateFormat的文档内有提示如下：
 * "Date formats are not synchronized.
 *  It is recommended to create separate format instances for each thread.
 *  If multiple threads access a format concurrently, it must be synchronized
 *  externally."
 *
 * @author kyan
 * @date 2019/5/13
 */
public class UnsafeSimpleDateFormatDemo {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatDate(Date date) {
        return sdf.format(date);
    }

    public static Date parse(String dateStr) throws ParseException {
        return sdf.parse(dateStr);
    }

    public static void main(String[] args) throws InterruptedException {
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
        //打印结果可能如下：
        //Mon May 13 14:00:00 CST 2019
        //Mon May 13 14:00:00 CST 2019
        //Wed May 15 05:16:40 CST 2019 《===== 日期解析错误
        //Mon May 13 14:00:00 CST 2019
        //Mon May 13 14:00:00 CST 2019
        //Mon May 13 14:00:00 CST 2019
        //Mon May 13 14:00:00 CST 2019
        //Mon Aug 05 03:00:00 CST 2019 《===== 日期解析错误
    }
}
