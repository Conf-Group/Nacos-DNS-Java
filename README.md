# Nacos-DNS-Java

## Nacos DNS protocol implementation

[Chinese document](README_ZH.md)

### use

> Start the command

```bash
./bin/startup.sh
```

> The configuration file

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
  contextPath: /nacos
  namespaceId: ${namesapceId}
```

### Profile interpretation

| configuration name | value example | description |
| :---- | :---- | :---- |
| loadBalancer|  RandomLoadBalancer | Load balancing selector, alternative path [RandomLoadBalancer、RoundRobinLoadBalancer、WeightLoadBalancer] |
| backendNameServer | 8.8.8.8 | Backup the NameServer |
| resolver.serviceGroupMatch | ^nacos\t\w+\w+: liaochuntao | key-value, according to the regular expression of the service name to match the specific group to get the service to nacos|
| nacosConfig.endpoint | address.nacos.com | address server domain name |
| nacosConfig.serverAddr | 127.0.0.1:8847,127.0.0.1:8848,127.0.0.1:8849 | nacos cluster node address string |
| nacosConfig.username | nacos | when permissions are enabled, the user name needs to be set |
| nacosConfig.password | nacos | when permissions are enabled, the password needs to be set |
| nacosConfig.namespaceId | 192af796-0761-455d-a526-219b66ef6ce1 | the ID information for the namespace |


### How to build

```bash
./build_release.sh
```
