package tesis.carpooling.go_together.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tesis.carpooling.go_together.entity.Administrator;
import tesis.carpooling.go_together.entity.DriverPoint;
import tesis.carpooling.go_together.entity.PassengerPoint;
import tesis.carpooling.go_together.entity.Users;
import tesis.carpooling.go_together.entity.Routes;
import tesis.carpooling.go_together.entity.Score;
import tesis.carpooling.go_together.entity.Session;
import tesis.carpooling.go_together.entity.Travel;
import tesis.carpooling.go_together.entity.UserType;
import tesis.carpooling.go_together.repository.AdminRepository;
import tesis.carpooling.go_together.repository.DriverPointRepository;
import tesis.carpooling.go_together.repository.RoutesRepository;
import tesis.carpooling.go_together.repository.ScoreRepository;
import tesis.carpooling.go_together.repository.SessionRepository;
import tesis.carpooling.go_together.repository.TravelRepository;
import tesis.carpooling.go_together.repository.UserRepository;
import tesis.carpooling.go_together.repository.UserTypeRepository;
import tesis.carpooling.go_together.repository.PassengerPointRepository;

/**
 *
 * @author Users
 */
@RestController
public class InternalApiRestController {
   
   @Autowired 
    private RoutesRepository routeRepo;
   
   @Autowired
   private UserRepository userRepo;
   
   @Autowired 
    private SessionRepository sessionRepo;
   
   @Autowired
   private AdminRepository adminRepo;
   
   @Autowired
   private UserTypeRepository typeRepo;
   
   @Autowired
   private TravelRepository travelRepo;
   
   @Autowired
   private ScoreRepository scoreRepo;
   
   @Autowired
   private PassengerPointRepository passengerPointRepo;
   
   @Autowired
   private DriverPointRepository driverPointRepo;
   
   // <editor-fold desc="Session" defaultstate="collapsed">
   
    /**
     * Create a session for passengers and drivers users
     * @param userId facial id
     * @return the session opened
     */
    @PostMapping("/create-session/{userId}")
    public Session createSession(@PathVariable("userId") String userId) {
        long findUser = userRepo.countUserByUuid(userId);
        if( findUser == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User not found"));
        Users user = userRepo.findUserByUuid(userId);
        long findSession = sessionRepo.getSessionByUser(user.getId());
        if(findSession != 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A session already exists for this user");
        if(!user.isEnabled()) 
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "User disabled");
        try {
            Session session = new Session(user.getId(),  user.getType().getType());
            sessionRepo.save(session);
            return session;
        } catch(Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        } 
    }
   
    /**
     * Create a session with admin user
     * @param userName name admin user
     * @param password password admin user
     * @return the session opened
     */
    @PostMapping("/create-session/admin/{userName}/{password}")
    public Session createAdminSession(@PathVariable("userName") String userName,
             @PathVariable("password") String password) {
        Administrator admin = adminRepo.findAdmin(userName, password);
        long findSession = sessionRepo.getSessionByUser(admin.getId());
        if(findSession != 0)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Session already exist");
        try {
            Session session = new Session(admin.getId(),  admin.getUserName());
            sessionRepo.save(session);
            return session;
        } catch(Exception ex ) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid user");
        }
    }
   
