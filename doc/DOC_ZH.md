# Gamer服务框架介绍

Gamer是用Java语言编写的游戏服务器，使用Netty4进行底层网络通信。支持tcp、http、http2、webSocket多种网络协议，可以用来开发手游、H5小游戏，Web服务器。

## Gamer模块概述

- gamer-common 其他模块公用的工具集合
- gamer-registry-api 注册中心抽象API
- gamer-registry-zookeeper 注册中心zookeeper实现
- gamer-registry-etcd 注册中心Etcd3实现
- gamer-remoting-api 远程通信抽象API
- gamer-remoting-netty 远程通信Netty4实现
- gamer-core-api Servlet、MVC、Push机制核心抽象API
- gamer-core-netty 核心模型Netty4实现
- gamer-rpc-api RPC通信抽象API
- gamer-rpc-tcp RPC基于tcp
- gamer-rpc-http RPC基于http
- gamer-rpc-websocket RPC基于WebSocket
- gamer-spring-boot-starter SpringBoot集成
- gamer-swagger Swagger2文档实现

## 使用Gamer
### 快速开始
#### 获取Gamer:
```
git clone https://github.com/thinkerwolf/Gamer.git
```

#### 服务端启动

使用IDEA打开项目，在`gamer-example/gamer-example-spring`下找到`ExampleSpringApplication`并运行，如果顺利的话，可以看到控制有如下打印:

```
 ________  ________  _____ ______   _______   ________     
|\   ____\|\   __  \|\   _ \  _   \|\  ___ \ |\   __  \    
\ \  \___|\ \  \|\  \ \  \\\__\ \  \ \   __/|\ \  \|\  \   
 \ \  \  __\ \   __  \ \  \\|__| \  \ \  \_|/_\ \   _  _\  
  \ \  \|\  \ \  \ \  \ \  \    \ \  \ \  \_|\ \ \  \\  \| 
   \ \_______\ \__\ \__\ \__\    \ \__\ \_______\ \__\\ _\ 
    \|_______|\|__|\|__|\|__|     \|__|\|_______|\|__|\|__|
   :: Gamer ::                                   (v1.0.0)
...
2020-07-28 14:36:32 [INFO ] [NettyBoss_tcp-1-1] c.t.gamer.netty.NettyServer - Listen @tcp on @9080 success
...
2020-07-28 14:36:32 [INFO ] [NettyBoss_http-4-1] c.t.gamer.netty.NettyServer - Listen @http on @8070 success
...
```
服务端已经启动完毕，tcp端口是`9080`，http端口是 `8070`。

#### http测试
打开浏览器，输入`http://localhost:8070/hello/index?name=gamer`:

[image](https://github.com/thinkerwolf/Gamer/blob/master/doc/hello_index.png)

打开浏览器，输入`http://localhost:8070/hello/api?name=gamer`:
```json
{"say":"Hello gamer"}
```

#### tcp测试
在`gamer-example/gamer-example-spring`下找到`ExampleTcpClient`并运行:
```
Received {"say":"Hello gamer"}
```