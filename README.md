## tools

### 1. dubbo-tools

#### 1.1 HystrixCluster

支持hystrix熔断的cluser扩展，支持动态对dubbo接口进行hystrix支持

1）使用步骤

> * 引入jar包
> * 设置消费端的cluster类型（cluster="hystrix"）
> * 使用HystrixConfigSubscriber订阅配置中心配置项
> * 继承Fallback接口，实现dubbo接口方法的fallback逻辑

2）配置格式样例（数组格式，每项对应一个接口方法）

```json
[
    {
        "interfaceName":"com.alibaba.dubbo.demo.DemoService",
        "methodName":"sayHello",
        "parameterTypes":[
            "java.lang.String"
        ],
        "groupKey":"test",
        "commandKey":"DemoService",
        "fallbackClass":"com.alibaba.dubbo.demo.consumer.dubbo.DemoFallback"
    }
]
```
