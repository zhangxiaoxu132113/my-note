## spring boot 配置文件详解:Properties,YAML

### 一，配置文件的生效顺序，会对值进行覆盖

1. @TestPropertySource 注解
2. 命令行参数
3. Java系统属性（**System.getProperties()**）
4. 操作系统环境变量
5. 只有在random.*里包含的属性会产生一个**RandomValuePropertySource**
6. 在打包的jar外的应用程序配置文件（**application.properties**，包含YAML和profile变量）
7. 在打包的jar内的应用程序配置文件（application.properties，包含YAML和profile变量）
8. 在@Configuration类上的**@PropertySource**注解
9. 默认属性（使用**SpringApplication.setDefaultProperties**指定）

### 二，属性占位符

　　当**application.properties**里的值被使用时，它们会被存在的Environment过滤，所以你能够引用先前定义的值（比如，系统属性）。

```properties
roncoo.name=www.roncoo.com

roncoo.desc=${roncoo.name} is a domain name

```

### 三，Application属性文件，按照优先级排序，位置高的将覆盖位置低的

1. 当前目录下的一个/config子目录
2. 当前目录
3. 一个classpath下的/config包
4. classpath根路径（root）

这个列表是按**优先级排序**的（列表中位置高的将覆盖位置低的）

###四，使用YAML代替Properties配置文件

yaml是spring boot建议使用的一种配置文件，使用yaml的话，需要记住yaml的三大规则

**1.规则一：缩进**

  yaml使用一个固定的缩进风格表示数据层结构关系，Saltstack需要每个缩进级别由两个空格组成。一定不能使用tab键

**2.规则二：冒号**

  每个冒号后面一定要有一个空格（以冒号结尾不需要空格，表示文件路径的模版可以不需要空格）

**3.规则三：短横线**
 想要表示列表项，使用一个短横杠加一个空格。多个项使用同样的缩进级别作为同一个列表的一部分

*注意写法：冒号后要加个空格*

如果在同级目录下有application.properties, application.yaml两种配置文件，启动项目优先加载application.properties这个配置文件。	

实例

```properties
eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry:  false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}: ${server.port}/eureka/
 
  server:
    enableSelfPreservation: false
    waitTimeInMsWhenSyncEmpty: 0
 
 
spring:
  application:
    name: random-image-microservice
 
server:
  port: 9999
 
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
    healthcheck:
      enabled: true
```



### 五，常见的一些配置项的介绍

```properties
#端口配置
server.port=8010
#时间格式化
spring.jackson.date-format=yyyy-MM-dd HH:mm:ssl
#时区
spring.jackson.time-zone=Asia/Chongqing
```

### 



