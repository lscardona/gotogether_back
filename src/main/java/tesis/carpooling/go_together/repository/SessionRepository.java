/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package tesis.carpooling.go_together.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tesis.carpooling.go_together.entity.Session;

/**
 *
 * @author Usuario
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    
    @Query("SELECT s FROM Session s WHERE s.id = :sessionId")
    public Session getSession(@Param("sessionId") UUID sessionId);
    
    @Query("SELECT COUNT(s) FROM Session s WHERE s.userId = :userId")
    public long getSessionByUser(@Param("userId") UUID userId);
}
