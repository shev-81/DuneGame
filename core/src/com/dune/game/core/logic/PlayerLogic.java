package com.dune.game.core.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.GameController;
import com.dune.game.core.units.*;
import com.dune.game.screens.AbstractScreen;

import static com.dune.game.core.units.Owner.PLAYER;


public class PlayerLogic {

    private GameController gameController;
    private int money;
    private int unitsMaxCount;
    private Vector2 tmpV;
    private Vector2 tmpPointHarv;
    private float countTimeDo;

    public PlayerLogic(GameController gameController) {
        this.gameController = gameController;
        this.tmpV = new Vector2();
        this.tmpPointHarv = new Vector2();
        this.unitsMaxCount = 5;
    }
    public void update(float dt){
        countTimeDo += dt;
        // проверяем есть ли рядом цели
        if(countTimeDo > 1){
            for(AbstractUnit plTank: gameController.getUnitsController().getPlayerUnits()){
                if(plTank.getUnitType() == UnitType.BATTLE_TANK){
                    for(AbstractUnit enemy: gameController.getUnitsController().getAiUnits()){
                        if(plTank.getPosition().dst(enemy.getPosition())<400 && plTank.getTarget() == null){
                            plTank.setTarget(enemy);
                        }
                    }
                }
            }
            countTimeDo = 0;
        }

        // реакция на правый клик мыши
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)){
            tmpV.set(gameController.getMouse());
            for(AbstractUnit unit: gameController.getSelectedUnits()) {
                if(unit.getOwnerType() == Owner.PLAYER){
                    unitProcessing(unit);
                }
                if(unit.getOwnerType() == Owner.PLAYER && unit.getUnitType() == UnitType.BUILDING){
                    gameController.getTestBtn().setVisible(true);
                }
            }
        }

        // если здание базы не выбрано то скрываем кнопку
        if(!gameController.getSelectedUnits().contains(gameController.getUnitsController().getBasePlayer())){
            gameController.getTestBtn().setVisible(false);
        }

        //  если денег хватает на боевой танк то создаем
        if(money >= 1000 && getUnitsCount() < unitsMaxCount){
            Building basePlayer = gameController.getUnitsController().getBasePlayer();  // определяем базу
            if(basePlayer.isActive()){
                BattleTank tank = gameController.getUnitsController().createBattleTank(basePlayer.getPosition().x, basePlayer.getPosition().y, PLAYER);
                tmpV.set(basePlayer.getPosition().x+MathUtils.random(150,200), basePlayer.getPosition().y+MathUtils.random(150,200));
                tank.commandMoveTo(tmpV);
            }
            money = money - 1000;
        }

        // если харвестер готов к разгрузке то едет на базу выгружать ресурс
        Building plB = gameController.getUnitsController().getBasePlayer();
        for (int j = 0; j < gameController.getUnitsController().getPlayerHarvesterUnits().size(); j++) {
            Harvester hPl = gameController.getUnitsController().getPlayerHarvesterUnits().get(j);
            // если харвестер находится в режиме сбора
            if(hPl.isHarvesterWorking()){
                // если контейнер полон
                if (hPl.getContainer() >= hPl.CONTAINER_CAPACITY) {
                    hPl.commandMoveTo(plB.getPosition());
                }
                // если харвестер имеет пустой контейнер но ему был ранее назначен поинт сбора он двигает к ближайшему ресурсу от точки сбора
                if (hPl.getContainer() == 0) {
                    tmpV.set(gameController.getBattleMap().getResourceNearestPosition(hPl.getTargetPointHarvest()));
                    if(hPl.getTargetPointHarvest().dst(tmpV)<300){
                        hPl.commandMoveTo(tmpV);
                    }else{
                        hPl.commandMoveTo(hPl.getTargetPointHarvest());
                    }
                }
                // если контейнер не пуст и не полон то назначаем ему ближайшую точку сбора ресурса
                if (hPl.getContainer() > 0 && hPl.getContainer() < hPl.CONTAINER_CAPACITY) {
                    tmpV.set(gameController.getBattleMap().getResourceNearestPosition(hPl.getPosition()));      // определяем ближайшую точку с ресурсом от точки сбора
                    if(tmpV.dst(hPl.getPosition()) < 200){                                                      // если ближайшая точка сбора ресурсов не далеко от точки сбора то едем
                        hPl.commandMoveTo(tmpV);                                                                // определенной пользователем то при отсутствиии ресурсов стоим на ней
                    }else{                                                                                      // то едем
                        hPl.commandMoveTo(hPl.getTargetPointHarvest());                                         // а иначе елем на точку сбора
                    }
                }
            }
        }
    }

    public void unitProcessing(AbstractUnit unit) {
        if (unit.getUnitType() == UnitType.HARVESTER) {
            tmpPointHarv.set(tmpV);
            if(gameController.getBattleMap().getResourceCount(tmpPointHarv)>0){   // если таргет спайс и больше 0
                ((Harvester)unit).setTargetPointHarvest(tmpPointHarv);            // устанавливаем поинт сбора
                ((Harvester)unit).setHarvesterWorking(true);                      // и переводим харвестер в режим работает сбор
            }else{
                ((Harvester)unit).setHarvesterWorking(false);
                unit.commandMoveTo(gameController.getMouse());
            }
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
        return gameController.getUnitsController().getPlayerUnits().size();
    }

    public void addMoney(int money) {
        this.money = this.money + money*100;
    }


    public int getUnitsMaxCount() {
        return unitsMaxCount;
    }
}
