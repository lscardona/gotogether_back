/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tesis.carpooling.go_together.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;

/**
 *
 * @author Lina Sofia Cardona <lscardona@unicauca.edu.co>
 */
@Entity
@Getter
@Setter
public class Users implements Serializable {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    private String name;
    
    private String identificationNumber;
    
    private String email;
    
    private String facialId;
    
    private String licensePlate;
    
    private String carModel;
    
    @ColumnDefault("true")
    private boolean enabled;
    
    @ColumnDefault("5")
    private float qualifying;
 
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REMOVE, 
        CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "type_id")
    private UserType type;
    
    public Users(){}
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(id, users.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
