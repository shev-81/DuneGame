package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class GameController {

    private Tank tank;
    private BattleMap battleMap;
    private EnemyController enemyController;
    private ProjectesController projectesController;
    private TextureAtlas atlas;

    // инициализация игровой логики
    public GameController() {
        Assets.getInstance().loadAssets();                          //загрузка ресурсов проекта
        this.battleMap = new BattleMap(this);           // создание игровой карты
        this.tank = new Tank(this);                     //создание танка
        this.projectesController = new ProjectesController(this);
        this.enemyController = new EnemyController(this);
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public ProjectesController getProjectesController() {
        return projectesController;
    }

    public EnemyController getEnemyController() {
        return enemyController;
    }

    public BattleMap getBattleMap() {
        return battleMap;
    }

    public Tank getTank() {
        return tank;
    }

    // апдейт всех созданных объектов
    public void update(float dt) {
        battleMap.upDate(dt);
        tank.update(dt);
        projectesController.update(dt);
        enemyController.update(dt);
        collisions(dt);
    }
    //разрешение коллизий столкновений объектов
    public void collisions(float dt) {
        int x=0;
        int y=0;
        for (Ball o : enemyController.getFreeList()) {
            o.update(dt);
            if (tank.getPosition().dst(o.getPosition()) < 60) {              // метод dst() возвращает расстояние между векторами
                o.getPosition().x = (float) Math.random() * 1160 + 80;
                o.getPosition().y = (float) Math.random() * 560 + 80;
            }
            if(projectesController.getActiveList().size() !=0){             // если есть активные снаряды то проверяем попадания в цели
                for (Bullet bullet: projectesController.getActiveList()){
                    if (bullet.getShootVector().dst(o.getPosition()) < 40) {
                        o.getPosition().x = (float) Math.random() * 1160 + 80;
                        o.getPosition().y = (float) Math.random() * 560 + 80;
                    }
                }
            }
        }
        for(Vector2 spiceV: battleMap.getSpiceArr()){   // собираем спайс на карте
            if(tank.getPosition().dst(spiceV) < 20){
                x = (int) (spiceV.x-40)/80;
                y = (int) (spiceV.y-40)/80;
                battleMap.getPosSpices()[x][y] = 0;
            }

        }
    }
}
