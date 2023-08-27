/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package tesis.carpooling.go_together.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tesis.carpooling.go_together.entity.Travel;

/**
 *
 * @author Usuario
 */
@Repository
public interface TravelRepository extends JpaRepository<Travel, UUID> {
    
}
