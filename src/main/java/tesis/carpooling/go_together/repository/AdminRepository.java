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
import tesis.carpooling.go_together.entity.Administrator;

/**
 *
 * @author Usuario
 */
@Repository
public interface AdminRepository extends JpaRepository<Administrator, UUID> {
    
    @Query("SELECT a FROM Administrator a WHERE a.userName=:userName and a.password = :userpw")
    Administrator findAdmin(@Param("userName") String userName, @Param("userpw") String userpw );
    
}
