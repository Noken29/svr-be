package com.noken29.svrbe.repository;

import com.noken29.svrbe.domain.RoutingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutingSessionRepository extends JpaRepository<RoutingSession, Long> {
}
