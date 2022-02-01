package com.dune.game.core.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.GameController;
import com.dune.game.core.units.*;
import com.dune.game.screens.ScreenManager;

import java.util.ArrayList;
import java.util.List;

import static com.dune.game.core.BattleMap.CELL_SIZE;
import static com.dune.game.core.units.Owner.AI;
import static com.dune.game.core.units.Owner.PLAYER;

public class UnitsController {
    private BattleTankController battleTankController;
    private HarvestersController harvestersController;
    private BuildingController buildingController;
    private HealBotsController healBotsController;
    private GameController gameController;
    private List <AbstractUnit> units;
    private List <AbstractUnit> playerUnits;
    private List <AbstractUnit> aiUnits;
    private List <BattleTank> aiBattleTanks;
    private List <BattleTank> playerBattleTanks;
    private List <Harvester> playerHarvesterUnits;
    private List <Harvester> aiHarvesterUnits;
    private List <HealTank> playerHealTanks;
    private Building basePlayer;  // определяем базу
    private Building baseAi;      // определяем базу

    public UnitsController(GameController gameController) {
        this.gameController = gameController;
        this.battleTankController = new BattleTankController(gameController);
        this.harvestersController = new HarvestersController(gameController);
        this.healBotsController = new HealBotsController(gameController);
        this.buildingController = new BuildingController(gameController);
        this.units = new ArrayList<>();
        this.playerUnits = new ArrayList<>();
        this.playerHealTanks = new ArrayList<>();
        this.playerBattleTanks = new ArrayList<>();
        this.aiHarvesterUnits = new ArrayList<>();
        this.playerHarvesterUnits = new ArrayList<>();
        this.aiUnits = new ArrayList<>();
        this.aiBattleTanks = new ArrayList<>();

        createStartUnits();
    }
    public void render(SpriteBatch batch){
        buildingController.render(batch);
        battleTankController.render(batch);
        harvestersController.render(batch);
        healBotsController.render(batch);

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
        healBotsController.update(dt);
        units.clear();
        aiUnits.clear();
        playerUnits.clear();
        aiBattleTanks.clear();
        playerHarvesterUnits.clear();
        playerHealTanks.clear();
        playerBattleTanks.clear();
        aiHarvesterUnits.clear();
        units.addAll(battleTankController.getActiveList());
        units.addAll(harvestersController.getActiveList());
        units.addAll(buildingController.getActiveList());
        units.addAll(healBotsController.getActiveList());
        for (int i = 0; i < units.size(); i++) {
            if(units.get(i).getOwnerType() == Owner.AI){
                aiUnits.add(units.get(i));
                if(units.get(i).getUnitType() == UnitType.BATTLE_TANK){     //заполняем список боевых танков Аи
                    aiBattleTanks.add((BattleTank)units.get(i));
                }
                if(units.get(i).getUnitType() == UnitType.HARVESTER){   // заполняем список харвестеров пользователя
                    aiHarvesterUnits.add((Harvester) units.get(i));
                }
            }
            if(units.get(i).getOwnerType() == PLAYER){
                playerUnits.add(units.get(i));
                if(units.get(i).getUnitType() == UnitType.HARVESTER){   // заполняем список харвестеров пользователя
                    playerHarvesterUnits.add((Harvester) units.get(i));
                }
                if(units.get(i).getUnitType() == UnitType.HEALTANK){   // заполняем список харвестеров пользователя
                    playerHealTanks.add((HealTank) units.get(i));
                }
                if(units.get(i).getUnitType() == UnitType.BATTLE_TANK){   // заполняем список харвестеров пользователя
                    playerBattleTanks.add((BattleTank) units.get(i));
                }
            }
        }
        if(aiUnits.size() == 0){
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GOPLWIN);
        }
        if(playerUnits.size() == 0){
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GOAIWIN);
        }
    }

    public void createStartUnits(){
        this.basePlayer = createBuilding(100,100, PLAYER);                   //создание базы игрока
        this.baseAi = createBuilding(27*CELL_SIZE,18*CELL_SIZE, AI);         //создание базы АИ
        createHarvester(basePlayer.getPosition().x+MathUtils.random(150,200), basePlayer.getPosition().y+MathUtils.random(150,200), PLAYER);//создание харвестера для игрока
        //createHealTank(basePlayer.getPosition().x+MathUtils.random(150,200), basePlayer.getPosition().y+MathUtils.random(150,200), PLAYER);
        //createBattleTank(basePlayer.getPosition().x+MathUtils.random(150,200), basePlayer.getPosition().y+MathUtils.random(150,200), PLAYER);//создание харвестера для игрока
        createHarvester(baseAi.getPosition().x-MathUtils.random(150,200), baseAi.getPosition().y-MathUtils.random(150,200), AI);    //создание харвестера для АИ

    }
    public BattleTank createBattleTank (float x, float y, Owner owner){
        return battleTankController.setup(x,y,owner);
    }
    public Harvester createHarvester (float x, float y, Owner owner){
        return harvestersController.setup(x,y,owner);
    }
    public Building createBuilding (float x, float y, Owner owner){
        return buildingController.setup(x,y,owner);
    }
    public HealTank createHealTank (float x, float y, Owner owner){
        return healBotsController.setup(x,y,owner);
    }


    public AbstractUnit getNearestAiUnit(Vector2 point){
        for(AbstractUnit aiUnit: aiUnits){
            if(aiUnit.getPosition().dst(point) <30){
                return aiUnit;
            }
        }
        return null;
    }
    public AbstractUnit getNearestPlUnit(Vector2 point){
        for(AbstractUnit playerUnit: playerUnits){
            if(playerUnit.getPosition().dst(point) <30){
                return playerUnit;
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
    public List<BattleTank> getPlayerBattleTanks() {
        return playerBattleTanks;
    }
    public List<HealTank> getPlayerHealTanks() {
        return playerHealTanks;
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
    public List<Harvester> getAiHarvesterUnits() {
        return aiHarvesterUnits;
    }
}
