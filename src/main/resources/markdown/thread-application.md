# 线程应用实例

## 「等待超时模式」示例

场景：调用一个方法时等待一段时间，如果该方法能够在给定的时间段内得到结果，则立即返回结果；否则，超时返回默认结果。

解决思路：

假设超时时间段是`T`，那么在当前时间`now+T`之后就会超时。

那么定义2个变量：
* 剩余的等待时间`remaining`，初始的剩余等待时间`remaining=T`
* 截止的超时时间`future=now+T`

仅需要`wait(remaining)`即可，在`wait(remaining)`返回之后计算剩余等待时间`remaining=future-now`，如果`remaining`小于等于0，表示已经
超时，直接退出；否则继续执行`wait(remaining)`。

伪代码如下：

```java
//对当前对象加锁
long future = System.currentTimeMillis() + timeout;
long remaining = timeout;
synchronized(lock) {
    //当result不满足要去 且 剩余等待时间>0
    while (!condition && remaining > 0) {
        wait(remaining);
        remaining = future - System.currentTimeMillis();
    }
    //条件满足或者超时则执行对应的逻辑
}
```

等待超时模式在「等待-通知的经典范式」上增加了超时控制，使得该模式相比原有的范式更具有灵活性，因为即使方法执行时间过长，也不会"永久"阻塞调用者，
而是会按照调用者的要求"按时"返回。

## 简单的数据库连接池示例

场景：模拟从数据库连接池（ConnectionPool）获取、使用和释放连接（Connection）的过程。客户端获取连接的过程被设定为「等待超时模式」，
也就是如果在1000毫秒内如果无法获取到可用连接，则返回给客户端null。假设连接池的大小为10个，然后通过调节客户端的线程数来模拟无法获取连接的场景。

解决思路：

连接池通过构造函数初始化连接数的最大上限，然后通过一个双向队列来维护连接，客户端需要先调用`fetchConnection(long millis)`方法
来指定在多少毫秒内超时获取连接，当连接使用完成后，调用`releaseConnection(Connection conn)`方法将连接放回连接池。


👉 [点击查看 ConnectionPoolDemo 示例代码](../../java/org/concurrency/thread/ConnectionPoolDemo.java)

代码中假设每个Connection的commit()时间为100毫秒，连接池大小为10，benchmark结果如下：

|线程数|循环次数|总尝试次数|获取到连接数|未获取到连接数|未获取的比例|
|---|---|---|---|---|---|
|10|20|200|200|200|0|0%|
|20|20|400|400|386|14|3.5%|
|30|20|600|600|551|49|8.2%|
|40|20|800|800|700|100|12.5%|
|50|20|1000|1000|816|184|18.4%|

可以看到在资源一定的情况下（总连接数为10），随着客户端的线程数的增加，超时未获取到连接的比率不断升高。