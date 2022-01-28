package com.dune.game.core.logic;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.GameController;
import com.dune.game.core.units.*;

import static com.dune.game.core.units.AbstractUnit.CONTAINER_CAPACITY;
import static com.dune.game.core.units.Owner.AI;

public class AiLogic {

    private GameController gameController;
    private int moneyAi;
    private Vector2 tmpV;
    private int unitsMaxCount;


    public AiLogic(GameController gameController) {
        this.gameController = gameController;
        this.tmpV = new Vector2();
        this.unitsMaxCount = 5;
    }

    public void update(float dt){
        // если есть несколько боевых танков то отправляем их воевать с игроком
        if(gameController.getUnitsController().getAiBattleTanks().size() >= 2){
            for (int i = 0; i < gameController.getUnitsController().getAiBattleTanks().size(); i++) {
                BattleTank aiBt = gameController.getUnitsController().getAiBattleTanks().get(i);
                if(gameController.getUnitsController().getPlayerHarvesterUnits().size()>0){
                    aiBt.setTarget(gameController.getUnitsController().getPlayerHarvesterUnits().get(0));
                }
            }
        }
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
            // если юнит харвестер ищем ближайший спавн спайса если спайса нет то возвращается на базу
            if(unit.getUnitType() == UnitType.HARVESTER){
                if(unit.getContainer() < CONTAINER_CAPACITY) {
                    if(gameController.getBattleMap().getResourceNearestPosition(unit.getPosition()) == null){
                        unit.commandMoveTo(gameController.getUnitsController().getBaseAi().getPosition(),true);
                    }else {
                        unit.commandMoveTo(gameController.getBattleMap().getResourceNearestPosition(unit.getPosition()),true);
                    }
                }
            }
        }
        // если харвестер готов к разгрузке то едет на базу выгружать ресурс
        Building aiB = gameController.getUnitsController().getBaseAi();
        for (int j = 0; j < gameController.getUnitsController().getUnits().size(); j++) {
            AbstractUnit hAi = gameController.getUnitsController().getUnits().get(j);
            if (aiB.getOwnerType() == hAi.getOwnerType() && hAi.getUnitType() == UnitType.HARVESTER && hAi.getContainer() >= CONTAINER_CAPACITY) {
                hAi.commandMoveTo(aiB.getPosition(),true);
            }
        }

        if(moneyAi >= 1000 && getUnitsCount() < unitsMaxCount){    //  если денег хватает на боевой танк и юнитов меньше максимально доступных то создаем
            Building baseAi = gameController.getUnitsController().getBaseAi();  // определяем базу
            if(baseAi.isActive()){
                gameController.getUnitsController().createBattleTank(baseAi.getPosition().x-MathUtils.random(150,200), baseAi.getPosition().y-MathUtils.random(150,200), AI);
            }
            moneyAi =moneyAi - 1000;
        }
    }
    public int getUnitsCount() {
        return gameController.getUnitsController().getAiUnits().size();
    }
    public void addMoney(int money) {
        this.moneyAi = this.moneyAi + money*100;
    }
}
