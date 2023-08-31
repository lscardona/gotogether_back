package tesis.carpooling.go_together.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import java.io.Serializable;
import java.util.UUID;
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
public class PassengerPoint implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequencePoint")
    @SequenceGenerator(name = "SequencePoint", sequenceName = "POINT_SEQ")
    private long id;
    
    private double lat;
    
    private double lng;
    
    private UUID elementId;
    
    public PassengerPoint(double latitude, double longitude) {
        this.lat=latitude;
        this.lng=longitude;
    }
}
