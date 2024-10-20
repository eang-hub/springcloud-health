
    @EnableConfigServer 注解是理解 Spring Cloud Config 服务器端组件的入口
        @Import 注解引入了 ConfigServerConfiguration
            定义了一个 Marker 空类，提供一个启动条件
                Marker启动条件的唯一使用者: ConfigServerAutoConfiguration
                    存在@ConditionalOnBean(ConfigServerConfiguration.Marker.class)
                    @Import->EnvironmentRepositoryConfiguration
                        存储配置信息的地方: EnvironmentRepository接口
                            DefaultRepositoryConfiguration类
                                @ConditionalOnMissingBean(EnvironmentRepository.class) 为EnvironmentRepository接口提供默认
    
                                GitRepositoryConfiguration 继承了这个 DefaultRepositoryConfiguration
                                    他提供的 EnvironmentRepository 就是 MultipleJGitEnvironmentRepository
                                        MultipleJGitEnvironmentRepository 则继承了抽象类 JGitEnvironmentRepository
                                            当服务器启动时，在 JGitEnvironmentRepository 中会决定是否调用 initClonedRepository() 方法来完成从远程 Git 仓库 Clone 代码。
                                                 AbstractScmEnvironmentRepository 实现了 EnvironmentRepository 接口，同时也是 JGitEnvironmentRepository 的父类，       
                                                    Environment findOne方法:构建 NativeEnvironmentRepository并调用他的findOne方法
                                                        NativeEnvironmentRepository类实现了 EnvironmentRepository 接口
                                                            |findOne方法: |委托 PassthruEnvironmentRepository 完成配置文件的读取，
                                                                        |然后通过 clean 方法完成本地文件地址与远程仓库之间地址的转换
                                                                        |ConfigFileApplicationListener 来监听配置文件的变化。    
    
                                                            |配置信息最终通过 EnvironmentController 暴露给客户端
                                                                |EnvironmentRepositor具体某一个 EnvironmentRepository 的实例 
                                                                |ObjectMapper 用于当将结果序列化成 JSON 格式的配置数据。
     


    ConfigServicePropertySourceLocator 类       
         #getRemoteEnvironment: 利用 RestTemplate 工具类执行 HTTP 请求获取服务器端提供的配置信息
            ConfigServicePropertySourceLocator#locate 方使用到#getRemoteEnvironment
                ConfigServicePropertySourceLocator实现了PropertySourceLocator
                    PropertySourceLocator 肯定被一个自动配置类所引用:PropertySourceBootstrapConfiguration 
                        |实现了 ApplicationContextInitializer 接口中的 initialize 方法，
                        |而所有的 ApplicationContextInitializer 都会在 Spring Boot 应用程序启动时进行加载。
                        
                        |当类路径中引入了 Spring Cloud Config 之后，一个 ConfigServicePropertySourceLocator 实例就会被构建
                        |并保存在 PropertySourceBootstrapConfiguration 的 propertySourceLocators 数组中。
                        |然后，我们会遍历所有 propertySourceLocators 的 locate 方法，从而完成对远程服务配置信息的读取。 

                        |setPropertySourceLocators 方法: 注入 ropertySourceLocators 数组
                            ConfigServiceBootstrapConfiguration 配置类
                                自动实例化一个 ConfigServicePropertySourceLocator


    调用 /actuator/bus-refresh 端点
        RefreshBusEndpoint 端点类
            发布了一个新的 RefreshRemoteApplicationEvent 事件
                 RefreshRemoteApplicationEvent 事件的监听者 RefreshListener
                    RefreshListener implements ApplicationListener<RefreshRemoteApplicationEvent>
                         #onApplicationEvent
                             ContextRefresher#refresh()方法进行配置属性的刷新


    客户端实时获取服务器端所更新的配置信息
        通过 Eureka 获取所有 Spring Cloud Config 服务的实例，从而在分布式环境下获取配置信息
        ConfigServerInstanceProvider 来完成与 Eureka 之间的交互   
            DiscoveryClient # getInstances() 方法从 Eureka 中获取 Spring Cloud Config 服务器实例
                ConfigServerInstanceProvider 的调用者是 DiscoveryClientConfigServiceBootstrapConfiguration
                    系统生成 `ContextRefreshedEvent` 事件时，会触发 `onApplicationEvent` 方法，进而调用 `startup()`，最终执行 `refresh()` 方法。