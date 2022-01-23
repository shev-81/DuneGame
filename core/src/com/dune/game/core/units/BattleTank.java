package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.*;

public class BattleTank extends AbstractUnit {


    public BattleTank(GameController gameController) {
        super(gameController);
        this.weaponsTextures = Assets.getInstance().getAtlas().findRegion("turret");
        this.tankTextures = Assets.getInstance().getAtlas().findRegion("tankcore").split(64, 64)[0];
        this.minDstToActiveTarget = 240.0f;
        this.weapon = new Weapon( 1.5f, 1);
        this.speed = 140.0f;
        this.hpMax = 100;
        this.container = 0; // текущая емкость контейнера
        this.unitType = UnitType.BATTLE_TANK;
    }

    @Override
    public void setup(Owner owner, float x, float y) {
        this.ownerType = owner;
        this.position.set(x, y);
        this.destination = new Vector2(position);
        this.hp = hpMax;
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
            int power = weapon.use(dt);
            if(power > -1){
                gameController.getProjectesController().setup(this);
            }
        }
        if (target == null) {
            float angleTo = tmpV.set(destination).sub(position).nor().angle(); // tmpV.set(destination).sub(position).angle();
            weapon.setAngle(rotateTo(weapon.getAngle(), angleTo, 180.0f, dt));
        }
    }

    @Override
    public void commandAttack(Targetable target){
        if(target.getType() == TargetType.UNIT && ((AbstractUnit)target).getOwnerType() != this.ownerType ){
            this.target = target;
        }else {
            commandMoveTo(target.getPosition());
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