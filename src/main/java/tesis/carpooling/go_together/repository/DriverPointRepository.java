/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
import tesis.carpooling.go_together.entity.DriverPoint;

/**
 *
 * @author Usuario
 */
@Repository
public interface DriverPointRepository extends JpaRepository<DriverPoint, Long> {
    
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM driver_point p WHERE p.element_id = :routeId")
    void deletePointsByRoute(@Param("routeId") UUID routeId);
    
    @Query("SELECT p FROM DriverPoint p WHERE p.elementId = :routeId")
    List<DriverPoint> getPointsByRoute(@Param("routeId") UUID routeId);
}
