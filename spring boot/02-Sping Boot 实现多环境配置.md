## spring boot 实现多环境配置

#### 一，多环境配置的好处

1. 不同环境配置可以配置不同的参数

2. 便于部署，提高效率，减少出错

   ​

#### 二，Properties多环境配置

1. 激活配置活动项

   ```properties
   spring.profiles.active=dev
   ```


2. 添加其他配置文件

   application-dev.properties

   application-prod.properties

   application-test.properties

   applicaton.properties

3.  YAML多环境配置

   3.1. 配置激活选项

   ```yaml
   spring:
     profiles:
       active: dev
   ```

   3.2. 在配置文件添加三个英文状态下的短横线即可区分

   ```yaml
   ---
   spring:
     profiles: dev
   ```

#### 四，两种配置方式的比较

1. Properties配置多环境，需要添加多个配置文件，YAML只需要一个配件文件
2. 书写格式的差异，yaml相对比较简洁，优雅
3.  YAML的缺点：不能通过*@PropertySource*注解加载。如果需要使用*@PropertySource*注解的方式加载值，那就要使用**properties文件**。

