package org.concurrency.thread;

/**
 * synchronized关键字测试
 *
 * @author kyan
 * @date 2019/5/8
 */
public class SynchronizedDemo {

    public static void main(String[] args) {
        //同步块：对 Synchronized Class对象进行加锁
        synchronized (SynchronizedDemo.class) {

        }

        //同步方法
        m();
    }

    public static synchronized void m() {}

    //通过javap工具查看生成的class文件，来分析synchronized的实现
    //$ javap -v SynchronizedDemo.class
    //Classfile /Users/kyan/Workspace/myworkspace/java-concurrency-notes/target/classes/org/concurrency/thread/SynchronizedDemo.class
    //  Last modified May 8, 2019; size 606 bytes
    //  MD5 checksum 2596f216b9e4121827bb5f26aa02f945
    //  Compiled from "SynchronizedDemo.java"
    //public class org.concurrency.thread.SynchronizedDemo
    //  minor version: 0
    //  major version: 52
    //  flags: ACC_PUBLIC, ACC_SUPER
    //Constant pool:
    //   #1 = Methodref          #4.#23         // java/lang/Object."<init>":()V
    //   #2 = Class              #24            // org/concurrency/thread/SynchronizedDemo
    //   #3 = Methodref          #2.#25         // org/concurrency/thread/SynchronizedDemo.m:()V
    //   #4 = Class              #26            // java/lang/Object
    //   #5 = Utf8               <init>
    //   #6 = Utf8               ()V
    //   #7 = Utf8               Code
    //   #8 = Utf8               LineNumberTable
    //   #9 = Utf8               LocalVariableTable
    //  #10 = Utf8               this
    //  #11 = Utf8               Lorg/concurrency/thread/SynchronizedDemo;
    //  #12 = Utf8               main
    //  #13 = Utf8               ([Ljava/lang/String;)V
    //  #14 = Utf8               args
    //  #15 = Utf8               [Ljava/lang/String;
    //  #16 = Utf8               StackMapTable
    //  #17 = Class              #15            // "[Ljava/lang/String;"
    //  #18 = Class              #26            // java/lang/Object
    //  #19 = Class              #27            // java/lang/Throwable
    //  #20 = Utf8               m
    //  #21 = Utf8               SourceFile
    //  #22 = Utf8               SynchronizedDemo.java
    //  #23 = NameAndType        #5:#6          // "<init>":()V
    //  #24 = Utf8               org/concurrency/thread/SynchronizedDemo
    //  #25 = NameAndType        #20:#6         // m:()V
    //  #26 = Utf8               java/lang/Object
    //  #27 = Utf8               java/lang/Throwable
    //{
    //  public org.concurrency.thread.SynchronizedDemo();
    //    descriptor: ()V
    //    flags: ACC_PUBLIC
    //    Code:
    //      stack=1, locals=1, args_size=1
    //         0: aload_0
    //         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
    //         4: return
    //      LineNumberTable:
    //        line 9: 0
    //      LocalVariableTable:
    //        Start  Length  Slot  Name   Signature
    //            0       5     0  this   Lorg/concurrency/thread/SynchronizedDemo;
    //
    //  public static void main(java.lang.String[]);
    //    descriptor: ([Ljava/lang/String;)V
    //    flags: ACC_PUBLIC, ACC_STATIC
    //    Code:
    //      stack=2, locals=3, args_size=1
    //         0: ldc           #2                  // class org/concurrency/thread/SynchronizedDemo
    //         2: dup
    //         3: astore_1
    //         4: monitorenter
    //         5: aload_1
    //         6: monitorexit
    //         7: goto          15
    //        10: astore_2
    //        11: aload_1
    //        12: monitorexit
    //        13: aload_2
    //        14: athrow
    //        15: invokestatic  #3                  // Method m:()V
    //        18: return
    //      Exception table:
    //         from    to  target type
    //             5     7    10   any
    //            10    13    10   any
    //      LineNumberTable:
    //        line 13: 0
    //        line 15: 15
    //        line 16: 18
    //      LocalVariableTable:
    //        Start  Length  Slot  Name   Signature
    //            0      19     0  args   [Ljava/lang/String;
    //      StackMapTable: number_of_entries = 2
    //        frame_type = 255 /* full_frame */
    //          offset_delta = 10
    //          locals = [ class "[Ljava/lang/String;", class java/lang/Object ]
    //          stack = [ class java/lang/Throwable ]
    //        frame_type = 250 /* chop */
    //          offset_delta = 4
    //
    //  public static synchronized void m();
    //    descriptor: ()V
    //    flags: ACC_PUBLIC, ACC_STATIC, ACC_SYNCHRONIZED
    //    Code:
    //      stack=0, locals=0, args_size=0
    //         0: return
    //      LineNumberTable:
    //        line 18: 0
    //}
    //SourceFile: "SynchronizedDemo.java"
}
