package com.dune.game.core;

import com.badlogic.gdx.math.MathUtils;

public class GameController {

    private BattleMap battleMap;
    private EnemyController enemyController;
    private ProjectesController projectesController;
    private TankController tankController;


    // инициализация игровой логики
    public GameController() {
        Assets.getInstance().loadAssets();                          //загрузка ресурсов проекта
        this.battleMap = new BattleMap(this);           // создание игровой карты
        this.projectesController = new ProjectesController(this);
        this.enemyController = new EnemyController(this);
        this.tankController = new TankController(this);
        this.tankController.setup(200,200, Tank.Owner.PLAYER);       //создание танка при помощи setup
        this.tankController.setup(600,600, Tank.Owner.PLAYER);
        this.tankController.setup(500,500, Tank.Owner.AI);
    }

    public ProjectesController getProjectesController() {
        return projectesController;
    }

    public TankController getTankController() {
        return tankController;
    }

    public EnemyController getEnemyController() {
        return enemyController;
    }

    public BattleMap getBattleMap() {
        return battleMap;
    }

    // апдейт всех созданных объектов
    public void update(float dt) {
        battleMap.upDate(dt);
        tankController.update(dt);
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
            for(Tank tank: tankController.getActiveList())
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
        // сталкивается ли танк с другими танками (если танков больше чем 1 в игре)
        if (tankController.getActiveList().size() > 1) {
            int countTank = tankController.getActiveList().size();
            for (int i = 0, j = i+1; i < countTank; i++, j++) {
                if(j == countTank){
                    j=0;
                }
                Tank tank = tankController.getActiveList().get(i);
                Tank tankNext = tankController.getActiveList().get(j);
                if (tank.position.dst(tankNext.position) < 50) {
                    tank.getDestination().set(tank.position.x + 20, tank.position.y + MathUtils.random(20.0f));
                    tankNext.getDestination().set(tankNext.position.x - 20, tankNext.position.y - MathUtils.random(20.0f));
                }
            }
        }
    }
}
