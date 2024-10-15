

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

