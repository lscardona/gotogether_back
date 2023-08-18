/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package tesis.carpooling.go_together.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tesis.carpooling.go_together.entity.UserType;

/**
 *
 * @author Usuario
 */
@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    
}
