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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;

/**
 *
 * @author Usuario
 */
@Getter
@Setter
@Entity
public class Travel {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @ColumnDefault("true")
    private boolean enabled;
    
    private long startTime;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE, 
        CascadeType.REFRESH, CascadeType.DETACH} )
    @JoinColumn(name = "points_id")
    private List<Point> points;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE, 
        CascadeType.REFRESH, CascadeType.DETACH} )
    @JoinColumn(name = "passenger_id")
    private Users passenger;
    
    public Travel(boolean enabled, Users passenger, long startTime, List<Point> points) {
        this.enabled=enabled;
        this.startTime=startTime;
        this.points=points;
        this.passenger=passenger;
    }
}
