
    在发送和接收消息时，需要使用 @EnableBinding 注解
        BindableProxyFactory 类: 初始化由 @EnableBinding 注解所提供接口的工厂类
            同时实现了 MethodInterceptor 接口和 Bindable 接口。前者是AOP 中的方法拦截器，后者是标明能够绑定 Input 和 Output 的接口。
                invoke: 拦截方法根据 @Input 和 @Output 注解获取消息通道对象并进行缓存
                Bindable: 提供了对 Input 和 Output 的绑定和解绑操作
                bindOutputs方法 :
                    工具类 BindingService，该类提供了对 Input 和 Output 目标对象进行绑定的能力
                    bindOutputs 方法遍历输出目标并使用 BindingService 将可绑定的目标注册为生产者
                        bindProducer方法:根据配置将输出对象绑定到指定目标
                            doBindProducer方法：通过 Binder 的 bindProducer 方法完成了目标对象的绑定



    Binder 是一个接口，分别提供了绑定生产者和消费者的方法 bindProducer，bindConsumer
        如何获取一个 Binder
            工厂类 BinderFactory的getBinder 方法
                BinderFactory 的实现类也只有一个，即 DefaultBinderFactory
                    通过 getBinderInstance 获取真正的 Binder 实例
                        Binder<T, ?, ?> binder = binderProducingContext.getBean(Binder.class);
                        构建了一个上下文对象 ConfigurableApplicationContext，并通过该上下文对象获取实现了 Binder 接口的 Java bean
        AbstractBinder，这是一个基于Binder抽象类    
            重写bindProducer，bindConsumer通过 doBindConsumer 和 doBindProducer 抽象方法交由子类进行完成
            AbstractMessageChannelBinder: AbstractBinder 的子类 
                doBindProducer 方法： 
                    创建并配置 MessageHandler，将消息生产者与输出通道绑定，
                    使用 SendingHandler 作为代理处理消息发送，并最终将任务委托给实际的 producerMessageHandler。
                    SendingHandler 所使用的 producerMessageHandler 需要由 AbstractMessageChannelBinder 子类负责进行创建。
                doBindConsumer:
                    MessageProducer consumerEndpoint = createConsumerEndpoint(destination, group, properties);
                    consumerEndpoint.setOutputChannel(inputChannel);
                AbstractMessageChannelBinder 具有三个抽象方法，即 createProducerMessageHandler、postProcessOutputChannel 和 afterUnbindProducer



    RabbitMQ集成消息发送
        RabbitMessageChannelBinder#createProducerMessageHandler 用于完成消息的发送
                通过构建 RabbitTemplate（封装与 RabbitMQ 交互的模板类），并使用 AmqpOutboundEndpoint 来设置交换机名称
                    AmqpOutboundEndpoint #send 方法进行消息的发送
                       整合 AmqpOutboundEndpoint，使用 RabbitTemplate 发送消息，
                       并通过 MessageConverter 将 Spring Messaging 的 Message 转换为 AmqpMessage，实现消息从 Spring 到 RabbitMQ 的发送。
    
    RabbitMQ集成消息消费
        RabbitMessageChannelBinder 中与消息消费相关的是 createConsumerEndpoint 方法
            该方法最终返回的是一个 AmqpInboundChannelAdapter 对象: 是一种 InboundChannelAdapter，代表面向输入的通道适配器，提供了消息监听功能
               Listener implements ChannelAwareMessageListener, RetryListener->#onMessage
                  调用了 createAndSend 方法完成消息的创建和发送
                    实现消息从 RabbitMQ 到 Spring 的转换



public final Binding<MessageChannel> doBindProducer(final String destination, MessageChannel outputChannel,
final P producerProperties) throws BinderException {

    // 1. 获取生产者目标（如队列或主题）
    ProducerDestination producerDestination = this.provisioningProvider
            .provisionProducerDestination(destination, producerProperties);
    
    // 2. 创建消息处理器
    MessageHandler producerMessageHandler = createProducerMessageHandler(producerDestination, producerProperties, null);
    
    // 3. 将消息处理器绑定到 outputChannel
    outputChannel.subscribe(new SendingHandler(producerMessageHandler));

    // 4. 创建并返回 Binding 对象来管理生命周期
    return new DefaultBinding<>(destination, null, outputChannel, 
        producerMessageHandler instanceof Lifecycle ? (Lifecycle) producerMessageHandler : null);
}





