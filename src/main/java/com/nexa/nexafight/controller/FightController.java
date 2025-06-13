package com.nexa.nexafight.controller;

import com.nexa.nexafight.dto.FighterDTO;
import com.nexa.nexafight.service.FightService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FightController {

    private final FightService fightService;

    public FightController(final FightService fightService) {
        this.fightService = fightService;
    }

    @GetMapping("/fight")
    public FighterDTO fight(@RequestParam(value = "id_fighter1") int idFighter1,
                            @RequestParam(value = "id_fighter2") int idFighter2) {
        return this.fightService.fight(idFighter1, idFighter2);
    }

}
