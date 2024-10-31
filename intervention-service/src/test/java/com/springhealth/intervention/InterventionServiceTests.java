package com.springhealth.intervention;

import com.springhealth.intervention.client.DeviceMapper;
import com.springhealth.intervention.client.DeviceServiceClient;
import com.springhealth.intervention.client.UserMapper;
import com.springhealth.intervention.client.UserServiceClient;
import com.springhealth.intervention.domain.Intervention;
import com.springhealth.intervention.repository.InterventionRepository;
import com.springhealth.intervention.service.InterventionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
@RunWith(SpringRunner.class) // 使用 SpringRunner 运行测试
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // 加载 Spring Boot 上下文
public class InterventionServiceTests {

	@MockBean // 模拟 UserServiceClient
	private UserServiceClient userClient;

	@MockBean // 模拟 DeviceServiceClient
	private DeviceServiceClient deviceClient;

	@MockBean // 模拟 InterventionRepository
	private InterventionRepository interventionRepository;

	@Autowired // 自动装配 InterventionService
	private InterventionService interventionService;

	@Test // 测试生成干预记录
	public void testGenerateIntervention() throws Exception {
		String userName = "springhealth_user1"; // 用户名
		String deviceCode = "device1"; // 设备代码

		// 当调用 userClient.getUserByUserName 时，返回一个模拟的用户
		given(this.userClient.getUserByUserName(userName)).willReturn(new UserMapper(1L, "user1", userName));

		// 当调用 deviceClient.getDevice 时，返回一个模拟的设备
		given(this.deviceClient.getDevice(deviceCode))
				.willReturn(new DeviceMapper(1L, "便携式血压计", "device1", "Sphygmomanometer", 100F));

		// 生成干预记录
		Intervention actual = interventionService.generateIntervention(userName, deviceCode);

		// 断言健康数据
		assertThat(actual.getHealthData()).isEqualTo(100L);
	}
}