    /**
     * Close a session of users
     * @param sessionId id of session
     */
    @DeleteMapping("/delete-session/{sessionId}")
    public void closeSession(@PathVariable("sessionId") UUID sessionId) {
        try {
            sessionRepo.deleteById(sessionId);
        } catch(Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
   
    /**
     * Create an user in database
     * @param user user created
     * @return user
     */
    @PostMapping("/api/signup")
    public Users createUser(@RequestBody Users user) {
        try {
            UserType type = typeRepo.findById(user.getType().getId()).get();
            user.setType(type);
            user.setEnabled(true);
            user.setQualifying(5);
            userRepo.save(user);
            return user;
        } catch(Exception ex) {
              throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                      String.format("Access denied", ex.getMessage()));
         }
    }
    
    // </editor-fold>
   
   // <editor-fold desc="Admin" defaultstate="collapsed">

    /**
     * gets registered drivers
     * @param sessionId admin session id
     * @return a list with all drivers
     */
    @GetMapping("/drivers/{sessionId}")
    public List<Users> getDrivers(@PathVariable("sessionId") UUID sessionId) {
        try {
            if(validateAdminCall(sessionId)) {
                List<Users> drivers = userRepo.getDrivers();
                return drivers;
            } else
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access denied");
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
   
    /**
     * gets registered passengers
     * @param sessionId admin session id
     * @return a list with the passengers
     */
    @GetMapping("/passengers/{sessionId}")
    public List<Users> getPassengers(@PathVariable("sessionId") UUID sessionId) {
        try {
            if(validateAdminCall(sessionId)) {
                List<Users> passengers = userRepo.getPassenger();
                return  passengers; 
            } else
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access denied");
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    /**
     * Enable or disable users
     * @param enabled flag that enabled or disabled users
     * @param userId user to change the state
     * @param sessionId admin session id
     */
    @PutMapping("/change-state/{sessionId}/{userId}/{enabled}")
    public void changeUserState(@PathVariable("enabled") boolean enabled, 
            @PathVariable("userId") UUID userId, @PathVariable("sessionId") UUID sessionId) {
        if(!validateAdminCall(sessionId)) 
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access denied");
        else {
            try {
                Users driver = userRepo.findUserById(userId);
                driver.setEnabled(enabled);
                userRepo.save(driver);
            } catch(Exception ex) {
                 throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                         String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
            }
        }
    }
   // </editor-fold>
   
   // <editor-fold desc="Passenger" defaultstate="collapsed">
   
    /**
     * Get information about driver
     * @param sessionId id of session 
     * @return the driver user
     */
    @GetMapping("/passenger/{sessionId}")
    public Users getPassengerUser(@PathVariable("sessionId") UUID sessionId) {
        if(!validatePassengerCall(sessionId)) 
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Session session = sessionRepo.findById(sessionId).get();
        if(session == null) 
             throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Users user = userRepo.findById(session.getUserId()).get();
        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        try {
            Users userInfo = new Users();
            userInfo.setName(user.getName());
            userInfo.setIdentificationNumber(user.getIdentificationNumber());
            userInfo.setEmail(user.getEmail());
            return userInfo;
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    /**
     * Create a travel
     * @param sessionId session user id
     * @param point List of points along the route 
     * @param startTime travel start time
     * @return the route
     */
    @PostMapping("/travel/{sessionId}/{start-travel}")
    public Travel createTravel(@PathVariable("sessionId") UUID sessionId, @RequestBody List<PassengerPoint> point, 
            @PathVariable("start-travel") long startTime) {
        
        if(!validatePassengerCall(sessionId))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        if(point.size() != 2)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can choose only two points");
        try{
            Session session = sessionRepo.getSession(sessionId);
            Users passenger = userRepo.findUserById(session.getUserId());
            Travel newRoute = new Travel(passenger, startTime);
            newRoute.setState(1);
            UUID travelSaved = travelRepo.save(newRoute).getId();
            point.forEach(p -> {
                PassengerPoint newPoint = new PassengerPoint(p.getLat(), p.getLng());
                newPoint.setElementId(travelSaved);
                passengerPointRepo.save(newPoint);
            });
            return newRoute;
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    @GetMapping("/passenger/routes/{sessionId}/{routeId}")
    public List<Routes> getRoutesByTravel(@PathVariable("sessionId") UUID sessionId, 
            @PathVariable("routeId") UUID routeId) {
        if(!validatePassengerCall(sessionId))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Travel myTravel = travelRepo.findById(routeId).get();
        List<PassengerPoint> myPoints = passengerPointRepo.getPointsByRoute(myTravel.getId());
        List<Routes> allRoutesByTime = routeRepo.findRoutesByTime(myTravel.getStartTime());
        allRoutesByTime.forEach(r -> {
            List<DriverPoint> points = driverPointRepo.getPointsByRoute(r.getId());
            r.setPoints(points);
        });
        List<Routes> finalRoutes = new ArrayList<>();
        
        try {
            finalRoutes = getNearbyRoutes(myPoints.get(0), myPoints.get(myPoints.size()-1), allRoutesByTime);
           return finalRoutes; 
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    @PutMapping("/passenger/select-route/{sessionId}/{routeId}")
    public Routes selectRoute(@PathVariable("sessionId") UUID sessionId, 
            @PathVariable("routeId") UUID routeId) {
        if(!validatePassengerCall(sessionId))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Session session = sessionRepo.findById(sessionId).get();
        if(session == null) 
             throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Users user = userRepo.findById(session.getUserId()).get();
        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Routes route = routeRepo.getRoute(routeId);
        if(route.getPassengers().size() < 4) {
            route.getPassengers().add(user);
            routeRepo.save(route);
            return route;
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The route is full");
        }
        
    }
    
    @DeleteMapping("travel/{sessionId}/{routeId}")
    public void deleteTravel(@PathVariable("sessionId") UUID sessionId, @PathVariable("routeId") UUID routeId) {
        if(!validatePassengerCall(sessionId)) 
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        try {
            Session session = sessionRepo.getSession(sessionId);
            Users user = userRepo.findUserById(session.getUserId());
            passengerPointRepo.deletePointsByRoute(routeId);
            Routes passengerRoute = routeRepo.findByPassengersContaining(user);
            if(passengerRoute != null) 
                passengerRoute.getPassengers().remove(user);
            
            travelRepo.deleteTravel(routeId);
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    /**
     * Adds driver rating by passengers
     * @param sessionId session by user
     * @param number driver score
     * @param userId Driver User ID
     * @return
     */
    @PutMapping("/score/{sessionId}/{userId}/{score}")
    public ResponseEntity<String> passengerQualifying(@PathVariable("sessionId") UUID sessionId, 
            @PathVariable("score") long number, @PathVariable("userId") UUID userId) {
        if(!validatePassengerCall(sessionId))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Session session = sessionRepo.findById(sessionId).get();
        if(session == null) 
             throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Users user = userRepo.findById(userId).get();
        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        try {
            Score newScore = new Score(user.getId(), number);
            scoreRepo.save(newScore);
            long numberScores = scoreRepo.countScoresByUser(user.getId());
            List<Score> scores = scoreRepo.getScoreByUser(user.getId());
            float count = 0;
            for(Score s : scores) {
                count += s.getScore();
            }
            float finalQualifying = count/numberScores;
            user.setQualifying(finalQualifying);
            userRepo.save(user);
            return ResponseEntity.ok("Calificación enviada");
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    /**
     * Get a travel for a passenger
     * @param sessionId
     * @return
     */
    @GetMapping("travel/{sessionId}")
    public Travel getTravel(@PathVariable("sessionId") UUID sessionId) {
        if(!validatePassengerCall(sessionId)) 
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Session session = sessionRepo.findById(sessionId).get();
        if(session == null) 
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Users user = userRepo.findById(session.getUserId()).get();
        Travel myTravel = travelRepo.getTravelByUser(user);
        if(myTravel == null)
            return new Travel();
        List<PassengerPoint> mypoint = passengerPointRepo.getPointsByRoute(myTravel.getId());
        myTravel.setPoints(mypoint);
        return myTravel;
    }
    
    /**
     * Gets the meaning of the passenger's trip status
     * @param sessionId session by user
     * @param travelId
     * @return
     */
    @GetMapping("/travel/state/{sessionId}/{travelId}")
    public String getTravelState(@PathVariable("sessionId") UUID sessionId, 
            @PathVariable("travelId") UUID travelId) {
        if(!validatePassengerCall(sessionId)) 
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        
        Optional<Travel> optionalMyTravel = travelRepo.findById(travelId);
        
        if(!optionalMyTravel.isPresent()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tienes una ruta creada");
        int state = optionalMyTravel.get().getState();
        
        switch (state) {
            case 1 -> {
                return "Esperando confirmación del conductor";
           }
            case 2 -> {
                return "Viaje aceptado";
           }
            case 3 -> {
                return "El conductor ha cancelado el viaje";
           }
            case 4 -> {
                return "Tu solicitud ha sido rechazada";
           }
            default -> throw new AssertionError();
        }
    }
    
    @GetMapping("/score/count/{userId}")
    public long countScoresByUser(@PathVariable("userId") UUID userId) {
        return scoreRepo.countScoresByUser(userId);
    }
// </editor-fold>
   
   // <editor-fold desc="Users Driver" defaultstate="collapsed">

    /**
     * Create a route
     * @param sessionId session user id
     * @param point List of points along the route 
     * @param startTime travel start time
     * @return the route
     */
    @PostMapping("/route/{sessionId}/{start-travel}")
    public Routes createRoute(@PathVariable("sessionId") UUID sessionId, @RequestBody List<PassengerPoint> point, 
            @PathVariable("start-travel") long startTime) {
        
        if(!validateDriverCall(sessionId))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        
        try{
            Session session = sessionRepo.getSession(sessionId);
            Users driver = userRepo.findUserById(session.getUserId());
            Routes newRoute = new Routes(true, driver, startTime);
            UUID routeId = routeRepo.save(newRoute).getId();
            point.forEach(p -> {
                DriverPoint newPoint = new DriverPoint(p.getLat(), p.getLng());
                newPoint.setElementId(routeId);
                driverPointRepo.save(newPoint);
            });
            return newRoute;
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    @GetMapping("/get-route/{sessionId}")
    public Routes getRoute(@PathVariable("sessionId") UUID sessionId) {
        if(!validateDriverCall(sessionId)) 
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        try {
            Session session = sessionRepo.findById(sessionId).get();
            Users user = userRepo.findById(session.getUserId()).get();
            Routes route = routeRepo.findRouteByUser(user.getId());
            if(route == null) {
                Routes newRoute = new Routes();
                newRoute.setDriver(user);
                return newRoute;
            }
            List<DriverPoint> myPoints = driverPointRepo.getPointsByRoute(route.getId());
            route.setPoints(myPoints);
            return route;
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    /**
     * Delete a route
     * @param sessionId user id
     * @param routeId route id
     */
    @DeleteMapping("route/{sessionId}/{routeId}")
    public void deleteRoute(@PathVariable("sessionId") UUID sessionId, @PathVariable("routeId") UUID routeId) {
        if(!validateDriverCall(sessionId)) 
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        try {
            Routes myRoute = routeRepo.findById(routeId).get();
            if(!myRoute.getPassengers().isEmpty()) {
                myRoute.getPassengers().forEach(user -> {
                    Travel travel = travelRepo.getTravelByUser(user);
                    if(travel != null) {
                        travel.setState(3);
                        travelRepo.save(travel);
                    }
                });
            }
           driverPointRepo.deletePointsByRoute(routeId);
           routeRepo.deleteRoute(routeId);
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    /**
     * Get information about driver
     * @param sessionId id of session 
     * @return the driver user
     */
    @GetMapping("/driver/{sessionId}")
    public Users getDriverUser(@PathVariable("sessionId") UUID sessionId) {
        if(!validateDriverCall(sessionId)) 
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Session session = sessionRepo.findById(sessionId).get();
        if(session == null) 
             throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Users user = userRepo.findById(session.getUserId()).get();
        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        try {
            Users userInfo = new Users();
            userInfo.setName(user.getName());
            userInfo.setIdentificationNumber(user.getIdentificationNumber());
            userInfo.setEmail(user.getEmail());
            userInfo.setLicensePlate(user.getLicensePlate());
            userInfo.setCarModel(user.getCarModel());
            userInfo.setQualifying(user.getQualifying());
            return userInfo;
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    @PutMapping("/driver-score/{sessionId}/{userId}/{score}")
    public ResponseEntity<String> driverQualifying(@PathVariable("sessionId") UUID sessionId, 
            @PathVariable("score") long number, @PathVariable("userId") UUID userId) {
        if(!validateDriverCall(sessionId))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Session session = sessionRepo.findById(sessionId).get();
        if(session == null) 
             throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Users user = userRepo.findById(userId).get();
        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        try {
            Score newScore = new Score(user.getId(), number);
            scoreRepo.save(newScore);
            long numberScores = scoreRepo.countScoresByUser(user.getId());
            List<Score> scores = scoreRepo.getScoreByUser(user.getId());
            float count = 0;
            for(Score s : scores) {
                count += s.getScore();
            }
            float finalQualifying = count/numberScores;
            user.setQualifying(finalQualifying);
            userRepo.save(user);
            return ResponseEntity.ok("Calificación enviada");
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    @GetMapping("/route/passengers/{sessionId}/{routeId}")
    public List<Users> getPassengersInRoute(@PathVariable("sessionId") UUID sessionId, 
            @PathVariable("routeId") UUID routeId) {
         if(!validateDriverCall(sessionId))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Session session = sessionRepo.findById(sessionId).get();
        if(session == null) 
             throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        
        Routes myRoute = routeRepo.findById(routeId).get();
        
        if(myRoute == null) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tu no tienes rutas creadas");
        try {
            List<Users> passengers = myRoute.getPassengers();
            if(passengers.isEmpty()) 
                return new ArrayList<>();
            
            return passengers;
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    @PutMapping("/delete-passenger/{sessionId}/{routeId}")
    public Routes deletePassengerFromRoute(@PathVariable("sessionId") UUID sessionId, 
            @PathVariable("routeId") UUID routeId, @RequestBody Users passenger) {
        if(!validateDriverCall(sessionId))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Session session = sessionRepo.findById(sessionId).get();
        if(session == null) 
             throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Users user = userRepo.findById(session.getUserId()).get();
        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Routes route = routeRepo.getRoute(routeId);
        Travel passengerTravel = travelRepo.getTravelByUser(passenger);
        if(passengerTravel != null) {
            passengerTravel.setState(4);
            travelRepo.save(passengerTravel);
        }
        route.getPassengers().remove(passenger);
        routeRepo.save(route);
        return route;
    }
    
    @PutMapping("/add-passenger-route/{sessionId}/{routeId}")
    public Routes addPassengerToRoute(@PathVariable("sessionId") UUID sessionId, 
            @PathVariable("routeId") UUID routeId, @RequestBody Users passenger) {
        if(!validateDriverCall(sessionId))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Session session = sessionRepo.findById(sessionId).get();
        if(session == null) 
             throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Users user = userRepo.findById(session.getUserId()).get();
        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        Routes route = routeRepo.getRoute(routeId);
        Travel passengerTravel = travelRepo.getTravelByUser(passenger);
        if(passengerTravel != null) {
            passengerTravel.setState(2);
            travelRepo.save(passengerTravel);
        }
        routeRepo.save(route);
        return route;
    }
   // </editor-fold>
   
   // <editor-fold desc="validate sessions" defaultstate="collapsed">
    public boolean validateAdminCall(UUID sessionId) {
        try {
            Session session = sessionRepo.getSession(sessionId);
            return "admin".equals(session.getUserType());
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Access denied", ex.getMessage()));
        }
    }

    public boolean validatePassengerCall(UUID sessionId) {
        try {
            Session session = sessionRepo.getSession(sessionId);
            if("passenger".equals(session.getUserType())) {
                Users passenger = userRepo.findUserById(session.getUserId());
                return passenger.isEnabled();
            }
            return false;
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Access denied", ex.getMessage()));
        }
    }

    public boolean validateDriverCall(UUID sessionId) {
        try {
            Session session = sessionRepo.getSession(sessionId);
            if("driver".equals(session.getUserType())) {
                Users driver = userRepo.findUserById(session.getUserId());
                return driver.isEnabled();
            }
            return false;
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Access denied", ex.getMessage()));
        }
    }
   // </editor-fold>
    
   // <editor-fold desc="Helpers" defaultstate="collapsed">
    
    public static List<Routes> getNearbyRoutes(PassengerPoint p1, PassengerPoint p2, List<Routes> allRoutes) {
        double maxDistance = 1.0;
        List<Routes> nearbyRoutes = new ArrayList<>();
        
        for (Routes route : allRoutes) {
            boolean matchStartPoint = false;
            boolean matchEndPoint = false;
            for(DriverPoint point : route.getPoints()) {
                double startDistance = haversine(p1.getLat(), p1.getLng(), point.getLat(), point.getLng());
                double endDistance = haversine(p2.getLat(), p2.getLng(), point.getLat(), point.getLng());
                
                if (startDistance <= maxDistance) {
                    matchStartPoint = true;
                }
                
                if (endDistance <= maxDistance) {
                    matchEndPoint = true;
                }
            }
            
            if(matchStartPoint && matchEndPoint)
                nearbyRoutes.add(route);
        }

        return nearbyRoutes;
    }
    
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radio de la Tierra en kilómetros

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
               Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c;

        return distance;
    }
   
    // </editor-fold>
  
}
