### sprint boot 错误处理机制

我们在做Web应用的时候，请求处理过程中发生错误是非常常见的情况。我们需要通过一个**错误处理机制**来实现对错误请求进行处理。下面讲解几种错误的处理

1. **ErrorController**

   sprint boot 默认提供了/error来实现对所有错误请求的处理，其背后处理的就是ErrorController来实现的。我们可以通过覆盖这个接口的方法来定义我们的错误处理。

   创建Controller，实现ErrorContorller的接口，实现接口的方法，返回错误的路径。

   ```java
   /**
    * Created by zhang miaojie on 2017/10/9.
    */
   @Controller
   @RequestMapping("/error")
   public class MyErrorController implements ErrorController {

       @Override
       public String getErrorPath() {
           return "/error";
       }

       @RequestMapping
       @ResponseBody
       public Result doHandleError1() {

           return new Result(-1, "网络请求异常，请稍后重试!");
       }
   }
   ```

2. #### 添加自定义的错误页面

   1. html静态页面：

      在resources/public/error/ 下定义如添加404页面： resources/public/error/404.html页面，中文注意页面编码


   1. 模板引擎页面：

      在templates/error/下定义如添加5xx页面： templates/error/5xx.ftl注：templates/error/ 这个的优先级比较 resources/public/error/高

3. #### 使用注解@ControllerAdvice

   ```java
   /**
        * 统一异常处理
        * 
        * @param exception
        *            exception
        * @return
        */
       @ExceptionHandler({ RuntimeException.class })
       @ResponseStatus(HttpStatus.OK)
       public ModelAndView processException(RuntimeException exception) {
           logger.info("自定义异常处理-RuntimeException");
           ModelAndView m = new ModelAndView();
           m.addObject("roncooException", exception.getMessage());
           m.setViewName("error/500");
           return m;
       }

       /**
        * 统一异常处理
        * 
        * @param exception
        *            exception
        * @return
        */
       @ExceptionHandler({ Exception.class })
       @ResponseStatus(HttpStatus.OK)
       public ModelAndView processException(Exception exception) {
           logger.info("自定义异常处理-Exception");
           ModelAndView m = new ModelAndView();
           m.addObject("roncooException", exception.getMessage());
           m.setViewName("error/500");
           return m;
       }
   ```

