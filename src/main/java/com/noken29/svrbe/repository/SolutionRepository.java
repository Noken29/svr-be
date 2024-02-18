package com.noken29.svrbe.repository;

import com.noken29.svrbe.domain.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface SolutionRepository extends JpaRepository<Solution, Long> {
}
