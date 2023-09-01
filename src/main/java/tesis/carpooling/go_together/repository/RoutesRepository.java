/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package tesis.carpooling.go_together.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tesis.carpooling.go_together.entity.Routes;
import tesis.carpooling.go_together.entity.Users;

/**
 *
 * @author Usuario
 */
@Repository
public interface RoutesRepository extends JpaRepository<Routes, UUID> {
    
    @Query("SELECT r FROM Routes r WHERE r.id= :routeId")
    Routes getRoute(@Param("routeId") UUID routeId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Routes r WHERE r.id = :routeId")
    void deleteRoute(@Param("routeId") UUID routeId);
    
    @Query("SELECT r FROM Routes r WHERE ABS(r.startTime - :myTime) <= 600")
    List<Routes> findRoutesByTime(@Param("myTime") long myTime);
    
    @Query("SELECT r FROM Routes r WHERE :passenger MEMBER OF r.passengers")
    Routes findByPassengersContaining(@Param("passenger") Users passenger);
}
