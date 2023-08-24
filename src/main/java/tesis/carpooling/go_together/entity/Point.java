/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tesis.carpooling.go_together.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Usuario
 */
@Entity
@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Point implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequencePoint")
    @SequenceGenerator(name = "SequencePoint", sequenceName = "POINT_SEQ")
    private long id;
    
    private double lat;
    
    private double lng;
    
    public Point(double latitude, double longitude) {
        this.lat=latitude;
        this.lng=longitude;
    }
}
