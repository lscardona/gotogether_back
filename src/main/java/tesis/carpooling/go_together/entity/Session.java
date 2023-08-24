/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tesis.carpooling.go_together.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

/**
 *
 * @author Usuario
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
public class Session implements Serializable {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    private UUID userId;
    
    private String userType;
    
    private long adminId;
    
    public Session() {}
    
    public Session(UUID userId, String userType) {
        this.userId=userId;
        this.userType=userType;
    }
//    public Session(UUID id, long adminId, String userType) {
//        this.id=id;
//        this.adminId=adminId;
//        this.userType=userType;
//    }
}
