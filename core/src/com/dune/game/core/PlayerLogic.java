package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.UnitType;

import static com.dune.game.core.Owner.AI;
import static com.dune.game.core.Owner.PLAYER;


public class PlayerLogic {

    private GameController gameController;
    private int money;
    private int unitsCount;
    private int unitsMaxCount;

    public PlayerLogic(GameController gameController) {
        this.gameController = gameController;
    }
    public void update(float dt){
        // реакция на правый клик мыши
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){
            for(AbstractUnit unit: gameController.getSelectedUnits()) {
                if(unit.getOwnerType() == Owner.PLAYER){
                    unitProcessing(unit);
                }
            }
        }
        if(money>=10){    //  если денег хватает на боевой танк то создаем
            gameController.getUnitsController().createBattleTank(MathUtils.random(50, 1100), MathUtils.random(50, 650), PLAYER);
            money = money - 10;
        }
    }

    public void unitProcessing(AbstractUnit unit) {
        if (unit.getUnitType() == UnitType.HARVESTER) {
            unit.commandMoveTo(gameController.getMouse());
            return;
        }
        if (unit.getUnitType() == UnitType.BATTLE_TANK) {
            AbstractUnit aiUnit = gameController.getUnitsController().getNearestAiUnit(gameController.getMouse());
            if (aiUnit == null) {
                unit.commandMoveTo(gameController.getMouse());
            } else {
                unit.commandAttack(aiUnit);
            }
        }
    }
    public int getMoney() {
        return money;
    }

    public int getUnitsCount() {
        unitsCount = 0;
        for(AbstractUnit unit: gameController.getUnitsController().getPlayerUnits()){
            unitsCount++;
        }
        return unitsCount;
    }

    public void setMoney(int money) {
        this.money = this.money + money;
    }


    public int getUnitsMaxCount() {
        return unitsMaxCount;
    }
}
