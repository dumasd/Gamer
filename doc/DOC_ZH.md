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

## 使用Gamer开发游戏服务器

#### 获取Gamer
```
git clone https://github.com/thinkerwolf/Gamer.git
```

#### 打开gamer-test工程

![SpringApplicationMain](https://github.com/thinkerwolf/Gamer/tree/master/doc/test_main.png "optional title")

#### 运行SpringApplicationMain

#### 运行效果

```
 ________  ________  _____ ______   _______   ________     
|\   ____\|\   __  \|\   _ \  _   \|\  ___ \ |\   __  \    
\ \  \___|\ \  \|\  \ \  \\\__\ \  \ \   __/|\ \  \|\  \   
 \ \  \  __\ \   __  \ \  \\|__| \  \ \  \_|/_\ \   _  _\  
  \ \  \|\  \ \  \ \  \ \  \    \ \  \ \  \_|\ \ \  \\  \| 
   \ \_______\ \__\ \__\ \__\    \ \__\ \_______\ \__\\ _\ 
    \|_______|\|__|\|__|\|__|     \|__|\|_______|\|__|\|__|
   :: Gamer ::                                   (v1.0.0)
```