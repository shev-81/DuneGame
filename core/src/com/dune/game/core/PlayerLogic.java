package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.UnitType;


public class PlayerLogic {

    private GameController gameController;

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
}
