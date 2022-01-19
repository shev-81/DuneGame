package com.dune.game.core;

public class Weapon {
private float period;
    private float time;
    private int power;
    private float angle;

    public Weapon( float period, int power) {
        this.period = period;
        this.power = power;
    }

    public int getPower() {
        return power;
    }

    public float getWeaponPercentage(){
        return time / period;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public int use (float dt){
        time += dt;
        if(time > period){
            time = 0.0f;
            return power;
        }
        return -1;
    }

    public void reset() {
        time = 0.0f;
    }
}
