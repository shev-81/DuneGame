package com.dune.game.core;

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

    public Vector2 getPosition() {
        return position;
    }
}
