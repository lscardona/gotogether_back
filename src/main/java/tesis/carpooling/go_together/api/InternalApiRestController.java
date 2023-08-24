/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tesis.carpooling.go_together.api;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tesis.carpooling.go_together.entity.Administrator;
import tesis.carpooling.go_together.entity.Point;
import tesis.carpooling.go_together.entity.Users;
import tesis.carpooling.go_together.entity.Routes;
import tesis.carpooling.go_together.entity.Session;
import tesis.carpooling.go_together.entity.UserType;
import tesis.carpooling.go_together.repository.AdminRepository;
import tesis.carpooling.go_together.repository.RoutesRepository;
import tesis.carpooling.go_together.repository.SessionRepository;
import tesis.carpooling.go_together.repository.UserRepository;
import tesis.carpooling.go_together.repository.UserTypeRepository;

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
            userRepo.save(user);
            return user;
        } catch(Exception ex) {
              throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                      String.format("Access denied", ex.getMessage()));
         }
    }
   
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
   
   // </editor-fold>
   
   // <editor-fold desc="Users Driver" defaultstate="collapsed">

    /**
     * Create a route
     * @param sessionId session user id
     * @param point List of points along the route 
     * @param startTime travel start time
     * @return the route
     */
    @PostMapping("/route/{sessionId}")
    public Routes createRoute(@PathVariable("sessionId") UUID sessionId, @RequestBody List<Point> point, 
            @PathVariable("start-travel") long startTime) {
        
        if(!validateDriverCall(sessionId))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        
        try{
            Session session = sessionRepo.getSession(sessionId);
            Users driver = userRepo.findUserById(session.getUserId());
            Routes newRoute = new Routes(true, driver, Calendar.getInstance().getTimeInMillis(), point);
            routeRepo.save(newRoute);
            return newRoute;
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
    }
    
    @GetMapping("/get-route/{sessionId}/{routeId}")
    public Routes getRoute(@PathVariable("sessionId") UUID sessionId, 
            @PathVariable("routeId") UUID routeId) {
        if(!validateDriverCall(sessionId)) 
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Access denied");
        try {
            return routeRepo.getRoute(routeId);
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
           routeRepo.deleteById(routeId);
        } catch(Exception ex) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                     String.format("Something went wrong, contact your administrator %s", ex.getMessage()));
        }
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
   
  
}
