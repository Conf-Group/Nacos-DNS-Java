# Nacos-DNS-Java

## Nacos DNS 协议实现

### 使用方式

> 启动命令

```bash
./bin/startup.sh
```

> 配置文件

```yaml
loadBalancer: RandomLoadBalancer
backendNameServer: 8.8.8.8

resolver:
  serviceGroupMatch:
    ^nacos\t\w+\w+: liaochuntao

nacosConfig:
  endpoint: xxx(Use this parameter if you are using address server mode)
  serverAddr: 127.0.0.1:8847,127.0.0.1:8848,127.0.0.1:8849
  username: xxx
  password: xxx
  namespaceId: ${namesapceId}
```

### 配置文件解释

| 名称 | 配置示例 | 描述 |
| :---- | :---- | :---- |
| loadBalancer|  RandomLoadBalancer | 负载均衡选择器, 可以选择的负载均衡器 [RandomLoadBalancer、RoundRobinLoadBalancer、WeightLoadBalancer] |
| backendNameServer | 8.8.8.8 | 后备的NameServer |
| resolver.serviceGroupMatch | ^nacos\t\w+\w+: liaochuntao | key-value结构, 根据服务名的正则表达式，去匹配对应的group，从而正确的去nacos拉取服务信息|
| nacosConfig.endpoint | address.nacos.com | 地址服务器域名或者IP |
| nacosConfig.serverAddr | 127.0.0.1:8847,127.0.0.1:8848,127.0.0.1:8849 | nacos集群节点地址串 |
| nacosConfig.username | nacos | 在启用权限时，需要配置用户名 |
| nacosConfig.password | nacos | 在启用权限时，需要配置密码 |
| nacosConfig.namespaceId | 192af796-0761-455d-a526-219b66ef6ce1 | 命名空间ID信息 |


### 如何构建

```bash
./build_release.sh
```
