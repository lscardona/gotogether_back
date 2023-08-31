package tesis.carpooling.go_together.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "routes")
@Getter
@Setter
public class Routes implements Serializable{
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @ColumnDefault("true")
    private boolean enabled;
    
    private long startTime;
   
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE, 
        CascadeType.REFRESH, CascadeType.DETACH} )
    @JoinColumn(name = "driver_id")
    private Users driver;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE, 
        CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "route_passenger",
        joinColumns = @JoinColumn(name = "route_id"),
        inverseJoinColumns = @JoinColumn(name = "passenger_id"))
    private List<Users> passengers;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "route_id")
    private List<DriverPoint> points = new ArrayList<>();
    
    public Routes() {}
    
    public Routes(boolean enabled, Users driver, long startTime) {
        this.enabled=enabled;
        this.startTime=startTime;
        this.driver=driver;
    }
    
    public List<Users> addPassenger(Users passenger) {
        this.passengers.add(passenger);
        return passengers;
    }
}
