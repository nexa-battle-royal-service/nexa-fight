package com.nexa.nexafight.service;

import com.nexa.nexafight.dto.FighterDTO;
import com.nexa.nexafight.repository.FightRepository;
import com.nexa.nexafight.repository.FighterRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Random;

@Service

public class FightService {

    private static final Random RANDOM = new Random();
    private final FighterRepository fighterRepository;
    private final FightRepository fightRepository;

    public FightService(final FighterRepository fighterRepository, final FightRepository fightRepository) {
        this.fighterRepository = fighterRepository;
        this.fightRepository = fightRepository;
    }

    public FighterDTO fight(final int idFighter1, final int idFighter2) {
        boolean isFighter1Valid = this.checkFighter(idFighter1);
        boolean isFighter2Valid = this.checkFighter(idFighter2);

        if (!isFighter1Valid && !isFighter2Valid) {
            return new FighterDTO(0, "Invalid fighter ID(s)", 0, 0, 0, 0, "", "draw");
        }

        FighterDTO winner = null;
        FighterDTO fighter1 = null;
        FighterDTO fighter2 = null;

        if (isFighter1Valid) fighter1 = FighterRepository.toDTO(this.fighterRepository.findById(idFighter1));
        if (isFighter2Valid) fighter2 = FighterRepository.toDTO(this.fighterRepository.findById(idFighter2));

        if (!isFighter1Valid || !isFighter2Valid) {
            if (isFighter1Valid) {
                winner = fighter1;
            } else {
                winner = fighter2;
            }
            this.fightRepository.addFight(idFighter1, idFighter2, winner != null ? winner.id() : 0);
            return winner;
        }

        int pvFighter1 = Objects.requireNonNull(fighter1).constitution();
        int pvFighter2 = Objects.requireNonNull(fighter2).constitution();

        while (pvFighter1 > 0 && pvFighter2 > 0) {
            pvFighter1 -= FightService.attack(fighter2, fighter1) ? FightService.damage(fighter2) : 0;
            pvFighter2 -= FightService.attack(fighter1, fighter2) ? FightService.damage(fighter1) : 0;
        }
        if (pvFighter1 <= 0) winner = fighter2;
        if (pvFighter2 <= 0) winner = fighter1;
        if (pvFighter1 <= 0 && pvFighter2 <= 0) winner = null;
        this.fightRepository.addFight(idFighter1, idFighter2, winner != null ? winner.id() : 0);
        RestTemplate restTemplate = new RestTemplate();
        if (winner == fighter1) {
            restTemplate.postForObject(fighter1.url() + "/win",
                                       null, Void.class);
        } else {
            restTemplate.postForObject(fighter1.url() + "/loose",
                                       null, Void.class);
        }
        if (winner == fighter2) {
            restTemplate.postForObject(fighter2.url() + "/win",
                                       null, Void.class);
        } else {
            restTemplate.postForObject(fighter1.url() + "/loose",
                                       null, Void.class);
        }
        restTemplate.postForObject(fighter1.url() + "/fight",
                                   null, Void.class);
        return winner != null ? winner : new FighterDTO(0, "draw", 0, 0, 0, 0, "", "draw");
    }

    private boolean checkFighter(final int idFighter) {
        RestTemplate restTemplate = new RestTemplate();
        String answer = restTemplate.getForObject("http://localhost:8085/check-fighter?id=" + idFighter + "&verbose=n",
                                                  String.class);
        return "true".equalsIgnoreCase(answer);
    }

    private static boolean attack(final FighterDTO attacker, final FighterDTO defender) {
        if (attacker == null || defender == null) {
            throw new IllegalArgumentException("Fighter cannot be null");
        }
        return FightService.getRandomInRange(1, attacker.dexterity()) >= FightService.getRandomInRange(1,
                                                                                                       defender.dexterity());
    }

    private static int damage(final FighterDTO fighter) {
        if (fighter == null) {
            throw new IllegalArgumentException("Fighter cannot be null");
        }
        return FightService.getRandomInRange(1, fighter.strength());


    }

    private static int getRandomInRange(final int min, final int max) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(
                "http://localhost:8082/random-range?min=" + min + "&max=" + max,
                Integer.class);
    }
}
