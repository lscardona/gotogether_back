package tesis.carpooling.go_together.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Usuario
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Score implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequencePoint")
    @SequenceGenerator(name = "SequencePoint", sequenceName = "POINT_SEQ")
    private long id;
    
    private UUID userId;
    
    private long score;
    
    public Score(UUID userId, long score) {
        this.userId = userId;
        this.score = score;
    }
}
