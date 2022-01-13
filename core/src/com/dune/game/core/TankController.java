package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class TankController extends ObjectPool<Tank>{
    private Vector2 tmpV;
    private Tank aiTank;

    public TankController(GameController gameController) {
        super(gameController);
        this.tmpV = new Vector2();
    }


    public void render (SpriteBatch batch){
        for(int i = 0; i<activeList.size(); i++){
            activeList.get(i).render(batch);
        }
    }

    public void update(float dt){
        for(int i = 0; i<activeList.size(); i++){
            activeList.get(i).update(dt);
        }
        playerUpdate(dt);
        aiUpdate(dt);
        checkPool();
    }

    public void playerUpdate(float dt) {
        // реакция на правый клик мыши
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){
            for(Tank tank: gameController.getSelectedUnits()) {
                if(tank.getOwnerType() == Tank.Owner.PLAYER && gameController.getSelectedUnits().contains(tank)){
                    tmpV.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
                    if (tank.getWeapon().getType() == Weapon.Type.HARVEST) {
                        tank.commandMoveTo(tmpV);
                    }
                    if (tank.getWeapon().getType() == Weapon.Type.GROUND) {
                        aiTank = gameController.getTankController().getNearestAiTank(tmpV);
                        if(aiTank == null){
                            tank.setTarget(null);
                            tank.commandMoveTo(tmpV);
                        }else{
                            tank.commandAttack(aiTank);
                        }
                    }
                }
            }
        }
    }

    public void aiUpdate(float dt) {
//        if (ownerType == Tank.Owner.AI) {
//
//        }
    }

    public void setup(float x, float y, Tank.Owner ownerType){
        Tank tank = getActiveElement();
        tank.setup(ownerType, x, y);
    }

    public Tank getNearestAiTank(Vector2 point){
        for(Tank tank: activeList){
            if(tank.getOwnerType() == Tank.Owner.AI && tank.getPosition().dst(point) <30){
                return tank;
            }
        }
        return null;
    }

    @Override
    protected Tank newObject() {
        return new Tank(gameController);
    }
}
