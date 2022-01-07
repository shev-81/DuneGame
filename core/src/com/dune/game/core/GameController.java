package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class GameController {

    private Tank tank;
    private BattleMap battleMap;
    private EnemyController enemyController;
    private ProjectesController projectesController;
    private TextureAtlas atlas;

    // инициализация игровой логики
    public GameController() {
        Assets.getInstance().loadAssets(); //загрузка ресурсов проекта
        this.battleMap = new BattleMap(this);          // создание игровой карты
        this.tank = new Tank(this);     //создание танка
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
        tank.update(dt);
        projectesController.update(dt);
        enemyController.update(dt);
        collisions(dt);
    }
    //разрешение коллизий столкновений объектов
    public void collisions(float dt) {
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
    }
}
