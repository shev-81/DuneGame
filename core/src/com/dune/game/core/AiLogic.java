package com.dune.game.core;

import com.badlogic.gdx.math.MathUtils;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.Harvester;
import com.dune.game.core.units.UnitType;

import static com.dune.game.core.Owner.AI;
import static com.dune.game.core.Owner.PLAYER;

public class AiLogic {

    private GameController gameController;
    private int moneyAi;

    public AiLogic(GameController gameController) {
        this.gameController = gameController;
    }

    public void update(float dt){
        // проверяем есть ли рядом вражеский танк
        for(AbstractUnit unit: gameController.getUnitsController().getAiUnits()){
            for(AbstractUnit enemy: gameController.getUnitsController().getPlayerUnits()) {
                if (unit.getPosition().dst(enemy.getPosition()) < 400 && unit.getUnitType() != UnitType.HARVESTER) {
                    unit.setTarget(enemy);
                }
                if (unit.getTarget() != null && ((AbstractUnit) unit.getTarget()).getHp() > enemy.getHp()) {
                    unit.setTarget(enemy);
                }
            }
            // если юнит харвестер ищем ближайший спавн спайса
            if(unit.getUnitType() == UnitType.HARVESTER){
                if(unit.getContainer() < unit.CONTAINER_CAPACITY) {
                    unit.commandMoveTo(gameController.getBattleMap().resourcePosition((Harvester) unit));
                }
            }
        }
        for (int i = 0; i < gameController.getUnitsController().getUnits().size(); i++) {
            AbstractUnit u1 = gameController.getUnitsController().getUnits().get(i);
            if (u1.getUnitType() == UnitType.BUILDING) {
                for (int j = 0; j < gameController.getUnitsController().getUnits().size(); j++) {
                    AbstractUnit u2 = gameController.getUnitsController().getUnits().get(j);
                    if (u1.getOwnerType() == u2.getOwnerType() && u2.getUnitType() == UnitType.HARVESTER && u2.getContainer() >= u2.CONTAINER_CAPACITY) {
                        u2.commandMoveTo(u1.getPosition());
                    }
                }
            }
        }
        if(moneyAi>=10){    //  если денег хватает на боевой танк то создаем
            gameController.getUnitsController().createBattleTank(MathUtils.random(50, 1100), MathUtils.random(50, 650), AI);
            moneyAi =moneyAi - 10;
        }
    }
    public void setMoney(int money) {
        this.moneyAi = this.moneyAi + money;
    }
}
