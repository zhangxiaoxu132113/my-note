## Spring Boot 日志配置-logback和log4j2

**支持日志框架 **: spring boot支持多种日志框架，如Java Util Logging, Log4j2 LogBack，默认是使用logback

**配置方式**: 默认配置文件配置和引用外部配置文件配置

#### 一、 默认配置文件配置(不建议使用：不够灵活，对log4j2等不够友好)

```properties
# 日志文件名，比如：roncoo.log，或者是 /var/log/roncoo.log

logging.file=roncoo.log 

# 日志级别配置，比如： logging.level.org.springframework=DEBUG

logging.level.*=info

logging.level.org.springframework=DEBUG

```

####二、 引用外部配置文件

1. logback配置方式：

   spring boot默认会加载classpath:logback-spring.xml或者classpath:logback-spring.groovy

   使用自定义配置文件，配置方式为：

   logging.config=classpath:logback-roncoo.xml

   *注意：不要使用logback这个来命名，否则spring boot将不能完全实例化*

2. 使用基于spring boot的配置

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>  
   <configuration>  
     
       <!-- 文件输出格式 -->  
       <property name="PATTERN" value="%-12(%d{yyyy-MM-dd HH:mm:ss.SSS}) |-%-5level [%thread] %c [%L] -| %msg%n" />  
       <!-- test文件路径 -->  
       <property name="TEST_FILE_PATH" value="D:/DevData/logs" />  
     
       <!-- 开发环境 -->  
       <springProfile name="dev">  
           <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">  
               <encoder>  
                   <pattern>${PATTERN}</pattern>  
               </encoder>  
           </appender>  
             
           <logger name="com.roncoo.education" level="debug"/>  
     
           <root level="info">  
               <appender-ref ref="CONSOLE" />  
           </root>  
       </springProfile>  
     
       <!-- 测试环境 -->  
       <springProfile name="test">  
           <!-- 每天产生一个文件 -->  
           <appender name="TEST-FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">  
               <!-- 文件路径 -->  
               <file>${TEST_FILE_PATH}</file>  
               <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">  
                   <!-- 文件名称 -->  
                   <fileNamePattern>${TEST_FILE_PATH}/info.%d{yyyy-MM-dd}.log</fileNamePattern>  
                   <!-- 文件最大保存历史数量 -->  
                   <MaxHistory>100</MaxHistory>  
               </rollingPolicy>  
                
               <layout class="ch.qos.logback.classic.PatternLayout">  
                   <pattern>${PATTERN}</pattern>  
               </layout>  
           </appender>  
             
           <root level="info">  
               <appender-ref ref="TEST-FILE" />  
           </root>  
       </springProfile>  
   </configuration>

   ```

   ​

