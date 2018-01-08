面试总结
最近两周面试了几家公司Java高级工程师的职位，主要有宜信、网信金融、阿里高德、口袋购物。目前有部分公司已经面试通过，两家在等消息。今天趁热把常见面试内容总结一下。

Java基础
Hashtable和HashMap的区别。
抽象类与接口的区别。
final关键字的使用和区别。
异常分类和处理机制。
JDK版本区别。
StringBuilder内部实现机制。
反射机制的使用。
匿名内部类的使用。
泛型的概念和使用。
弱引用和虚引用的概念和使用方式。

开源框架
SpringMVC和Struts2的区别
Spring IOC和AOP的概念以及实现方式
Spring事务的管理
Hibernate与MyBatis的比较
Hibernate延迟加载的机制

JVM虚拟机
GC算法有哪些
垃圾回收器有哪些
如何调优JVM

缓存和NoSQL
缓存的使用场景
缓存命中率的计算
Memcache与Redis的比较
如何实现Redis的分片
MongoDB的特点

分布式
zookeeper的用途
dubbo的用途以及优点
dubbo的实现原理

数据结构和算法
单向链表的逆序排列
双向链表的操作
1亿个整数的倒序输出
找出给定字符串中最长回文（回文：abcdcba，两端对称）

网络编程
Get和Post的区别
Https协议的实现
长连接的管理
Socket的基本方法

数据库
inner join和left join的区别
复杂SQL语句
数据库优化方式
数据库拆分方式
如何保证不同数据结构的数据一致性

安全
什么是XSS攻击，具体如何实现？
开放问题：如何保障系统安全？

设计模式
写出一个设计模式的类图
设计模式的意义是什么
写个单例模式的代码

多线程
如何避免Quartz重复启动任务
线程池满了如何处理额外的请求
同一个对象的连个同步方法能否被两个线程同时调用

待更

2017年03月04日更新：
距离发布文章已经很久，恰逢求职季，遂整理部分面试问题的答案，不甚完整，仅供参考。

部分答案
Java基础
HashMap和Hashtable的区别
HashMap是非线程安全的，Hashtable是线程安全的。

HashMap的键值都可以为null，Hashtable的键值都不可以为null值。

HashMap继承自AbstractMap类，Hashtable继承自Dictionary类。

ps : Properties类继承自Hashtable类。

异常分类和处理机制
分类：

运行时异常（Runtime Exception）

受检查异常（Checked Exception）

运行时异常：

必须继承RuntimeException类，

定义方法时不必声明会抛出运行时异常。

调用方法时不必捕获运行时异常。

受检查异常：

不继承自RumtimeException类

定义方法时需要抛出可能会抛出的Checked Exception

调用方法时需要捕获Checked Exception或者继续向上抛出。

逻辑上：

运行时异常：一般不需要或者不知道如何处理此类异常；

受检验异常：一般需要知道如何处理可能发生的异常情况。

StringBuilder内部实现机制
StringBuilder内部有一个字符数组，代码如下

char[] value;   //字符数组
int count;      //字符串长度
每一次append操作都是将新的字符串加入到可变长的字符数组中，长度计算方式与ArrayList类似。调用toString()方法时，new一个String对象即可。

public String toString() {
        return new String(value, 0, count);// Create a copy, don't share the array
}
ps: StringBuffer是线程安全的，StringBuilder是非线程安全的。

匿名内部类的使用
匿名内部类是没有名字的类，只在某一处被使用，不会被多处调用，一般是某个父类或接口的特定实现。

强引用、软引用、弱引用和虚引用
强引用: 一般的引用都是强引用，即使OutOfMemory也不会回收这部分被把持的引用内存。

软引用（SoftReference）: 如果内存空间足够，垃圾回收器就不会回收它；如果内存空间不足了，就会回收这些对象的内存。只要垃圾回收器没有回收它，该对象就可以被使用。++软引用可以用来实现内存敏感的高速缓存。++

弱引用（WeakReference）：弱引用的对象拥有更短暂的生命周期。当垃圾回收期发现只有若引用的对象，不论内存空间足够与否，都会回收它。

虚引用（）：虚引用不会决定对象的生命周期，如果一个对象仅持有一个虚引用，那么它随时可能被回收。++虚引用主要用来跟踪对象被垃圾回收器回收的活动。++

更多关于『强、软、弱和虚引用』参考文章：http://zhangjunhd.blog.51cto....

开源框架
SpringMVC和Struts2的区别
SpringMVC的方法级别的拦截，Struts2是类级别的拦截；

SpringMVC是基于Servlet实现Controller，Struts2是基于Filter实现；

SpringMVC性能和安全性高于Struts2；

