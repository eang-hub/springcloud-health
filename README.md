## **InstanceRegistry 接口**

- **功能**：保存注册信息，数据结构为 `<服务名, <服务id, Lease 对象>>`。
- **继承**：实现了 `LeaseManager` 和 `LookupService` 接口。
- **扩展**：通过 `PeerAwareInstanceRegistry` 接口，具体实现类为 `PeerAwareInstanceRegistryImpl`。

### **LeaseManager 接口**

- **功能**：负责服务的注册、续约、取消和剔除等操作。
- **方法**：`register()` 方法用于添加注册信息。

### **LookupService 接口**

- **功能**：管理应用程序与服务实例的关系。
- **方法**：

  - `getApplication()`：从缓存中获取应用信息。
  - 通过 `responseCache.get(cacheKey)` 获取缓存数据。
- **实现**：`ResponseCache` 接口的实现类为 `ResponseCacheImpl`，包含：

  - `readOnlyCacheMap`
  - `readWriteCacheMap`
- **同步机制**：通过定时任务 `CacheUpdateTask` 实现缓存的同步更新。

### **PeerAwareInstanceRegistryImpl 类**

- **注册方法**：`register()` 实现了节点间状态同步。
  - **状态同步**：通过 `replicateToPeers()` 方法实现。
  - **不同 Action 的处理**：调用 `PeerEurekaNode` 的不同方法。
    - **StatusUpdate Action**：触发 `PeerEurekaNode` 的 `statusUpdate` 方法。
    - **通信机制**：使用 `replicationClient.statusUpdate` 完成节点间的通信。
      - `replicationClient` 是 `HttpReplicationClient` 接口的实例。
      - `HttpReplicationClient` 继承自 `EurekaHttpClient` 接口。
      - `EurekaHttpClient` 的实现类为 `JerseyReplicationClient`。

---

## **EurekaClient 接口**

- **继承**：实现了 `LookupService` 接口，具体实现类为 `DiscoveryClient`。
- **主要方法**：

  - `register()`, `renew()` 等方法用于服务注册。
  - `initScheduledTasks()`：管理定时任务，包括：
    - 缓存刷新（`cacheRefresh`）
    - 心跳（`heartbeat`）
    - 服务实例复制（`InstanceInfoReplicator`）
- **服务注册流程**：

  - `register()` 方法在 `InstanceInfoReplicator` 的 `run()` 方法中被执行。
  - 使用 `eurekaTransport.registrationClient.register()` 获取服务注册信息。
  - `CacheRefreshThread` 线程负责具体操作：
    - `fetchRegistry()` 更新注册信息。
    - `getAndUpdateDelta()` 方法用于增量拉取服务实例数据，确保客户端数据与 Eureka 服务器数据的一致性。

### **EurekaTransport 类**

- **位置**：`DiscoveryClient` 类中的内部类。
- **功能**：定义 `registrationClient` 变量以实现服务注册。

  - **类型**：`registrationClient` 是 `EurekaHttpClient` 接口的实例。
  - **主要方法**：包括 `register()`, `cancel()`, `sendHeartBeat()`, `statusUpdate()`, `getApplication()` 等。
- **实现类**：`EurekaHttpClientDecorator` 通过 `execute(RequestExecutor requestExecutor)` 抽象方法包装 `EurekaHttpClient`。
- **构建客户端**：

  - 通过 `EurekaHttpClientFactory` 类构建具体的 `EurekaHttpClient` 实现，如 `RetryableEurekaHttpClient` 和 `MetricsCollectingEurekaHttpClient`。
  - `EurekaHttpClients` 工具类创建被 `RedirectingEurekaHttpClient`、`RetryableEurekaHttpClient` 和 `SessionedEurekaHttpClient` 包装的 `EurekaHttpClient`。
- **远程请求执行**：

  - 原始的 `EurekaHttpClient` 通过 `TransportClientFactory` 创建。
  - **实现类**：`JerseyEurekaHttpClientFactory` 返回不同的客户端实现，如 `JerseyEurekaHttpClient`。
    - **Jersey 客户端**：通过 `EurekaJerseyClient` 获取，后者使用 `ApacheHttpClient4` 对象完成 REST 调用。












## 要分别启动使用不同配置文件的 `eureka-server`，可以通过以下方式实现：

### 1. 使用 Spring Boot 的 `--spring.profiles.active` 参数

在 Spring Boot 项目中，可以通过 `application.yml` 文件的 `spring.profiles.active` 指定要加载的具体配置文件。如果有不同的配置文件（如 `application-eureka1.yml` 和 `application-eureka2.yml`），可以通过运行时参数切换加载。

#### 启动命令：

* 启动 `application-eureka1.yml` 配置：
  ```bash
  java -jar eureka-server.jar --spring.profiles.active=eureka1
  ```
* 启动 `application-eureka2.yml` 配置：
  ```bash
  java -jar eureka-server.jar --spring.profiles.active=eureka2
  ```

---

### 2. 修改 `application.yml` 文件支持多配置

在 `application.yml` 文件中，可以通过 `spring.profiles` 配置不同的配置文件。示例如下：

#### `application.yml`

```yaml
spring:
  profiles:
    active: eureka1 # 默认使用 eureka1 配置
```

#### `application-eureka1.yml`

```yaml
server:
  port: 8761
eureka:
  instance:
    hostname: eureka1
  client:
    register-with-eureka: false
    fetch-registry: false
```

#### `application-eureka2.yml`

```yaml
server:
  port: 8762
eureka:
  instance:
    hostname: eureka2
  client:
    register-with-eureka: false
    fetch-registry: false
```

#### 启动命令：

通过运行时参数切换 `spring.profiles.active`：

```bash
java -jar eureka-server.jar --spring.profiles.active=eureka1
java -jar eureka-server.jar --spring.profiles.active=eureka2
```

---

### 3. 配置多个 `@Profile` 注解的方式（代码级别区分）

可以在代码中使用 `@Profile` 注解，根据当前激活的配置文件加载不同的配置。例如：

```java
@Configuration
@Profile("eureka1")
public class Eureka1Config {
    // 配置 eureka1 的相关逻辑
}

@Configuration
@Profile("eureka2")
public class Eureka2Config {
    // 配置 eureka2 的相关逻辑
}
```

然后通过 `--spring.profiles.active` 指定 `eureka1` 或 `eureka2`。

---

### 4. 使用多个启动脚本

为方便开发，可以为不同的配置文件编写独立的启动脚本。

#### 启动脚本示例：

* `start-eureka1.sh`：
  ```bash
  #!/bin/bash
  java -jar eureka-server.jar --spring.profiles.active=eureka1
  ```
* `start-eureka2.sh`：
  ```bash
  #!/bin/bash
  java -jar eureka-server.jar --spring.profiles.active=eureka2
  ```

然后分别运行：

```bash
bash start-eureka1.sh
bash start-eureka2.sh
```

---

### 5. 使用 IDE 启动（如 IntelliJ IDEA）

如果你使用的是 IntelliJ IDEA，可以在配置运行参数时设置激活的配置文件：

1. **打开运行/调试配置**：
   * 点击 `Run > Edit Configurations...`
2. **添加运行参数**：
   * 在 `Program Arguments` 中添加：

     ```
     --spring.profiles.active=eureka1
     ```

     或

     ```
     --spring.profiles.active=eureka2
     ```
3. **分别启动不同配置**：
   * 分别创建两份运行配置（一个 `eureka1`，一个 `eureka2`），然后分别运行即可。

---

### 总结

推荐通过 `--spring.profiles.active` 的方式分别启动两种配置文件，
