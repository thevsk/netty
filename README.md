[TOC]
# TCP
```
IP  : X
PORT: 7999
```

报文格式:

`长度(4)报体`

报体：

```json
{
	"type": 1, // int 类型
	"uuid": null, // string 32 唯一识别码
	"data": {} // object 内容
}
```

报体类型：

| 数字 | 说明 |
| ------------ | ------------ |
| 0 | 心跳包 |
| 1 | 登录登出 |
| 2 | 操作 |
| 3 | 错误 |

## 心跳包

请求：

```json
{
	"type": 0,
	"uuid": null,
	"data": {
		"time": null // long 型 时间戳
	}
}
```

返回：

```json
{
	"type": 0,
	"uuid": null,
	"data": {
		"time": null // long 型 时间戳
	}
}
```

## 错误

### 参数缺失错误

返回：

```json
{
	"type": 3,
	"uuid": null,
	"data": {
		"state": "paramError",
		"msg": "参数缺失"
	}
}
```

### 未登录错误

返回：

```json
{
	"type": 3,
	"uuid": null,
	"data": {
		"state": "noLogin",
		"msg": "您未登录"
	}
}
```

## 登录

### 请求登录

发送：

```json
{
	"type": 1,
	"uuid": null,
	"data": {
		"userId": null // 用户id
	}
}
```

返回：

```json
{
	"type": 1,
	"uuid": null,
	"data": {
		"state": "online",
		"msg": "登录成功"
	}
}
```

### 被踢下线

返回：

```json
{
	"type": 1,
	"uuid": null,
	"data": {
		"state": "offline",
		"msg": "您在其他设备登录，您已经被踢下线，登录ip: xxx"
	}
}
```

## 操作

### 给所有TCP客户端发送消息

发送：

```json
{
	"type": 2,
	"uuid": null,
	"data": {
		"state": "sendAllTcpClient",
		"content": null
	}
}
```

返回：

```json
{
	"type": 2,
	"uuid": null,
	"data": {
		"state": "success"
	}
}
```

### 给所有WebSocket客户端发送消息

发送：

```json
{
	"type": 2,
	"uuid": null,
	"data": {
		"state": "sendAllWebSocketClient",
		"content": null
	}
}
```

返回：

```json
{
	"type": 2,
	"uuid": null,
	"data": {
		"state": "success"
	}
}
```

### 收到给TCP客户端的群发消息

返回：

```json
{
	"type": 2,
	"uuid": null,
	"data": {
		"state": "sendAllTcpClient",
		"content": null
	}
}
```

# WebSocket

```
IP  : ws://X/
PORT: 7998
```

报文格式:

`报体`

其他同TCP协议

## 操作

### 收到给WebSocket客户端的群发消息

返回：

```json
{
	"type": 2,
	"uuid": null,
	"data": {
		"state": "sendAllWebSocketClient",
		"content": null
	}
}
```
