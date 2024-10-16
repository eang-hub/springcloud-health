### **负载均衡原理与实现**

---

## **ILoadBalancer：核心组件与职责**
ILoadBalancer 负责从注册中心中获取服务器列表并选择一个服务实例来进行调用。

- **核心组件**：
    - **IRule**：抽象了负载均衡策略，用于实现不同的算法来选择服务器。
    - **IPing**：判断目标服务是否存活。
    - **LoadBalancerStats**：记录负载均衡的运行信息，提供统计支持。

### **BaseLoadBalancer 类**
- **内部维护**：
    - `allServerList`：所有服务器的列表。
    - `upServerList`：存活的服务器列表。

- **ILoadBalancer 定义的方法**：
    - **addServers**：向服务器列表中添加新的服务实例，并更新维护的列表。
    - **getReachableServers**：获取可用的服务器实例。
    - **getAllServers**：获取所有服务器实例。

- **chooseServer**：根据特定规则选择服务器。主要使用以下负载均衡算法：
    - **静态算法**：
        - 随机（Random）
        - 轮询（Round Robin）
        - 加权轮询（Weighted Round Robin）

    - **动态算法**：
        - 源 IP 哈希算法
        - 最少连接数算法
        - 服务调用时延算法

---

### **IRule 接口：负载均衡策略**
IRule 是负载均衡策略的抽象接口，其实现类提供了不同类型的路由策略：

- **静态策略（无状态）**：
    - **RandomRule**：随机选择服务器。
    - **RoundRobinRule**：轮询选择服务器。

- **动态策略（基于服务器状态）**：
    - **AvailabilityFilteringRule**：过滤掉不可用的服务器。
    - **WeightedResponseTimeRule**：基于服务器响应时间的加权策略。

- **RetryRule**：重试机制，若初次选择失败则重试其他服务器。

---

## **@LoadBalanced 注解与 RestTemplate 的客户端负载均衡**
使用 **@LoadBalanced** 注解的 RestTemplate 能自动具备客户端负载均衡能力。

- **LoadBalancerAutoConfiguration 自动配置类**：
    - 维护一个被 **@LoadBalanced** 修饰的 RestTemplate 列表。
    - **restTemplateCustomizer**：向 RestTemplate 的拦截器列表中添加 **LoadBalancerInterceptor**。

- **LoadBalancerInterceptor 拦截器**：
    - 通过 **LoadBalancerClient** 选择具体的服务实例。
    - **intercept 方法**：调用 **LoadBalancerClient** 的 `execute` 方法来实现请求的负载均衡。

---

## **LoadBalancerClient 接口**
- **继承自**：ServiceInstanceChooser 接口
- **核心方法**：`choose` 方法，用于根据负载均衡策略选择服务实例。

### **RibbonLoadBalancerClient 实现类**
- **choose 方法**：最终调用 `getServer` 方法选择服务器实例，执行负载均衡逻辑。

---

### **总结**
- **ILoadBalancer** 是负载均衡的核心，内部依赖 **IRule** 和 **IPing** 等组件。
- **BaseLoadBalancer** 维护服务器列表，并通过不同的算法实现负载均衡策略。
- 使用 **@LoadBalanced** 注解的 RestTemplate，通过 **LoadBalancerInterceptor** 实现客户端负载均衡。
- **LoadBalancerClient** 提供了关键的服务选择逻辑，确保服务调用分发至合适的实例。

