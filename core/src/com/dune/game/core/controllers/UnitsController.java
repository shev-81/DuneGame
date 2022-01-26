package com.dune.game.core.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.GameController;
import com.dune.game.core.units.*;

import java.util.ArrayList;
import java.util.List;

import static com.dune.game.core.units.Owner.AI;
import static com.dune.game.core.units.Owner.PLAYER;

public class UnitsController {
    private BattleTankController battleTankController;
    private HarvestersController harvestersController;
    private BuildingController buildingController;
    private GameController gameController;
    private List <AbstractUnit> units;
    private List <AbstractUnit> playerUnits;
    private List <AbstractUnit> aiUnits;
    private List <BattleTank> aiBattleTanks;
    private List <Harvester> playerHarvesterUnits;
    private Building basePlayer;  // определяем базу
    private Building baseAi;      // определяем базу

    public UnitsController(GameController gameController) {
        this.gameController = gameController;
        this.battleTankController = new BattleTankController(gameController);
        this.harvestersController = new HarvestersController(gameController);
        this.buildingController = new BuildingController(gameController);
        this.units = new ArrayList<>();
        this.playerUnits = new ArrayList<>();
        this.playerHarvesterUnits = new ArrayList<>();
        this.aiUnits = new ArrayList<>();
        this.aiBattleTanks = new ArrayList<>();
        createStartUnits();
    }
    public void render(SpriteBatch batch){
        buildingController.render(batch);
        battleTankController.render(batch);
        harvestersController.render(batch);

        // рамка выбора юнитов
        for(AbstractUnit unit: gameController.getSelectedUnits()) {
            switch (unit.getOwnerType()) {
                case PLAYER:
                    batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
                    batch.draw(unit.getBorderline(), unit.getPosition().x + 38, unit.getPosition().y + 46, 0, 0, 78, 2, 1, 1, -180);   // верхняя
                    batch.draw(unit.getBorderline(), unit.getPosition().x - 40, unit.getPosition().y - 40, 0, 0, 78, 2, 1, 1, 0);      // нижняя
                    batch.draw(unit.getBorderline(), unit.getPosition().x + 40, unit.getPosition().y - 38, 0, 0, 82, 2, 1, 1, 90);     // правая
                    batch.draw(unit.getBorderline(), unit.getPosition().x - 80, unit.getPosition().y + 2, 40, 1, 82, 2, 1, 1, -90);    // левая
                    batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    break;
                case AI:
                    batch.setColor(1.0f, 0.0f, 0.0f, 1.0f);
                    batch.draw(unit.getBorderline(), unit.getPosition().x + 38, unit.getPosition().y + 46, 0, 0, 78, 2, 1, 1, -180);   // верхняя
                    batch.draw(unit.getBorderline(), unit.getPosition().x - 40, unit.getPosition().y - 40, 0, 0, 78, 2, 1, 1, 0);      // нижняя
                    batch.draw(unit.getBorderline(), unit.getPosition().x + 40, unit.getPosition().y - 38, 0, 0, 82, 2, 1, 1, 90);     // правая
                    batch.draw(unit.getBorderline(), unit.getPosition().x - 80, unit.getPosition().y + 2, 40, 1, 82, 2, 1, 1, -90);    // левая
                    batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    break;
            }
        }
    }

    public void update(float dt){
        battleTankController.update(dt);
        harvestersController.update(dt);
        buildingController.update(dt);
        units.clear();
        aiUnits.clear();
        playerUnits.clear();
        aiBattleTanks.clear();
        playerHarvesterUnits.clear();
        units.addAll(battleTankController.getActiveList());
        units.addAll(harvestersController.getActiveList());
        units.addAll(buildingController.getActiveList());
        for (int i = 0; i < units.size(); i++) {
            if(units.get(i).getOwnerType() == Owner.AI){
                aiUnits.add(units.get(i));
                if(units.get(i).getUnitType() == UnitType.BATTLE_TANK){     //заполняем список боевых танков Аи
                    aiBattleTanks.add((BattleTank)units.get(i));
                }
            }
            if(units.get(i).getOwnerType() == PLAYER){
                playerUnits.add(units.get(i));
                if(units.get(i).getUnitType() == UnitType.HARVESTER){   // заполняем список харвестеров пользователя
                    playerHarvesterUnits.add((Harvester) units.get(i));
                }
            }
        }
    }

    public void createStartUnits(){
        this.basePlayer = createBuilding(100,100, PLAYER);                                     //создание базы игрока
        this.baseAi = createBuilding(1500,850, AI);                                            //создание базы АИ
        createHarvester(basePlayer.getPosition().x+MathUtils.random(150,200), basePlayer.getPosition().y+MathUtils.random(150,200), PLAYER);//создание харвестера для игрока
        createBattleTank(basePlayer.getPosition().x+MathUtils.random(150,200), basePlayer.getPosition().y+MathUtils.random(150,200), PLAYER);//создание харвестера для игрока
        createHarvester(baseAi.getPosition().x-MathUtils.random(150,200), baseAi.getPosition().y-MathUtils.random(150,200), AI);    //создание харвестера для АИ
    }
    public BattleTank createBattleTank (float x, float y, Owner owner){
        return battleTankController.setup(x,y,owner);
    }
    public void createHarvester (float x, float y, Owner owner){
        harvestersController.setup(x,y,owner);
    }
    public Building createBuilding (float x, float y, Owner owner){
        return buildingController.setup(x,y,owner);
    }

    public AbstractUnit getNearestAiUnit(Vector2 point){
        for(AbstractUnit aiUnit: aiUnits){
            if(aiUnit.getPosition().dst(point) <30){
                return aiUnit;
            }
        }
        return null;
    }

    public List<Harvester> getPlayerHarvesterUnits() {
        return playerHarvesterUnits;
    }

    public Building getBasePlayer() {
        return basePlayer;
    }

    public Building getBaseAi() {
        return baseAi;
    }

    public List<BattleTank> getAiBattleTanks() {
        return aiBattleTanks;
    }

    public List<AbstractUnit> getUnits() {
        return units;
    }

    public List<AbstractUnit> getAiUnits() {
        return aiUnits;
    }

    public List<AbstractUnit> getPlayerUnits() {
        return playerUnits;
    }
}
