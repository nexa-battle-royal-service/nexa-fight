package com.nexa.nexafight.repository;

import com.nexa.nexafight.entity.Fighter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FightRepository extends JpaRepository<Fighter, Integer> {

    @Query(value = "CALL addFight(:fighter1Id, :fighter2Id, :winnerId)", nativeQuery = true)
    Integer addFight(int fighter1Id, int fighter2Id, int winnerId);
}
