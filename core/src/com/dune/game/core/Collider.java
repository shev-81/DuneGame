package com.dune.game.core;

import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.units.AbstractUnit;

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
                        unit.takeDamage(bullet.getDamage());
                        bullet.setActive(false);
                    }
                }
            }
        }

        // сталкивается ли танк с другими танками (если танков больше чем 1 в игре)
        for (int i = 0; i < units.size() - 1; i++) {
            AbstractUnit u1 = units.get(i);
            for (int j = i + 1; j < units.size(); j++) {
                AbstractUnit u2 = units.get(j);
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
}
