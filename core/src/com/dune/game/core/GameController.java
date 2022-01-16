package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.screens.ScreenManager;

import java.util.ArrayList;
import java.util.List;


public class GameController {

    private BattleMap battleMap;
    private Vector2 tmpV;
    private ProjectesController projectesController;
    private TankController tankController;
    private List<Tank> selectedUnits;
    private Vector2 startSelection;
    private Vector2 endSelection;
    private Vector2 mouse;


    // инициализация игровой логики
    public GameController() {
        this.battleMap = new BattleMap(this);           // создание игровой карты
        this.projectesController = new ProjectesController(this);
        this.tankController = new TankController(this);
        for (int i = 0; i < 6; i++) {
            this.tankController.setup(MathUtils.random(50, 1100), MathUtils.random(50, 650), Tank.Owner.PLAYER);       //создание танка при помощи setup
            this.tankController.setup(MathUtils.random(50, 1100), MathUtils.random(50, 650), Tank.Owner.PLAYER);       //создание танка при помощи setup
            this.tankController.setup(MathUtils.random(50, 1100), MathUtils.random(50, 650), Tank.Owner.AI);
        }
        this.selectedUnits = new ArrayList<>();
        this.tmpV = new Vector2();
        this.startSelection = new Vector2();
        this.endSelection = new Vector2();
        this.mouse = new Vector2();

        prepareSelection();
    }

    // метод выделения юнитов на карте
    public void prepareSelection() {
        InputProcessor ip = new InputAdapter() { // отвечает за массовое выделение юнитов на карте
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {  // левая кнопка или тач нажата и удерживается
                if (button == Input.Buttons.LEFT) {
                    startSelection.set(mouse);
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {  // кнопка отпущена
                if (button == Input.Buttons.LEFT) {
                    tmpV.set(mouse);
                    if (tmpV.x < startSelection.x) {    // инвертирование выделения юнитов с другой стороны
                        float t = tmpV.x;
                        tmpV.x = startSelection.x;
                        startSelection.x = t;
                    }
                    if (tmpV.y > startSelection.y) {
                        float t = tmpV.y;
                        tmpV.y = startSelection.y;
                        startSelection.y = t;
                    }
                    selectedUnits.clear();
                    if (Math.abs(tmpV.x - startSelection.x) > 20 & Math.abs(tmpV.y - startSelection.y) > 20) { // обработка простого клика по месту
                        for (Tank tank : tankController.getActiveList()) {
                            if (tank.getOwnerType() == Tank.Owner.PLAYER && tank.getPosition().x > startSelection.x && tank.getPosition().x < tmpV.x
                                    && tank.getPosition().y > tmpV.y && tank.getPosition().y < startSelection.y) {
                                selectedUnits.add(tank);
                            }
                        }
                    } else {
                        for (Tank tank : tankController.getActiveList()) {
                            if (Math.abs(tmpV.dst(tank.getPosition())) < 30) {
                                selectedUnits.add(tank);
                            } else {
                                selectedUnits.remove(tank);
                            }
                        }
                    }
                }
                return true;
            }
        };
        Gdx.input.setInputProcessor(ip);
    }

    // апдейт всех созданных объектов
    public void update(float dt) {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());              // привязка координат курсора мыши к окну в игре
        ScreenManager.getInstance().getViewport().unproject(mouse);
        battleMap.upDate(dt);
        tankController.update(dt);
        projectesController.update(dt);
        collisions(dt);
    }

    //разрешение коллизий столкновений объектов
    public void collisions(float dt) {
        // проверяем попадания активных снарядов по вражеским танкам
        for (Bullet bullet : projectesController.getActiveList()) {
            for (Tank tank : tankController.getActiveList()) {
                if (tank.getOwnerType() == Tank.Owner.AI) {
                    if (bullet.getShootVector().dst(tank.getPosition()) < 40) {
                        tank.setHp(bullet.getDamage());
                        bullet.setActive(false);
                    }
                }
            }
        }
        // сталкивается ли танк с другими танками (если танков больше чем 1 в игре)
        for (int i = 0; i < tankController.activeList.size() - 1; i++) {
            Tank t1 = tankController.getActiveList().get(i);
            for (int j = i + 1; j < tankController.activeList.size(); j++) {
                Tank t2 = tankController.getActiveList().get(j);
                float dst = t1.getPosition().dst(t2.getPosition());
                if (dst < 30 + 30) {
                    float colLengthD2 = (60 - dst) / 2;
                    tmpV.set(t2.getPosition()).sub(t1.getPosition()).nor().scl(colLengthD2);
                    t2.moveBy(tmpV);
                    tmpV.scl(-1);
                    t1.moveBy(tmpV);
                }
            }
        }
    }

    public Vector2 getMouse() {
        return mouse;
    }

    public Vector2 getStartSelection() {
        return startSelection;
    }

    public Vector2 getEndSelection() {
        return endSelection;
    }

    public List<Tank> getSelectedUnits() {
        return selectedUnits;
    }

    public boolean isTankSelection(Tank tank) {
        return selectedUnits.contains(tank);
    }

    public ProjectesController getProjectesController() {
        return projectesController;
    }

    public TankController getTankController() {
        return tankController;
    }

    public BattleMap getBattleMap() {
        return battleMap;
    }
}
