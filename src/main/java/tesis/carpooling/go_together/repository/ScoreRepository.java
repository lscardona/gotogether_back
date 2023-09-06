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
import tesis.carpooling.go_together.entity.Score;

@Repository
public interface ScoreRepository extends JpaRepository<Score, UUID> {
    
    @Query("SELECT COUNT(s) FROM Score s WHERE s.userId= :userId")
    long countScoresByUser(@Param("userId") UUID userId);
    
    @Query("SELECT s FROM Score s WHERE s.userId= :userId")
    List<Score> getScoreByUser(@Param("userId") UUID userId);
}
