# Dubbo 多版本

当一个接口实现，出现不兼容升级时，可以用版本号过渡，版本号不同的服务相互间不引用。

可以按照以下的步骤进行版本迁移：

1. 在低压力时间段，先升级一半提供者为新版本
2. 再将所有消费者升级为新版本
3. 然后将剩下的一半提供者升级为新版本

老版本服务提供者配置：

```
<dubbo:service interface="com.foo.BarService" version="1.0.0" />

```

新版本服务提供者配置：

```
<dubbo:service interface="com.foo.BarService" version="2.0.0" />

```

老版本服务消费者配置：

```
<dubbo:reference id="barService" interface="com.foo.BarService" version="1.0.0" />

```

新版本服务消费者配置：

```
<dubbo:reference id="barService" interface="com.foo.BarService" version="2.0.0" />

```

如果不需要区分版本，可以按照以下的方式配置 [1](http://dubbo.io/books/dubbo-user-book/demos/multi-versions.html#fn_1)：

```
<dubbo:reference id="barService" interface="com.foo.BarService" version="*" />

```

> 1. `2.2.0` 以上版本支持[ ↩](http://dubbo.io/books/dubbo-user-book/demos/multi-versions.html#reffn_1)

