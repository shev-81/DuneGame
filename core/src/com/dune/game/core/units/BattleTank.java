package com.dune.game.core.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.*;
import com.dune.game.core.interfaces.Targetable;

public class BattleTank extends AbstractUnit {

    private TextureRegion rocketWeaponsTextures;
    private int lvlUpgrade;

    public BattleTank(GameController gameController) {
        super(gameController);
        this.weaponsTextures = Assets.getInstance().getAtlas().findRegion("turret");
        this.rocketWeaponsTextures = Assets.getInstance().getAtlas().findRegion("roketturret");
        this.tankTextures = Assets.getInstance().getAtlas().findRegion("tankcore").split(64, 64)[0];
        this.minDstToActiveTarget = 240.0f;
        this.weapon = new Weapon( 1.5f, 5);
        this.speed = 140.0f;
        this.hpMax = 100;
        this.container = 0; // текущая емкость контейнера
        this.unitType = UnitType.BATTLE_TANK;
        this.lvlUpgrade = 1;
        this.useWeaponSound = Gdx.audio.newSound(Gdx.files.internal("music/tank_shot.wav"));
    }

    @Override
    public void setup(Owner owner, float x, float y) {
        this.ownerType = owner;
        this.position.set(x, y);
        this.destination = new Vector2(position);
        this.hp = hpMax;
        commandMoveTo(destination,true);
    }

    @Override
    public void renderGui(SpriteBatch batch) {
    // *********************************************************************
    }

    public float getHpPercent() {    // процент от текущего хп
        return hp / 100;
    }

    // перезарядка оружия танка
    public void updateWeapon(float dt) {
        if (target != null) {
            if (!((AbstractUnit) target).isActive()) {
                target = null;
                return;
            }
            float angleTo = tmpV.set(target.getPosition()).sub(position).angle();
            weapon.setAngle(rotateTo(weapon.getAngle(), angleTo, 180.0f, dt));
            if(this.position.dst(target.getPosition())< weapon.getRange()) {
                int power = weapon.use(dt);
                if (power > -1) {
                    if(gameController.getPointOfView().dst(this.position) < 500){         // проигрывание звука выстрела
                        float volume = (1 - gameController.getPointOfView().dst(this.position)/500) / 6;
                        useWeaponSound.play(volume);
                    }
                    gameController.getProjectesController().setup(this, lvlUpgrade);
                }
            }
        }
    }

    @Override
    public void commandAttack(Targetable target){
        if(target.getType() == TargetType.UNIT && ((AbstractUnit)target).getOwnerType() != this.ownerType ){
            this.target = target;
        }else {
            commandMoveTo(target.getPosition(),false);
        }
    }

    public void upgradeWeapon(){
        if(lvlUpgrade<2){       // блокировка грейда оружия выше 2 - го
            lvlUpgrade++;
            this.weaponsTextures = rocketWeaponsTextures;
            this.weapon.setPower(weapon.getPower()*lvlUpgrade);
            this.weapon.setRange(weapon.getRange()*lvlUpgrade);
            this.minDstToActiveTarget *= lvlUpgrade;
            this.useWeaponSound = Gdx.audio.newSound(Gdx.files.internal("music/roket.wav"));
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getDestination() {
        return destination;
    }

    public float getHp() {
        return hp;
    }

}
