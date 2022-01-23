package com.dune.game.core;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.Building;
import com.dune.game.core.units.Harvester;
import com.dune.game.core.units.UnitType;

import java.util.List;

public class Collider {

    private GameController gameController;
    private Vector2 tmpV;

    public Collider(GameController gameController) {
        this.gameController = gameController;
        this.tmpV = new Vector2();
    }

    //разрешение коллизий столкновений объектов
    public void checkCollisions() {
        List<AbstractUnit> units = gameController.getUnitsController().getUnits();
        // проверяем попадания активных снарядов по танкам
        for (Bullet bullet : gameController.getProjectesController().getActiveList()) {
            for (AbstractUnit unit : gameController.getUnitsController().getUnits()) {
                if(bullet.getOwnerType() != unit.getOwnerType()){
                    if (bullet.getPosition().dst(unit.getPosition()) < 40) {
                        for (int k = 0; k < 25; k++) {
                            tmpV.set(bullet.getVelocity()).nor().scl(120.0f).add(MathUtils.random(-40, 40), MathUtils.random(-40, 40));
                            gameController.getParticleController().setup(
                                    bullet.getPosition().x, bullet.getPosition().y, tmpV.x, tmpV.y, 0.4f, 1.0f, 0.2f,
                                    1, 0, 0, 1, 1, 1, 0, 0.6f);
                        }
                        unit.takeDamage(bullet.getDamage());
                        bullet.setActive(false);
                    }
                }
                if(unit.getHp()<unit.getHpMax()/2) {  // при хп ниже половины танк загорается
                    for (int i = 0; i < 5; i++) {
                        gameController.getParticleController().setup(
                            unit.getPosition().x, unit.getPosition().y, MathUtils.random(-40.0f, 40.0f), MathUtils.random(-40.0f, 40.0f), 0.3f, 0.3f, 1.4f,
                            1, 1, 0, 1f, 0.1f, 0, 0, 0.8f);
                    }
                }
            }
        }

        // сталкивается ли танк с другими танками (если танков больше чем 1 в игре)
        for (int i = 0; i < units.size() - 1; i++) {
            AbstractUnit u1 = units.get(i);
            for (int j = i + 1; j < units.size(); j++) {
                AbstractUnit u2 = units.get(j);
                if (u1.getUnitType() != UnitType.BUILDING && u2.getUnitType() != UnitType.BUILDING) {
                    float dst = u1.getPosition().dst(u2.getPosition());
                    if (dst < 30 + 30) {
                        float colLengthD2 = (60 - dst) / 2;
                        tmpV.set(u2.getPosition()).sub(u1.getPosition()).nor().scl(colLengthD2);
                        u2.moveBy(tmpV);
                        tmpV.scl(-1);
                        u1.moveBy(tmpV);
                    }
                }
            }
        }

        //проверяем заехал ли харвестер на свою базу
        for (int i = 0; i < gameController.getUnitsController().getUnits().size(); i++) {
            AbstractUnit u1 = gameController.getUnitsController().getUnits().get(i);
            if (u1.getUnitType() == UnitType.BUILDING) {
                for (int j = 0; j < gameController.getUnitsController().getUnits().size(); j++) {
                    AbstractUnit u2 = gameController.getUnitsController().getUnits().get(j);
                    if (u1.position.dst(u2.getPosition()) < 40 && u2.getUnitType() == UnitType.HARVESTER && u2.getContainer() != 0) {
                        if(u2.getOwnerType() == Owner.PLAYER){
                            gameController.getPlayerLogic().setMoney(u2.getContainer());
                        }else{
                            gameController.getAiLogic().setMoney(u2.getContainer());
                        }
                        u2.setContainer(0);
                    }
                }
            }
        }
    }
}
