package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.*;

public class Harvester extends AbstractUnit {

    public Harvester(GameController gameController) {
        super(gameController);
        this.weaponsTextures = Assets.getInstance().getAtlas().findRegion("harvester");
        this.tankTextures = Assets.getInstance().getAtlas().findRegion("tankcore").split(64, 64)[0];
        this.minDstToActiveTarget = 5.0f;
        this.weapon = new Weapon( 3f, 1);
        this.speed = 120.0f;
        this.hpMax = 500;
        this.container = 0; // емкость ресурсов
        this.unitType = UnitType.HARVESTER;
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
        // если активен сбор ресурса отрисовка прогресса тика сбора
        if(container <= CONTAINER_CAPACITY){
            if (weapon.getWeaponPercentage() > 0.0f) {
                batch.setColor(0.2f, 0.0f, 0.0f, 1.0f);
                batch.draw(progressBarTextures, position.x - 32, position.y + 43, 64, 8);
                batch.setColor(1.0f, 1.0f, 0.0f, 1.0f);
                batch.draw(progressBarTextures, position.x - 30, position.y + 45, 60 * weapon.getWeaponPercentage(), 4);
                batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        // отрисовка заполнения контейнера харвестера ресурсом
        for (int i = 1; i <= container / CONTAINER_POINT; i++) {
            batch.setColor(0.2f, 0.0f, 0.0f, 1.0f);
            batch.draw(progressBarTextures, position.x + 34, position.y - 30 + (10 * (i - 1)), 10, 10);
            batch.setColor(0.0f, 1.0f, 1.0f, 1.0f);
            batch.draw(progressBarTextures, position.x + 36, position.y - 28 + (10 * (i - 1)), 6, 6);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    public float getHpPercent() {    // процент от текущего хп
        return hp / hpMax;
    }

    // перезарядка оружия танка
    public void updateWeapon(float dt) {
        if (target == null) {
            float angleTo = tmpV.set(destination).sub(position).angle(); // tmpV.set(destination).sub(position).angle();
            weapon.setAngle(rotateTo(weapon.getAngle(), angleTo, 180.0f, dt));
        }

        if (gameController.getBattleMap().getResourceCount(position) > 0) {
            int result = weapon.use(dt);
            if (result > -1 && container < CONTAINER_CAPACITY) {
                container += gameController.getBattleMap().harvestResource(position, result); // наполняем контейнер
            }
        } else {
            weapon.reset();
        }
    }

    @Override
    public boolean takeDamage(int damage) {
        if(!isActive()){
            return false;
        }
        this.hp -= damage*(container+1);    // урон по наполненому харвестеру увеличивается в зависимости от наполнения его емкости
        return hp<0;
    }
    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getDestination() {
        return destination;
    }

    @Override
    public void commandAttack(Targetable target) {
        commandMoveTo(target.getPosition());
    }

    public boolean isFullContainer(){
        return container >= CONTAINER_CAPACITY;
    }

    public float getHp() {
        return hp;
    }

}