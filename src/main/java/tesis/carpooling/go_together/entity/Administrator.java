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
 * @author Lina Sofia Cardona <lscardona@unicauca.edu.co>
 */
@Entity
@AllArgsConstructor
@Getter
@Setter
public class Administrator implements Serializable {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    private String userName;
    
    private String password;
    
    public Administrator(){}
}
