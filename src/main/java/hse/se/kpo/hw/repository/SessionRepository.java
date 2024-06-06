package hse.se.kpo.hw.repository;

import hse.se.kpo.hw.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    Optional<Session> findByToken(String token);
}
