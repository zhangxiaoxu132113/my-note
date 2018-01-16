## 并发容器之ConcurrentMap
并发Map容器有两种实现，分别是
- ConcurrentHashMap
- ConcurrentLinkedQueue


### ConcurrentHashMap
1.1 设计思路
ConcurrentHashMap采用了分段锁的设计，只有在同一个分段内才存在竞态关系，不同的分段锁之间没有锁竞争。相比于对整个Map加锁的设计，分段锁大大的提高了高并发环境下的处理能力。但同时，由于不是对整个Map加锁，导致一些需要扫描整个Map的方法（如size(), containsValue()）需要使用特殊的实现，另外一些方法（如clear()）甚至放弃了对一致性的要求（ConcurrentHashMap是弱一致性的，具体请查看ConcurrentHashMap能完全替代HashTable吗？）。

ConcurrentHashMap中的分段锁称为Segment，它即类似于HashMap（JDK7与JDK8中HashMap的实现）的结构，即内部拥有一个Entry数组，数组中的每个元素又是一个链表；同时又是一个ReentrantLock（Segment继承了ReentrantLock）。ConcurrentHashMap中的HashEntry相对于HashMap中的Entry有一定的差异性：HashEntry中的value以及next都被volatile修饰，这样在多线程读写过程中能够保持它们的可见性，
> http://www.importnew.com/22007.html 这篇文章介绍了ConcurrentHashMap，非常详细。
> https://my.oschina.net/hosee/blog/675423 这篇文章介绍了是否HashTable能替代ConcurrentHashMap







