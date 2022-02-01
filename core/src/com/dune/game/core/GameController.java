package com.dune.game.core;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dune.game.core.controllers.Collider;
import com.dune.game.core.controllers.ParticleController;
import com.dune.game.core.controllers.ProjectesController;
import com.dune.game.core.controllers.UnitsController;
import com.dune.game.core.gui.GuiMenuBase;
import com.dune.game.core.gui.GuiPlayerInfo;
import com.dune.game.core.logic.AiLogic;
import com.dune.game.core.logic.PlayerLogic;
import com.dune.game.core.units.*;
import com.dune.game.screens.ScreenManager;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    private static final float CAMERA_SPEED = 240.0f;
    private GuiPlayerInfo guiPlayerInfo;
    private GuiMenuBase baseMenu;
    private BattleMap battleMap;
    private boolean paused;
    private float worldTimer;
    private ProjectesController projectesController;
    private UnitsController unitsController;
    private ParticleController particleController;
    private PathFinder pathFinder;
    private List<AbstractUnit> selectedUnits;
    private PlayerLogic playerLogic;
    private AiLogic aiLogic;
    private Collider collider;
    private Vector2 startSelection;
    private Vector2 mouse;
    private Vector2 pointOfView;
    private Vector2 pointHarvest;
    private Vector2 tmpV;
    private Stage stage;
    private Music music;
    private Sound clickMouse;

    // инициализация игровой логики
    public GameController() {
        this.battleMap = new BattleMap(this);                       // создание игровой карты
        this.pathFinder = new PathFinder(battleMap);                            // подсистема построения маршрутов юнитов
        this.projectesController = new ProjectesController(this);   // контроллер снарядов
        this.playerLogic = new PlayerLogic(this);                   // контроллер действий пользователя
        this.aiLogic = new AiLogic(this);                           // контроллер действий CPU
        this.collider = new Collider(this);                         // коллайдер (контроллер коллизий)
        this.unitsController = new UnitsController(this);           // контроллер юнитов
        this.particleController = new ParticleController(this);     // контроллер системы частиц
        this.selectedUnits = new ArrayList<>();
        this.tmpV = new Vector2();
        this.startSelection = new Vector2();
        this.pointHarvest = new Vector2();
        this.mouse = new Vector2();
        this.pointOfView = new Vector2(ScreenManager.HALF_WORLD_WIDTH, ScreenManager.HALF_WORLD_HEIGHT);
        this.music = Gdx.audio.newMusic(Gdx.files.internal("music/menu_music.mp3"));
        this.clickMouse = Gdx.audio.newSound(Gdx.files.internal("music/click_mouse.wav"));
        this.music.setVolume(0.05f);
        this.music.setLooping(true);
        this.music.play();
        createGuiAndPrepareGameInput();
    }

    // апдейт всех созданных объектов
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            paused = !paused;
        }
        if (!paused) {
            worldTimer += dt;
            ScreenManager.getInstance().pointCameraTo(getPointOfView());  // выравнивание координат сцена под обзор экрана мира
            mouse.set(Gdx.input.getX(), Gdx.input.getY());                // привязка координат курсора мыши к окну в игре
            ScreenManager.getInstance().getViewport().unproject(mouse);
            battleMap.update(dt);
            unitsController.update(dt);
            playerLogic.update(dt);
            aiLogic.update(dt);
            projectesController.update(dt);
            collider.checkCollisions();
            particleController.update(dt);
        }
        guiPlayerInfo.update(dt);
        ScreenManager.getInstance().resetCamera();
        stage.act(dt);
        changePOV(dt);
    }

    // метод выделения юнитов на карте
    public InputProcessor prepareSelection() {
        return new InputAdapter() { // отвечает за массовое выделение юнитов на карте
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {  // левая кнопка или тач нажата и удерживается
                if (button == Input.Buttons.LEFT) {
                    startSelection.set(mouse);
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {  // кнопка отпущена
                if(selectedUnits.size() != 0){
                    clickMouse.play();
                }
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
                    if (selectedUnits.size() > 0) {
                        // реакция на левый тач
                        for (int i = 0; i < selectedUnits.size(); i++) {
                            AbstractUnit unit = selectedUnits.get(i);
                            if (unit.getOwnerType() == Owner.PLAYER) {
                                unitProcessing(unit);
                            }
                            if (unit.getOwnerType() == Owner.PLAYER && unit.getUnitType() == UnitType.BUILDING) {
                                baseMenu.setVisible(true);
                            }
                        }
                        selectedUnits.clear();
                    }
                    // обработка простого клика по месту
                    if (Math.abs(tmpV.x - startSelection.x) > 20 & Math.abs(tmpV.y - startSelection.y) > 20) {
                        for (AbstractUnit unit : unitsController.getPlayerUnits()) {
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
                    startSelection.set(-1, -1);
                }
                return true;
            }
        };
    }

    // метод определяет действия для юнита по клику если он есть в листе выбранных
    public void unitProcessing(AbstractUnit unit) {
        if (unit.getUnitType() == UnitType.HARVESTER) {
            if (getBattleMap().getResourceCount(getMouse()) > 0 && !((Harvester) unit).isHarvesterWorking()) {                    // если таргет спайс и больше 0
                pointHarvest.set(getMouse());
                ((Harvester) unit).setTargetPointHarvest(pointHarvest);              // устанавливаем поинт сбора
                ((Harvester) unit).setHarvesterWorking(true);                      // и переводим харвестер в режим работает сбор
            } else {
                ((Harvester) unit).setHarvesterWorking(false);
                unit.commandMoveTo(getMouse(),true);
            }
        }
        if (unit.getUnitType() == UnitType.BATTLE_TANK) {
            AbstractUnit aiUnit = getUnitsController().getNearestAiUnit(getMouse());
            if (aiUnit == null) {
                unit.commandMoveTo(getMouse(), true);
            } else {
                unit.commandAttack(aiUnit);
            }
        }
        if (unit.getUnitType() == UnitType.HEALTANK) {
            AbstractUnit PlUnit = getUnitsController().getNearestPlUnit(getMouse());
            if (PlUnit == null) {
                unit.commandMoveTo(getMouse(), true);
            } else {
                unit.commandAttack(PlUnit);
            }
        }
    }

    // метод создания интерфейса в игре
    public void createGuiAndPrepareGameInput() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), ScreenManager.getInstance().getBatch());
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, prepareSelection()));  // мультиплексер задает какой обработчик будет выполненым первым
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        BitmapFont font14 = Assets.getInstance().getAssetManager().get("fonts/Roboto-Medium24.ttf");
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(
                skin.getDrawable("menu"), null, null, font14);

        // кнопка меню
        final TextButton menuBtn = new TextButton("Menu", textButtonStyle);
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });

        Image backGroundMenuImage = new Image(skin.getRegion("menuFon"));
        backGroundMenuImage.setWidth(1280);

        // группа меню
        Group menuGroup = new Group();
        menuBtn.setPosition(0, 0);
        menuGroup.addActor(menuBtn);
        menuGroup.setPosition(1130, 680);

        // интерфейс игры
        Label.LabelStyle labelStyle = new Label.LabelStyle(font14, Color.WHITE);
        skin.add("simpleLabel", labelStyle);
        guiPlayerInfo = new GuiPlayerInfo(playerLogic, skin);
        baseMenu = new GuiMenuBase(playerLogic, unitsController, textButtonStyle);  // интерфейс меню базы
        guiPlayerInfo.setPosition(0, 700);
        backGroundMenuImage.setPosition(0, 690);
        stage.addActor(backGroundMenuImage);
        stage.addActor(guiPlayerInfo);
        stage.addActor(baseMenu);
        stage.addActor(menuGroup);
        skin.dispose();
    }

    // метод контроля границ игровой зоны
    public void changePOV(float dt) {
        if (Gdx.input.getY() < 50) {
            pointOfView.y += CAMERA_SPEED * dt;
            if (pointOfView.y + ScreenManager.HALF_WORLD_HEIGHT > BattleMap.MAP_HEIGHT_PX) {
                pointOfView.y = BattleMap.MAP_HEIGHT_PX - ScreenManager.HALF_WORLD_HEIGHT;
            }
            ScreenManager.getInstance().pointCameraTo(pointOfView);
        }
        if (Gdx.input.getY() > Gdx.graphics.getHeight() - 50) { //700
            pointOfView.y -= CAMERA_SPEED * dt;
            if (pointOfView.y < ScreenManager.HALF_WORLD_HEIGHT) {
                pointOfView.y = ScreenManager.HALF_WORLD_HEIGHT;
            }
            ScreenManager.getInstance().pointCameraTo(pointOfView);
        }
        if (Gdx.input.getX() < 50) {
            pointOfView.x -= CAMERA_SPEED * dt;
            if (pointOfView.x < ScreenManager.HALF_WORLD_WIDTH) {
                pointOfView.x = ScreenManager.HALF_WORLD_WIDTH;
            }
            ScreenManager.getInstance().pointCameraTo(pointOfView);
        }
        if (Gdx.input.getX() > Gdx.graphics.getWidth() - 50) { //1260
            pointOfView.x += CAMERA_SPEED * dt;
            if (pointOfView.x + ScreenManager.HALF_WORLD_WIDTH > BattleMap.MAP_WIDTH_PX) {
                pointOfView.x = BattleMap.MAP_WIDTH_PX - ScreenManager.HALF_WORLD_WIDTH;
            }
            ScreenManager.getInstance().pointCameraTo(pointOfView);
        }
    }
    public float getWorldTimer() {
        return worldTimer;
    }
    public AiLogic getAiLogic() {
        return aiLogic;
    }
    public PlayerLogic getPlayerLogic() {
        return playerLogic;
    }
    public boolean isPaused() {
        return paused;
    }
    public Vector2 getMouse() {
        return mouse;
    }
    public Stage getStage() {
        return stage;
    }
    public Vector2 getStartSelection() {
        return startSelection;
    }
    public Sound getClickMouse() {
        return clickMouse;
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
    public ParticleController getParticleController() {
        return particleController;
    }
    public Group getBaseMenu() {
        return baseMenu;
    }
    public Vector2 getPointOfView() {
        return pointOfView;
    }
    public Group getCreateTanksMenu() {
        return baseMenu.getCreateTanksMenu();
    }
    public Group getUpgradesMenu() {
        return baseMenu.getUpgradesMenu();
    }
    public PathFinder getPathFinder() {
        return pathFinder;
    }
}

//        for (int i = 0; i < 5; i++) {   //  горящий курсор мыши
//            particleController.setup(mouse.x, mouse.y, MathUtils.random(-15.0f, 15.0f), MathUtils.random(-30.0f, 30.0f), 0.5f,
//                    0.3f, 1.4f, 1, 1, 0, 1, 1, 0, 0, 0.5f);
//        }
