package org.serdaroquai.me.components;

import org.serdaroquai.me.entity.Estimation;
import org.springframework.data.repository.Repository;

public interface EstimationRepository extends Repository<Estimation, String> {

	Estimation save(Estimation estimation);
}