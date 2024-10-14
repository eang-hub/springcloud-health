package com.springhealth.intervention.repository;

import com.springhealth.intervention.domain.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterventionRepository extends JpaRepository<Intervention, Long> {

	List<Intervention> findInterventionsByUserId(@Param("userId") Long userId);
	
	List<Intervention> findInterventionsByDeviceId(@Param("deviceId") Long deviceId);

}
