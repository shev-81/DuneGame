package com.dune.game.core;

import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.UnitType;

public class AiLogic {

    private GameController gameController;

    public AiLogic(GameController gameController) {
        this.gameController = gameController;
    }

    public void update(float dt){
        // проверяем есть ли рядом вражеский танк
        for(AbstractUnit unit: gameController.getUnitsController().getAiUnits()){
            for(AbstractUnit enemy: gameController.getUnitsController().getPlayerUnits()){
                if(unit.getPosition().dst(enemy.getPosition()) < 400 && unit.getUnitType() != UnitType.HARVESTER){
                    unit.setTarget(enemy);
                }
                if(unit.getTarget() != null && ((AbstractUnit)unit.getTarget()).getHp() > enemy.getHp()){
                    unit.setTarget(enemy);
                }
            }
        }
    }
}
