/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tesis.carpooling.go_together;

import com.google.maps.GeoApiContext;

/**
 *
 * @author Usuario
 */
public class GoogleMapsApiConfig {
    private static final String API_KEY = "AIzaSyCBtOfuiyNgucAZs8gOa3l_BZSNOWTYK7c";
    private static final GeoApiContext CONTEXT = new GeoApiContext.Builder()
        .apiKey(API_KEY)
        .build();

    public static GeoApiContext getContext() {
        return CONTEXT;
    }
}
