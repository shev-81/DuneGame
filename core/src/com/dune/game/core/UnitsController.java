package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.units.AbstractUnit;
import java.util.ArrayList;
import java.util.List;

import static com.dune.game.core.Owner.AI;
import static com.dune.game.core.Owner.PLAYER;

public class UnitsController {
    private BattleTankController battleTankController;
    private HarvestersController harvestersController;
    private BuildingController buildingController;
    private GameController gameController;
    private List <AbstractUnit> units;
    private List <AbstractUnit> playerUnits;
    private List <AbstractUnit> aiUnits;

    public UnitsController(GameController gameController) {
        this.gameController = gameController;
        this.battleTankController = new BattleTankController(gameController);
        this.harvestersController = new HarvestersController(gameController);
        this.buildingController = new BuildingController(gameController);
        this.units = new ArrayList<>();
        this.playerUnits = new ArrayList<>();
        this.aiUnits = new ArrayList<>();
        for (int i = 0; i < 2; i++) {       // создаем стартовых юнитов
//            createBattleTank(MathUtils.random(50, 1100), MathUtils.random(50, 650), PLAYER);            //создание танка при помощи setup
            createHarvester(MathUtils.random(50, 1100), MathUtils.random(50, 650), PLAYER);             //создание танка при помощи setup
//            createBattleTank(MathUtils.random(50, 1100), MathUtils.random(50, 650), Owner.AI);          //создание танка при помощи setup
            createHarvester(MathUtils.random(50, 1100), MathUtils.random(50, 650), Owner.AI);           //создание танка при помощи setup
        }
        createBuilding(100,100, PLAYER);            //создание строения при помощи setup
        createBuilding(1500,850, AI);                //создание строения при помощи setup
    }
    public  void render(SpriteBatch batch){
        buildingController.render(batch);
        battleTankController.render(batch);
        harvestersController.render(batch);

        // рамка выбора юнитов
        for(AbstractUnit unit: gameController.getSelectedUnits()) {
            switch (unit.getOwnerType()) {
                case PLAYER:
                    batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
                    batch.draw(unit.getBorderline(), unit.position.x + 38, unit.position.y + 46, 0, 0, 78, 2, 1, 1, -180);   // верхняя
                    batch.draw(unit.getBorderline(), unit.position.x - 40, unit.position.y - 40, 0, 0, 78, 2, 1, 1, 0);      // нижняя
                    batch.draw(unit.getBorderline(), unit.position.x + 40, unit.position.y - 38, 0, 0, 82, 2, 1, 1, 90);     // правая
                    batch.draw(unit.getBorderline(), unit.position.x - 80, unit.position.y + 2, 40, 1, 82, 2, 1, 1, -90);    // левая
                    batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    break;
                case AI:
                    batch.setColor(1.0f, 0.0f, 0.0f, 1.0f);
                    batch.draw(unit.getBorderline(), unit.position.x + 38, unit.position.y + 46, 0, 0, 78, 2, 1, 1, -180);   // верхняя
                    batch.draw(unit.getBorderline(), unit.position.x - 40, unit.position.y - 40, 0, 0, 78, 2, 1, 1, 0);      // нижняя
                    batch.draw(unit.getBorderline(), unit.position.x + 40, unit.position.y - 38, 0, 0, 82, 2, 1, 1, 90);     // правая
                    batch.draw(unit.getBorderline(), unit.position.x - 80, unit.position.y + 2, 40, 1, 82, 2, 1, 1, -90);    // левая
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
        units.addAll(battleTankController.getActiveList());
        units.addAll(harvestersController.getActiveList());
        units.addAll(buildingController.getActiveList());
        for (int i = 0; i < units.size(); i++) {
            if(units.get(i).getOwnerType() == Owner.AI){
                aiUnits.add(units.get(i));
            }
            if(units.get(i).getOwnerType() == PLAYER){
                playerUnits.add(units.get(i));
            }
        }
    }

    public void createBattleTank (float x, float y, Owner owner){
        battleTankController.setup(x,y,owner);
    }
    public void createHarvester (float x, float y, Owner owner){
        harvestersController.setup(x,y,owner);
    }
    public void createBuilding (float x, float y, Owner owner){
        buildingController.setup(x,y,owner);
    }

    public AbstractUnit getNearestAiUnit(Vector2 point){
        for(AbstractUnit aiUnit: aiUnits){
            if(aiUnit.getPosition().dst(point) <30){
                return aiUnit;
            }
        }
        return null;
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
