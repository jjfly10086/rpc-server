## RPC demo
based on Java NIO 
## 使用
暴露服务方：在项目启动中执行ProviderFactory.init()方法
```$xslt
<bean class="com.jwh.demo.ProviderFactory" init-method="init">
        <property name="port" value="8000"/>
</bean>
```
在需要暴露的服务加上@RpcProvider注解
```$xslt
@Service
@RpcProvider
public class UserServiceImpl xxx
```
调用方：启动中执行ReferenceFactory.init()方法
```$xslt
<bean class="com.jwh.demo.ReferenceFactory" init-method="init">
        <property name="hostName" value="192.168.17.148"/>
        <property name="port" value="8000"/>
</bean>
```
在需要注入的服务加上@RpcReference注解
```$xslt
@RestController
public class RestController{
    @RpcReference
    private IUserService userService;
}
```