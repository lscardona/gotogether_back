/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tesis.carpooling.go_together;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author Usuario
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Permite a todos los endpoints
            .allowedOrigins("*")     // Orígenes permitidos (puedes ajustar según necesites)
            .allowedMethods("GET", "POST", "PUT", "DELETE")  // Métodos HTTP permitidos
            .allowedHeaders("*");    // Cabeceras permitidas
    }
}
