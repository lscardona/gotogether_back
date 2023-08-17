/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package tesis.carpooling.go_together.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tesis.carpooling.go_together.entity.Users;

/**
 *
 * @author Users
 */
@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    
    @Query("SELECT p FROM Users p WHERE p.uuid=:userId")
    Users findUserByUuid(@Param("userId") String userId);
    
    @Query("SELECT COUNT(d) FROM Users d WHERE d.uuid = :userId")
    public int countUserByUuid(@Param("userId") String userId);
    
    @Query("SELECT p FROM Users p WHERE p.id=:userId")
    Users findUserById(@Param("userId") UUID userId);
    
    @Query("SELECT d FROM Users d WHERE d.type.id=3")
    List<Users> getPassenger();
    
    @Query("SELECT d FROM Users d WHERE d.type.id=2")
    List<Users> getDrivers();
}
