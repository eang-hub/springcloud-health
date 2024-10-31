package com.springhealth.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = UserApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserApplicationTests {

    @Test
    void contextLoads() {
        // 测试应用程序上下文是否加载成功
    }
}
