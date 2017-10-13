## spring boot cors跨域问题解决方案

#### 一，跨域解决方案

跨域问题有多种解决方案，比如jsonp，cors，iframe等

- **jsonp**

  只支持get请求方法，不支持post请求，对老式的浏览器比较好的支持

- **cors**

  CORS是现代浏览器支持跨域资源请求的一种方式，全称是"跨域资源共享"（Cross-origin resource sharing），当使用XMLHttpRequest发送请求时，浏览器发现该请求不符合同源策略，会给该请求加一个请求头：Origin，后台进行一系列处理，如果确定接受请求则在返回结果中加入一个响应头：Access-Control-Allow-Origin;浏览器判断该相应头中是否包含Origin的值，如果有则浏览器会处理响应，我们就可以拿到响应数据，如果不包含浏览器直接驳回，这时我们无法拿到响应数据。



#### 二，sprint boot 实现cros跨域



