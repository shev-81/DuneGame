package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
    protected GameController gameController;
    protected Vector2 position;
    protected Vector2 tmpV;

    public GameObject(GameController gameController) {
        this.gameController = gameController;
        this.position = new Vector2(0,0);
        this.tmpV = new Vector2(0,0);
    }

    public int getCellX(){
        return (int) (position.x / BattleMap.CELL_SIZE);
    }
    public int getCellY(){
        return (int) (position.y / BattleMap.CELL_SIZE);
    }

    public Vector2 getPosition() {
        return position;
    }
}
