package com.driver.services.impl;

import com.driver.Exception.NotFoundException;
import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override

    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        User user;
        ParkingLot parkingLot;
        try{
            user = userRepository3.findById(userId).get();
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        } catch (Exception e) {
            throw new Exception("Cannot make reservation");
        }


        Spot availableSpot = null;
        int minPrice = Integer.MAX_VALUE;

        for(Spot spot: parkingLot.getSpotList()){
            if(!spot.getOccupied() && spot.getNumberOfWheels() >= numberOfWheels){
                if(spot.getPricePerHour()*timeInHours < minPrice){
                    availableSpot = spot;
                    minPrice = spot.getPricePerHour()*timeInHours;
                }
            }
        }

        if(availableSpot==null){
            throw new Exception("Cannot make reservation");
        }


        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setSpot(availableSpot);
        reservation.setNumberOfHours(timeInHours);

        availableSpot.setOccupied(true);


        List<Reservation> userReservations = user.getReservationList();
        userReservations.add(reservation);
        user.setReservationList(userReservations);
        userRepository3.save(user); // Save the updated user entity


        List<Reservation> spotReservations = availableSpot.getReservationList();
        spotReservations.add(reservation);
        availableSpot.setReservationList(spotReservations);
        spotRepository3.save(availableSpot); // Save the updated spot entity


        return reservation;

    }

    private List<Spot> filterSpotsByType(List<Spot> availableSpots, Integer numberOfWheels) {
        List<Spot> filteredSpots = new ArrayList<>();
        for (Spot spot : availableSpots) {
            if (!spot.getOccupied() && spot.getNumberOfWheels() >= numberOfWheels) {
                filteredSpots.add(spot);
            }
        }
        return filteredSpots;
    }
}
