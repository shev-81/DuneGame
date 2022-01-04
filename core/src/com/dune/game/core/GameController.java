package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;

public class GameController {

    private Tank tank;
    private BattleMap battleMap;
    private ArrayList<Obstacles> obstacles;

    // инициализация игровой логики
    public GameController() {
        obstacles = new ArrayList<>();
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("game.pack"));
        this.battleMap = new BattleMap(atlas);
        for (int i = 0; i < 5; i++) {
            obstacles.add(new Ball((float) Math.random() * 1160 + 80, (float) Math.random() * 560 + 80, atlas));
        }
        this.tank = new Tank(200, 200, atlas);
    }

    public BattleMap getBattleMap() {
        return battleMap;
    }

    public Tank getTank() {
        return tank;
    }

    public ArrayList<Obstacles> getObstacles() {
        return obstacles;
    }

    public void update(float dt){
        tank.update(dt);
        for (Obstacles o : obstacles)
            o.update(dt);
        collisions();
    }

    public void collisions(){
        for (Obstacles o : obstacles){
              if(tank.getPosition().dst(o.getPosition())<60)  {              // метод dst() возвращает расстояние между векторами
                o.getPosition().x = (float) Math.random() * 1160 + 80;
                o.getPosition().y = (float) Math.random() * 560 + 80;
            }
        }
    }
}
