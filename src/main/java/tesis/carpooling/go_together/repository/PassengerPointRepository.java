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
import tesis.carpooling.go_together.entity.PassengerPoint;

/**
 *
 * @author Usuario
 */
@Repository
public interface PassengerPointRepository extends JpaRepository<PassengerPoint, Long> {
    
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM passenger_point p WHERE p.element_id = :travelId")
    void deletePointsByRoute(@Param("travelId") UUID travelId);
    
    @Query("SELECT p FROM PassengerPoint p WHERE p.elementId = :travelId")
    List<PassengerPoint> getPointsByRoute(@Param("travelId") UUID travelId);
}
