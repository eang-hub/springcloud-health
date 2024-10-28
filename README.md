    设置客户端信息
        通过继承 AuthorizationServerConfigurerAdapter 配置 Spring 授权服务器的客户端信息
            configure(AuthorizationServerEndpointsConfigurer) 配置授权服务器的端点，
            将 AuthenticationManager 和 UserDetailsService 注入来处理身份验证。
            然后，在 configure(ClientDetailsServiceConfigurer) 设置客户端信息，
            定义客户端ID、密钥（以 {noop} 不加密编码）、授权模式（包括密码模式、刷新令牌等）限定访问范围
    
        @EnableAuthorizationServer 注解会启用多个端点，其中 AuthorizationEndpoint 用于控制授权。
        通过继承 AuthorizationServerConfigurerAdapter 并重写 configure() 方法，可以配置端点的行为。
        因为使用密码模式，该模式需要身份认证，需在 AuthorizationServerEndpointsConfigurer 中指定 AuthenticationManager，用于校验用户名和密码。
        此外，指定 UserDetailsService 自定义用户信息服务，以替换默认的实现，确保安全认证过程符合自定义需求。

    设置用户认证信息
        设置用户认证信息所依赖的配置类是 WebSecurityConfigurer 类，提供了 WebSecurityConfigurerAdapter 类来简化该配置类的使用方式
        继承 WebSecurityConfigurerAdapter 类并且覆写其中的 configure() 的方法来完成配置工作。
            只需要指定用户名（User）、密码（Password）和角色（Role）这三项数据
        置了用户信息之后，AuthenticationManager 就会通过 authenticate 方法负责在用户登录时检查用户名和密码的有效性   
            如果匹配，认证成功，返回认证对象。
            如果不匹配，抛出 AuthenticationException 异常。

    Bootstrap 类中添加 @EnableResourceServer 注解，相当于就是声明了该服务中的所有内容都是受保护的资源。
    会对所有的 HTTP 请求进行验证以确定 Header 部分中是否包含 Token 信息，如果没有 Token 信息，则会直接限制访问
    将 Token 传递给 OAuth2 授权服务器的目的就是获取该 Token 中包含的用户和授权信息  
        继承 SpringHealthResourceServerConfiguration 类并覆写它的 configure 方法
            用户层级的权限访问控制:使用HttpSecurity 对象配置anyRequest().authenticated() 方法指定了访问该服务的任何请求都需要进行验证。
            用户+角色层级的权限访问控制:  HttpSecurity 中通过 antMatchers() 和 hasRole() 方法指定想要限制的资源和角色
            用户+角色+操作层级的权限访问控制: 在 HttpSecurity 的 antMatchers() 中添加 HttpMethod.PUT 限定。
        OAuth2RestTemplate 工具类：在 HTTP 请求中传播 Token
            - 使用 `new OAuth2RestTemplate(details, oauth2ClientContext);` 实例化。
              - 其中：
                  - `details` 是 `OAuth2ProtectedResourceDetails` 的实例，包含 `clientId`、`clientSecret`、`scope` 等属性。
                  - `oauth2ClientContext` 是 `OAuth2ClientContext` 的实例，用于管理请求会话。
              - Token 会保存在 `OAuth2ClientContext` 中，保证每个用户请求的信息隔离，确保状态分离。



auth-server 服务内容
```
server:
   port: 8080
   
logging:
    level:
      com.netflix: WARN
      org.springframework.web: WARN
      com.tianyalan: INFO

eureka:
  instance:
    preferIpAddress: true
  client:
    registerWithEureka: true
    fetchRegistry: true
    serviceUrl:
        defaultZone: http://localhost:8761/eureka/
```

```java

@Configuration
public class SpringHealthAuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager).userDetailsService(userDetailsService);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		clients.inMemory().withClient("springhealth").secret("{noop}springhealth_secret")
				.authorizedGrantTypes("refresh_token", "password", "client_credentials")
				.scopes("webclient", "mobileclient");
	}
}


@Configuration
public class SpringHealthWebSecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.inMemoryAuthentication().withUser("springhealth_user").password("{noop}password1").roles("USER").and()
                .withUser("springhealth_admin").password("{noop}password2").roles("USER", "ADMIN");
    }
}



@SpringCloudApplication
@RestController
@EnableResourceServer
@EnableAuthorizationServer
public class AuthServerApplication {

    @RequestMapping(value = "/userinfo" , produces = "application/json")
    public Map<String, Object> user(OAuth2Authentication user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", user.getUserAuthentication().getPrincipal());
        userInfo.put("authorities", AuthorityUtils.authorityListToSet(user.getUserAuthentication().getAuthorities()));
        return userInfo;
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}

```

intervention-service 服务内容
```
server:
   port: 8084
spring:
  cloud:
    stream:
      bindings:
        userInfoChangedChannel:
          destination: userInfoChangedTopic
          content-type: application/json
          group: interventionGroup
      kafka:
        binder:
          zk-nodes: localhost
          brokers: localhost
    config:
     enabled: true
     uri: http://localhost:8888


hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds:1000
feign:
  hystrix:
    enabled: true

eureka:
  instance:
    preferIpAddress: true
  client:
    registerWithEureka: true
    fetchRegistry: true
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/


security:
  oauth2:
    resource:
      userInfoUri: http://localhost:8080/userinfo
    client:
      grant-type: client_credentials


logging:
#    level:
      com.netflix: WARN
      org.springframework.web: WARN
      com.tianyalan: INFO

```

```java

@SpringCloudApplication
@EnableBinding(Sink.class)
@EnableResourceServer
@EnableOAuth2Client
public class InterventionApplication {



	@Bean
	@LoadBalanced
	public OAuth2RestTemplate oauth2RestTemplate(
												 OAuth2ProtectedResourceDetails details) {
		return new OAuth2RestTemplate(details);
	}

	@Bean
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory)
			throws UnknownHostException {
		RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
		template.setConnectionFactory(redisConnectionFactory);

		return template;
	}
    public static void main(String[] args) {
        SpringApplication.run(InterventionApplication.class, args);
    }
}


@Component
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);

    @Autowired
    OAuth2RestTemplate restTemplate;

    public User getUserByUserName(String userName){

        logger.debug("Get user: {}", userName);

        ResponseEntity<User> restExchange =
                restTemplate.exchange(
                        "http://zuulservice:5555/springhealth/user/users/{userName}",
                        HttpMethod.GET,
                        null, User.class, userName);

        User user = restExchange.getBody();

        return user;
    }
}

使用postman访问http://localhost:8084/interventions/ccc/userName233/deviceCode2333
添加 Token 
报错"error": "access_denied",
```