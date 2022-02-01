package com.dune.game.core.logic;

import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.GameController;
import com.dune.game.core.units.*;

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
        this.money = 1000;
    }

    public void update(float dt) {
        countTimeDo += dt;
        // проверяем есть ли рядом цели
        if (countTimeDo > 1) {
            for (AbstractUnit plTank : gameController.getUnitsController().getPlayerUnits()) {
                if (plTank.getUnitType() == UnitType.BATTLE_TANK) {
                    for (AbstractUnit enemy : gameController.getUnitsController().getAiUnits()) {
                        if (plTank.getPosition().dst(enemy.getPosition()) < 400 && plTank.getTarget() == null && plTank.getPosition().dst(plTank.getDestination()) < 50) {
                            plTank.setTarget(enemy);
                        }
                    }
                }
            }
            countTimeDo = 0;
        }
        // если здание базы не выбрано то скрываем кнопку
        if (!gameController.getSelectedUnits().contains(gameController.getUnitsController().getBasePlayer())) {
            gameController.getBaseMenu().setVisible(false);
            gameController.getCreateTanksMenu().setVisible(false);
            gameController.getUpgradesMenu().setVisible(false);
        }
        // если харвестер готов к разгрузке то едет на базу выгружать ресурс
        Building plB = gameController.getUnitsController().getBasePlayer();
        for (int j = 0; j < gameController.getUnitsController().getPlayerHarvesterUnits().size(); j++) {
            Harvester hPl = gameController.getUnitsController().getPlayerHarvesterUnits().get(j);
            // если харвестер находится в режиме сбора
            if (hPl.isHarvesterWorking()) {
                // если контейнер полон
                if (hPl.getContainer() >= hPl.CONTAINER_CAPACITY) {
                    hPl.commandMoveTo(plB.getPosition(),true);
                }
                // если харвестер имеет пустой контейнер но ему был ранее назначен поинт сбора он двигает к ближайшему ресурсу от точки сбора
                if (hPl.getContainer() == 0) {
                    tmpV.set(gameController.getBattleMap().getResourceNearestPosition(hPl.getTargetPointHarvest()));
                    if (hPl.getTargetPointHarvest().dst(tmpV) < 300) {
                        hPl.commandMoveTo(tmpV,true);
                    } else {
                        hPl.commandMoveTo(hPl.getTargetPointHarvest(),true);
                    }
                }
                // если контейнер не пуст и не полон то назначаем ему ближайшую точку сбора ресурса
                if (hPl.getContainer() > 0 && hPl.getContainer() < hPl.CONTAINER_CAPACITY) {
                    tmpV.set(gameController.getBattleMap().getResourceNearestPosition(hPl.getPosition()));     // определяем ближайшую точку с ресурсом от точки сбора
                    if (tmpV.dst(hPl.getPosition()) < 200) {                                                   // если ближайшая точка сбора ресурсов не далеко от точки сбора то едем
                        hPl.commandMoveTo(tmpV,false);                                                // определенной пользователем то при отсутствиии ресурсов стоим на ней
                    } else {                                                                                   // то едем
                        hPl.commandMoveTo(hPl.getTargetPointHarvest(),true);                          // а иначе елем на точку сбора
                    }
                }
            }
        }
    }

    public int getMoney() {
        return money;
    }
    public void addUnitsMaxCount(){
        unitsMaxCount++;
    }
    public int getUnitsCount() {
        return gameController.getUnitsController().getPlayerUnits().size();
    }
    public void addMoney(int money) {
        this.money = this.money + money * 100;
    }
    public void minusMoney(int money) {
        this.money = this.money - money;
    }

    public int getUnitsMaxCount() {
        return unitsMaxCount;
    }
}
