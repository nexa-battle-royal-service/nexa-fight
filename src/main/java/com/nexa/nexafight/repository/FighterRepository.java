package com.nexa.nexafight.repository;

import com.nexa.nexafight.dto.FighterDTO;
import com.nexa.nexafight.entity.Fighter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface FighterRepository extends JpaRepository<Fighter, Integer> {

    static FighterDTO toDTO(Fighter fighter) {
        ;
        if (fighter == null) {
            return null;
        }
        return new FighterDTO(
                fighter.getId(),
                fighter.getName(),
                fighter.getConstitution(),
                fighter.getStrength(),
                fighter.getDexterity(),
                fighter.getXp(),
                fighter.getUrl(),
                fighter.getPlayer()
        );
    }

    @Procedure(procedureName = "getFighterById")
    Fighter findById(int id);
}
