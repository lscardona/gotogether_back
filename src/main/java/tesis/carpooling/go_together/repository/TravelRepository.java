/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package tesis.carpooling.go_together.repository;

import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tesis.carpooling.go_together.entity.Travel;
import tesis.carpooling.go_together.entity.Users;

/**
 *
 * @author Usuario
 */
@Repository
public interface TravelRepository extends JpaRepository<Travel, UUID> {
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Travel r WHERE r.id = :travelId")
    void deleteTravel(@Param("travelId") UUID travelId);
    
    @Query("SELECT t FROM Travel t WHERE t.passenger = :pass")
    Travel getTravelByUser(@Param("pass") Users pass);
}
