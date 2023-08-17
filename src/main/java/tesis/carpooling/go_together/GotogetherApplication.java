package tesis.carpooling.go_together;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class GotogetherApplication {

	public static void main(String[] args) {
		SpringApplication.run(GotogetherApplication.class, args);
	}
        
        @Component
        public static class Bootstrap {
            
            @Autowired
            private JdbcTemplate jdbcTemplate;
            
            @PostConstruct
            void init() {
                jdbcTemplate.execute("INSERT INTO administrator (id, user_name, password)  "
                        + "SELECT gen_random_uuid(), 'admin', 'admin123' "
                        + "WHERE NOT EXISTS (SELECT 1 FROM administrator)");
                
                jdbcTemplate.execute("INSERT INTO user_type (id, type) SELECT 1, 'admin'");
                
                jdbcTemplate.execute("INSERT INTO user_type (id, type) SELECT 2, 'passenger'");
                
                jdbcTemplate.execute("INSERT INTO user_type (id, type) SELECT 3, 'driver'");
                
            }
        }
 
}
