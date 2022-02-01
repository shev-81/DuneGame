package com.dune.game.core.gui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dune.game.core.controllers.UnitsController;
import com.dune.game.core.logic.PlayerLogic;
import com.dune.game.core.units.BattleTank;
import com.dune.game.core.units.Building;
import com.dune.game.core.units.Harvester;
import com.dune.game.core.units.HealTank;
import static com.dune.game.core.units.Owner.PLAYER;

public class GuiMenuBase extends Group {

    private Group createTanksMenu;
    private Group upgradesMenu;
    private PlayerLogic playerLogic;
    private UnitsController unitsController;
    private Vector2 tmpV;
    private final TextButton crHarButton;
    private final TextButton crHtButton;
    private final TextButton crBtButton;
    private final TextButton upgradeBaseButton;
    private final TextButton upgradeBattleTanks;
    private final TextButton createTanksButton;
    private final TextButton upgredesButton;
    private TextButton.TextButtonStyle textButtonStyle;


    public GuiMenuBase(PlayerLogic playerLogic,UnitsController unitsController, TextButton.TextButtonStyle textButtonStyle) {

        this.playerLogic = playerLogic;
        this.unitsController = unitsController;
        this.textButtonStyle = textButtonStyle;
        this.tmpV = new Vector2();
        // кнопка создание харвестера
        crHarButton = new TextButton("Harvester", textButtonStyle);
        crHarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //  если денег хватает то создаем
                if (getPlayerLogic().getMoney() >= 1000 && getPlayerLogic().getUnitsCount() < getPlayerLogic().getUnitsMaxCount()) {
                    Building basePlayer = getUnitsController().getBasePlayer();  // определяем базу
                    if (basePlayer.isActive()) {
                        Harvester harvester = getUnitsController().createHarvester(basePlayer.getPosition().x, basePlayer.getPosition().y, PLAYER);
                        tmpV.set(basePlayer.getPosition().x + MathUtils.random(150, 200), basePlayer.getPosition().y + MathUtils.random(150, 200));
                        harvester.commandMoveTo(tmpV,true);
                    }
                    getPlayerLogic().minusMoney(1000);
                }
            }
        });
        crHarButton.setVisible(true);

        // кнопка создание хилл танка
        crHtButton = new TextButton("Healer", textButtonStyle);
        crHtButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //  если денег хватает на боевой танк то создаем
                if (getPlayerLogic().getMoney() >= 1000 && getPlayerLogic().getUnitsCount() < getPlayerLogic().getUnitsMaxCount()) {
                    Building basePlayer = getUnitsController().getBasePlayer();  // определяем базу
                    if (basePlayer.isActive()) {
                        HealTank healTank = getUnitsController().createHealTank(basePlayer.getPosition().x, basePlayer.getPosition().y, PLAYER);
                        tmpV.set(basePlayer.getPosition().x + MathUtils.random(150, 200), basePlayer.getPosition().y + MathUtils.random(150, 200));
                        healTank.commandMoveTo(tmpV,true);
                    }
                    getPlayerLogic().minusMoney(1000);
                }
            }
        });
        crHtButton.setVisible(true);

        // кнопка создание боевого танка
        crBtButton = new TextButton("Tank", textButtonStyle);
        crBtButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //  если денег хватает на боевой танк то создаем
                if (getPlayerLogic().getMoney() >= 1000 && getPlayerLogic().getUnitsCount() < getPlayerLogic().getUnitsMaxCount()) {
                    Building basePlayer = getUnitsController().getBasePlayer();  // определяем базу
                    if (basePlayer.isActive()) {
                        BattleTank tank = getUnitsController().createBattleTank(basePlayer.getPosition().x, basePlayer.getPosition().y, PLAYER);
                        tmpV.set(basePlayer.getPosition().x + MathUtils.random(150, 200), basePlayer.getPosition().y + MathUtils.random(150, 200));
                        tank.commandMoveTo(tmpV,true);
                    }
                    getPlayerLogic().minusMoney(1000);
                }
            }
        });
        crBtButton.setVisible(true);

        // кнопка апгрейда базы
        upgradeBaseButton = new TextButton("limit +1", textButtonStyle);
        upgradeBaseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //  если денег хватает на upgrade то делаем
                if (getPlayerLogic().getMoney() >= 1000) {
                    getUnitsController().getBasePlayer().upGradeBuilding();
                    getPlayerLogic().addUnitsMaxCount();
                    getPlayerLogic().minusMoney(1000);
                }
            }
        });
        upgradeBaseButton.setVisible(true);

        // кнопка апгрейда танков
        upgradeBattleTanks = new TextButton("weapon up ", textButtonStyle);
        upgradeBattleTanks.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //  если денег хватает на upgrade то делаем
                if (getPlayerLogic().getMoney() >= 1000) {
                    for (int i = 0; i < getUnitsController().getPlayerBattleTanks().size() ; i++) {
                        getUnitsController().getPlayerBattleTanks().get(i).upgradeWeapon();
                    }
                    getPlayerLogic().minusMoney(1000);
                }
            }
        });
        upgradeBattleTanks.setVisible(true);

        // кнопка вызова под меню создания танков
        createTanksButton = new TextButton("Create", textButtonStyle);
        createTanksButton.addListener(new InputListener() {
            @Override
            public boolean mouseMoved (InputEvent event, float x, float y) {
                createTanksMenu.setVisible(true);
                upgradesMenu.setVisible(false);
                return true;
            }
        });
        createTanksButton.setVisible(true);

        // кнопка вызова под меню upgrades
        upgredesButton = new TextButton("Upgrade", textButtonStyle);
        upgredesButton.addListener(new InputListener() {
            @Override
            public boolean mouseMoved (InputEvent event, float x, float y) {
                upgradesMenu.setVisible(true);
                createTanksMenu.setVisible(false);
                return true;
            }
        });
        upgredesButton.setVisible(true);

        // группа меню базы
        createTanksMenu = new Group();
        upgradesMenu = new Group();

        //определение позиций всех кнопок
        crBtButton.setPosition(150, 0);
        crHarButton.setPosition(150, 60);
        crHtButton.setPosition(150, 120);
        upgradeBaseButton.setPosition(150, 60);
        upgradeBattleTanks.setPosition(150, 120);

        createTanksButton.setPosition(0,0);
        upgredesButton.setPosition(0,60);

        // добавление кнопок в группы меню
        createTanksMenu.addActor(crBtButton);
        createTanksMenu.addActor(crHarButton);
        createTanksMenu.addActor(crHtButton);
        createTanksMenu.setVisible(false);


        upgradesMenu.addActor(upgradeBaseButton);
        upgradesMenu.addActor(upgradeBattleTanks);
        upgradesMenu.setVisible(false);

        // добавление в всех подменю в основную группу меню базы
        this.addActor(createTanksButton);
        this.addActor(upgredesButton);
        this.addActor(createTanksMenu);
        this.addActor(upgradesMenu);
        this.setPosition(getUnitsController().getBasePlayer().getPosition().x + 50, getUnitsController().getBasePlayer().getPosition().y + 50);
        this.setVisible(false);
    }

    public Group getCreateTanksMenu() {
        return createTanksMenu;
    }

    public Group getUpgradesMenu() {
        return upgradesMenu;
    }

    public UnitsController getUnitsController() {
        return unitsController;
    }

    public PlayerLogic getPlayerLogic() {
        return playerLogic;
    }
}
