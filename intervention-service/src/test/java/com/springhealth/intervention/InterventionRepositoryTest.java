package com.springhealth.intervention;

import com.springhealth.intervention.domain.Intervention;
import com.springhealth.intervention.repository.InterventionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@RunWith(SpringRunner.class)
@DataJpaTest
public class InterventionRepositoryTest {

	@Autowired
	private TestEntityManager entityManager; // 用于持久化测试数据

	@Autowired
	private InterventionRepository interventionRepository; // 用于访问干预记录

	@Test
	public void testFindInterventionByUserId() throws Exception {
		// 持久化干预记录
		this.entityManager.persist(new Intervention(1L, 1L, 100F, "Intervention1", new Date()));
		this.entityManager.persist(new Intervention(1L, 2L, 200F, "Intervention2", new Date()));

		Long userId = 1L;
		// 查找干预记录
		List<Intervention> interventions = this.interventionRepository.findInterventionsByUserId(userId);
		assertThat(interventions).size().isEqualTo(2); // 断言数量为2
		Intervention actual = interventions.get(0);
		assertThat(actual.getUserId()).isEqualTo(userId); // 断言用户 ID 匹配
	}

	@Test
	public void testFindInterventionByDeviceId() throws Exception {
		this.entityManager.persist(new Intervention(1L, 1L, 100F, "Intervention1", new Date()));
		this.entityManager.persist(new Intervention(1L, 1L, 200F, "Intervention2", new Date()));

		Long deviceId = 1L;
		List<Intervention> interventions = this.interventionRepository.findInterventionsByDeviceId(deviceId);
		assertThat(interventions).size().isEqualTo(2); // 断言数量为2
		Intervention actual = interventions.get(0);
		assertThat(actual.getUserId()).isEqualTo(deviceId); // 断言设备 ID 匹配
	}

	@Test
	public void testFindInterventionByNonExistedUserId() throws Exception {
		this.entityManager.persist(new Intervention(1L, 1L, 100F, "Intervention1", new Date()));
		this.entityManager.persist(new Intervention(1L, 2L, 200F, "Intervention2", new Date()));

		Long userId = 3L;
		List<Intervention> interventions = this.interventionRepository.findInterventionsByUserId(userId);
		assertThat(interventions).size().isEqualTo(0); // 断言数量为0
	}
}

