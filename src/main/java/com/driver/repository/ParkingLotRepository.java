package com.driver.repository;

import com.driver.model.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Integer>{
    @Override
    Optional<ParkingLot> findById(Integer id);
}
