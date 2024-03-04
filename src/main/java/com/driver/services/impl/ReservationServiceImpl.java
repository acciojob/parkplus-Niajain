package com.driver.services.impl;

import com.driver.Exception.NotFoundException;
import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
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

        List<Spot> availableSpots = parkingLot.getSpotList();

        // Filter spots based on their types
        List<Spot> filteredSpots = filterSpotsByType(availableSpots, numberOfWheels);
        Spot minPriceSpot;
        if (filteredSpots.isEmpty()) {
            throw new NotFoundException("Cannot make reservation");
//            return null;
        }

        else
        {
            minPriceSpot = filteredSpots.get(0);
            double minPrice = filteredSpots.get(0).getPricePerHour()*timeInHours;
            for (Spot spot : filteredSpots) {
                double totalPrice = spot.getPricePerHour() * timeInHours;
                if (totalPrice < minPrice) {
                    minPrice = totalPrice;
                    minPriceSpot = spot;
                }
            }
            if (minPriceSpot == null) {
                throw new NotFoundException("Cannot make reservation");
//            return null;
            }
        }



        Payment payment = new Payment();
        payment.setPaymentCompleted(false); // Assuming the payment is not completed initially
        payment.setPaymentMode(PaymentMode.CASH);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setSpot(minPriceSpot);
        reservation.setNumberOfHours(timeInHours);

        minPriceSpot.setOccupied(true);

        reservation.setPayment(payment);

        // Save the reservation
        Reservation savedReservation = reservationRepository3.save(reservation);

        List<Reservation> userReservations = user.getReservationList();
        userReservations.add(savedReservation);
        user.setReservationList(userReservations);
        userRepository3.save(user); // Save the updated user entity


        List<Reservation> spotReservations = minPriceSpot.getReservationList();
        spotReservations.add(savedReservation);
        minPriceSpot.setReservationList(spotReservations);
        spotRepository3.save(minPriceSpot); // Save the updated spot entity


        return savedReservation;

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
