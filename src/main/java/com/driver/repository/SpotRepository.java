package com.driver.repository;

import com.driver.model.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Integer>{
    @Override
    Optional<Spot> findById(Integer id);


}
