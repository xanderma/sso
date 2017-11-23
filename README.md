# sso
本程序演示了单点登录的基本实现

# 简介

当网站做得越来越大时，并分离为多个子站时，这时候就需要单点登录来实现多个子站共享登录信息。

本程序使用了Dubbo作为Rcp框架，使用CAS协议作为标准。

# 相关代码

单实例登录代码：https://github.com/xanderma/sso-single

多实例登录代码：https://github.com/xanderma/sso-mult

单点登录代码：https://github.com/xanderma/sso

# 启动程序

1、配置以下host
```
127.0.0.1 tomcat1.com
127.0.0.1 tomcat2.com
127.0.0.1 abc.tomcat2.com
```

2、安装Redis，端口为6379

3、首先启动SsoServerApplication.java，来启动单点登录服务器（8088端口）

4、再启动SsoClientApplication.java来启动一个子站Demo（8089端口）

6、访问http://tomcat2.com:8089 输入任意用户名登录

7、访问http://abc.tomcat2.com:8089 发现已经不需要再次登录

# 原理
访问网站时，会从Cookie中拿ticket，再拿这个ticket通过单点登录服务器验证是否是否登录，如果没有登录则跳转到单点登录服务器的登录页面，
登录成功之后单点登录服务器会创建一个ticket保存到Redis中，并跳转回原页面并附带该ticket，原页面把该ticket保存到Cookie中用于下次验证。

程序使用了Dubbo作为Rpc框架，export模块和sso-server应该是属于同一个项目的，而sso-client应该属于单独一个项目。这里为了方便将他们合在了一起。
在实际使用时应该将export模块和sso-server模块放在一起作为一个项目，并将export模块打包成jar包，然后sso-client项目引用这个jar包。
