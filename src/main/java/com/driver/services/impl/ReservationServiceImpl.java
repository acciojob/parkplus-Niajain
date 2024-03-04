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
        try {
            user = userRepository3.findById(userId).get();
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        } catch (Exception e) {
            throw new Exception("Cannot make reservation");
        }

        Spot availableSpot = null;
        int minPrice = Integer.MAX_VALUE;

        for (Spot parkingSpot : parkingLot.getSpotList()) {
            if (!parkingSpot.getOccupied() &&
                    (numberOfWheels != 4 || parkingSpot.getSpotType() != SpotType.TWO_WHEELER) &&
                    (numberOfWheels <= 4 || parkingSpot.getSpotType() == SpotType.OTHERS)) {

                int spotPrice = parkingSpot.getPricePerHour() * timeInHours;
                if (spotPrice < minPrice) {
                    availableSpot = parkingSpot;
                    minPrice = spotPrice;
                }
            }
        }

        if (availableSpot == null) {
            throw new Exception("Cannot make reservation");
        }

        Reservation reservation = new Reservation();
        reservation.setSpot(availableSpot);
        reservation.setUser(user);
        reservation.setNumberOfHours(timeInHours);

        availableSpot.setOccupied(true);

        List<Reservation> userReservations = user.getReservationList();
        userReservations.add(reservation);
        user.setReservationList(userReservations);
        userRepository3.save(user);

        List<Reservation> spotReservations = availableSpot.getReservationList();
        spotReservations.add(reservation);
        availableSpot.setReservationList(spotReservations);
        spotRepository3.save(availableSpot);

        return reservation;
    }
}