SpringMVC更加组件化和流程化，易于扩展，比如返回JSON通过设置@ResponseBody即可；

Struts2更加无耦合，比较纯粹，但是需要更多的自行开发的代码以支持更多功能。

Spring事务的管理
分类：声明式事务、编程式事务
声明式事务：通过统一配置实现事务的统一管理，一般配置TransactionMananger以及相关属性即可。

编程式事务（注解方式）：在需要事务的方法上配置相关的注解（包括事务类型、回滚策略等）

事务类型：
PROPAGATION_REQUIRED

PROPAGATION_SUPPORTS

PROPAGATION_MANDATORY

PROPAGATION_REQUIRES_NEW

PROPAGATION_NOT_SUPPORTED

PROPAGATION_NEVER

PROPAGATION_NESTED

事务隔离级别
ISOLATION_DEFAULT

ISOLATION_READ_UNCOMMITTED

ISOLATION_READ_COMMITTED

ISOLATION_REPEATABLE_READ

ISOLATION_SERIALIZABLE

Hibernate与MyBatis的比较
Hibernate完全实现对象关系映射（ORM），MyBatis实现的是SQL Mapping

MyBatis可以进行更为细致的SQL优化，可以减少查询字段。比Hibernate容易掌握，Hibernate门槛较高。

Hibernate的DAO层开发比MyBatis简单，Mybatis需要维护SQL和结果映射。

Hibernate对对象的维护和缓存要比MyBatis好，对增删改查的对象的维护要方便。

Hibernate数据库移植性很好，MyBatis的数据库移植性不好，不同的数据库需要写不同SQL。

Hibernate有更好的二级缓存机制，可以使用第三方缓存。MyBatis本身提供的缓存机制不佳。

JVM虚拟机
GC算法有哪些
引用计数

复制

标记-清除

标记-压缩

分代（新生代、老年代、永久代）

垃圾回收器有哪些
串行回收器：新生代串行回收器、老年代串行回收器

并行回收器：新生代ParNew回收器、新生代ParallelGC回收器、老年代ParallelGC回收器

CMS回收器：（Concurrent Mark Sweep、并发标记清除）

G1回收器（1.7以后代替CMS回收器）

如何调优JVM
标准参数：

-client -server模式

-Xmn、-Xms、-Xmx

监控：jps、jstat、jinfo、jmap、jhat、jstack…

Java 中堆和栈有什么区别？
JVM 中堆和栈属于不同的内存区域，使用目的也不同。

栈常用于保存方法帧和局部变量，而对象总是在堆上分配。

栈通常都比堆小，也不会在多个线程之间共享，而堆被整个 JVM 的所有线程共享。

缓存和NoSQL
缓存命中率的计算
命中缓存次数/(命中缓存次数+未命中缓存次数) = 命中率

Memcache与Redis的区别
memcache把数据存在内存之中，断电后会挂掉；Redis部分数据持久化在硬盘上，断电不会丢失。

memcache存的是key-value对，redis支持更多的数据结构和数据类型

memcache可以使用一致性hash做分布式，redis可以做主从同步

redis单线程，只使用1个cpu

如何实现Redis的分片
使用一致性哈希对数据进行映射

实现方式：客户端分片（每个客户端对应一个分片）、代理协助分片、查询路由分片；

使用redis集群，如codis（豌豆荚，依赖zookeeper）；

分布式
zookeeper的用途
zookeeper作为分布式应用协调系统，已经用到很多分布式项目中。
可以用来完成统一命名服务、状态同步服务、集群管理、分布式应用配置项等管理工作。

zookeeper的主要操作分一下几种：

创建节点

读取节点数据

更新节点数据

删除节点

监控节点变化

应用场景：

统一命名服务，使用create自动创建节点编号；

配置管理，多个节点的共享配置，当配置发生变化时，可利用zookeeper让使用这些配置的节点获得通知，进行重新加载等操作。如dubbo服务。

集群管理：集群选举主节点，资源定位。

共享锁

负载均衡

应用项目：
dubbo服务集群、redis集群、Hadoop集群等

dubbo的用途以及优点
Dubbo是一个分布式服务框架，致力于提供高性能和透明化的RPC远程服务调用方案，以及SOA服务治理方案。

数据结构和算法
将单项链表逆序排列
将单向链表逆序输出，方法有三种：

遍历链表，将每个节点的内容存入一个数组中，然后逆序输出数组(最简单的做法)

使用栈来逆序输出

直接将链表（指针）逆序然后输出

单向链表详细信息参考文章：http://blog.csdn.net/jianyuer...

海量数据操作
十道面试题与十个海量数据处理方法总结