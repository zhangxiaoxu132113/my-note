## DUBBO

## 概观

Dubbo 是一个高性能，基于Java的[RPC](https://en.wikipedia.org/wiki/Remote_procedure_call)框架，由阿里巴巴开源。和许多RPC系统一样，dubbo基于定义一个服务的思想，指定可以通过参数和返回类型远程调用的方法。在服务器端，服务器实现这个接口并运行一个dubbo服务器来处理客户端调用。在客户端，客户端有一个存根，提供与服务器相同的方法。

![dubbo](http://dubbo.io/images//dubbo-architecture.png)

Dubbo提供三个关键功能，包括基于接口的远程调用，容错和负载均衡，以及自动服务注册和发现。Dubbo框架广泛应用于阿里巴巴内外，其他公司包括[京东](http://www.jd.com/)，[当当](http://www.dangdang.com/)，[qunar](https://www.qunar.com/)，[kaola](https://www.kaola.com/)等。

## 快速开始

本指南让你从一个简单的工作例子开始在Java中使用dubbo。您可以在github上的[dubbo项目中](https://github.com/alibaba/dubbo)找到目录“dubbo-demo”中的完整工作示例。

#### 先决条件

- JDK：版本6或更高版本
- Maven：版本3或更高

#### Maven依赖

您可能需要使用最新版本![](https://img.shields.io/maven-central/v/com.alibaba/dubbo.svg)来构建您的dubbo应用程序。

```
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dubbo</artifactId>
    <version>${dubbo.version}</version>
</dependency>

```

#### 定义服务接口

由于服务提供者和服务使用者都依赖于相同的接口，因此强烈建议将接口定义放在一个独立的模块中，这个模块可以由提供者模块和消费者模块共享。

```
package com.alibaba.dubbo.demo;

public interface DemoService {
    String sayHello(String name);
}
```



#### 实施服务提供商

```
package com.alibaba.dubbo.demo.provider;
import com.alibaba.dubbo.demo.DemoService;

public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {
        return "Hello " + name;
    }
}

```

#### 配置服务提供者

下面的代码片段展示了如何使用spring framework配置一个dubbo服务提供者，这是推荐的，但是如果它是首选的，你也可以使用[API配置](https://dubbo.gitbooks.io/dubbo-user-book/content/configuration/api.html)。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">
    <dubbo:application name="demo-provider"/>
    <dubbo:registry address="multicast://224.5.6.7:1234"/>
    <dubbo:protocol name="dubbo" port="20880"/>
    <dubbo:service interface="com.alibaba.dubbo.demo.DemoService" ref="demoService"/>
    <bean id="demoService" class="com.alibaba.dubbo.demo.provider.DemoServiceImpl"/>
</beans>
```

#### 启动服务提供者

```java
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Provider {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] {"META-INF/spring/dubbo-demo-provider.xml"});
        context.start();
        // press any key to exit
        System.in.read();
    }
}
```



#### 配置服务消费者

下面的代码再一次展示了Spring集成

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">
    <dubbo:application name="demo-consumer"/>
    <dubbo:registry address="multicast://224.5.6.7:1234"/>
    <dubbo:reference id="demoService" interface="com.alibaba.dubbo.demo.DemoService"/>
</beans>
```



#### 运行服务消费者

```java
import com.alibaba.dubbo.demo.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Consumer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[]{"META-INF/spring/dubbo-demo-consumer.xml"});
        context.start();
        // obtain proxy object for remote invocation
        DemoService demoService = (DemoService) context.getBean("demoService");
        // execute remote invocation
        String hello = demoService.sayHello("world");
        // show the result
        System.out.println(hello);
    }
}
```



基于这个链接，学习dubbo框架 http://dubbo.io/books/dubbo-user-book/