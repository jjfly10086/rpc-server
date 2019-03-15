## RPC demo
based on Java NIO 
## 使用
1.开启服务支持：@EnableRpcService
```$xslt
@SpringBootApplication
@EnableRpcService
public class SpringSecurityApplication {
    
}
```
2.application.properties 配置zookeeper连接地址端口
```$xslt
   com.jwh.demo.register.services-root-dir=/services
   com.jwh.demo.register.zk-host=localhost
   com.jwh.demo.register.zk-port=2181
   com.jwh.demo.register.export-port=8082
   com.jwh.demo.register.session-timeout=5000
   com.jwh.demo.subscribe.services-root-dir=/services
   com.jwh.demo.subscribe.zk-host=localhost
   com.jwh.demo.subscribe.zk-port=2181
   com.jwh.demo.subscribe.session-timeout=5000
```
3.在需要暴露的服务加上@RpcProvider注解
```$xslt
@RpcProvider
public class UserServiceImpl xxx
```
4.在需要注入的服务加上@RpcReference注解
```$xslt
@RestController
public class RestController{
    @RpcReference
    private IUserService userService;
}
```