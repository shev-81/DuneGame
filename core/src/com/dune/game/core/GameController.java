package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.screens.ScreenManager;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    private BattleMap battleMap;
    private Vector2 tmpV;
    private ProjectesController projectesController;
    private UnitsController unitsController;
    private List<AbstractUnit> selectedUnits;
    private PlayerLogic playerLogic;
    private AiLogic aiLogic;
    private Collider collider;
    private Vector2 startSelection;
    private Vector2 endSelection;
    private Vector2 mouse;

    // инициализация игровой логики
    public GameController() {

        this.battleMap = new BattleMap(this);                       // создание игровой карты
        this.projectesController = new ProjectesController(this);   // контроллер снарядов
        this.unitsController = new UnitsController(this);           // контроллер юнитов
        this.playerLogic = new PlayerLogic(this);                   // контроллер действий пользователя
        this.aiLogic = new AiLogic(this);                           // контроллер действий CPU
        this.collider = new Collider(this);                         // коллайдер (контроллер коллизий)
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
                        for (AbstractUnit unit: unitsController.getPlayerUnits()) {
                            if (unit.getPosition().x > startSelection.x && unit.getPosition().x < tmpV.x
                                && unit.getPosition().y > tmpV.y && unit.getPosition().y < startSelection.y) {
                                selectedUnits.add(unit);
                            }
                        }
                    } else {
                        for (AbstractUnit unit : unitsController.getUnits()) {
                            if (Math.abs(tmpV.dst(unit.getPosition())) < 30) {
                                selectedUnits.add(unit);
                            } else {
                                selectedUnits.remove(unit);
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
        unitsController.update(dt);
        playerLogic.update(dt);
        aiLogic.update(dt);
        projectesController.update(dt);
        battleMap.update(dt);
        collider.checkCollisions();
    }

    public Vector2 getMouse() {
        return mouse;
    }

    public Vector2 getStartSelection() {
        return startSelection;
    }

    public List<AbstractUnit> getSelectedUnits() {
        return selectedUnits;
    }

    public boolean isUnitSelected(AbstractUnit abstractUnit) {
        return selectedUnits.contains(abstractUnit);
    }

    public ProjectesController getProjectesController() {
        return projectesController;
    }

    public UnitsController getUnitsController() {
        return unitsController;
    }

    public BattleMap getBattleMap() {
        return battleMap;
    }
}
