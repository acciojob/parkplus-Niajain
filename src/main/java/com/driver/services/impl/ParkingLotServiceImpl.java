package com.driver.services.impl;

import com.driver.Exception.NotFoundException;
import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
        ParkingLot parkingLot=new ParkingLot();

        parkingLot.setName(name);
        parkingLot.setAddress(address);

        return parkingLotRepository1.save(parkingLot);
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        Optional<ParkingLot> optionalParkingLot=parkingLotRepository1.findById(parkingLotId);
        if(!optionalParkingLot.isPresent())
        {
            throw new NotFoundException("Parking Lot ID Not found");
        }
        ParkingLot parkingLot=optionalParkingLot.get();

        SpotType spotType=CalculateSpotType(numberOfWheels);

        Spot spot=new Spot();
        spot.setParkingLot(parkingLot);
        spot.setPricePerHour(pricePerHour);
        spot.setNumberOfWheels(numberOfWheels);
        spot.setSpotType(spotType);
        spot.setOccupied(false);
        List<Spot> spots = parkingLot.getSpotList();
        if (spots == null) {
            spots = new ArrayList<>();
        }
        spots.add(spot);
        parkingLot.setSpotList(spots);

//        parkingLotRepository1.save(parkingLot);
        return spotRepository1.save(spot);


    }

    private SpotType CalculateSpotType(Integer numberOfWheels) {
        if(numberOfWheels==2)
            return SpotType.TWO_WHEELER;
        else if(numberOfWheels==4)
            return SpotType.FOUR_WHEELER;
        else
            return SpotType.OTHERS;
    }

    @Override
    public void deleteSpot(int spotId) {
        Optional<Spot> optionalSpot=spotRepository1.findById(spotId);
        if(!optionalSpot.isPresent())
        {
            throw new NotFoundException("Spot Id doesn't exist");
        }
        Spot spot=optionalSpot.get();

        ParkingLot parkingLot = spot.getParkingLot();

        // Remove the spot from the list of spots associated with the parking lot
        List<Spot> spots = parkingLot.getSpotList();
        spots.remove(spot);
        parkingLot.setSpotList(spots);

        spotRepository1.delete(spot);

    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        Optional<ParkingLot> optionalParkingLot=parkingLotRepository1.findById(parkingLotId);
        if(!optionalParkingLot.isPresent())
        {
            throw new NotFoundException("Parking lot doesn't exist");
        }
        ParkingLot parkingLot=optionalParkingLot.get();

        Optional<Spot> optionalSpot=spotRepository1.findById(spotId);
        if(!optionalSpot.isPresent())
        {
            throw new NotFoundException("Spot doesn't exist");
        }
        Spot spot=optionalSpot.get();

        if(spot.getParkingLot().getId()!=parkingLotId)
        {
            throw new NotFoundException("Spot does not belong to the specified parking lot");
        }

        spot.setPricePerHour(pricePerHour);

        return spotRepository1.save(spot);
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        Optional<ParkingLot> optionalParkingLot=parkingLotRepository1.findById(parkingLotId);

        if(!optionalParkingLot.isPresent())
        {
            throw new NotFoundException("Parking lot doesn't exists");
        }
        ParkingLot parkingLot=optionalParkingLot.get();

        List<Spot> spots=parkingLot.getSpotList();
        if((spots!=null)&&(!spots.isEmpty()))
        {
            for(Spot spot:spots)
                spotRepository1.delete(spot);
        }

        parkingLotRepository1.delete(parkingLot);
    }
}
