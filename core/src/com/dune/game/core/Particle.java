package com.dune.game.core;

import com.badlogic.gdx.math.Vector2;

public class Particle implements Poolable {
    private Vector2 position;
    private Vector2 velocity;
    private float r1, g1, b1, a1;
    private float r2, g2, b2, a2;

    private boolean active;
    private float time;
    private float timeMax;
    private float size1, size2;

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getR1() {
        return r1;
    }

    public float getG1() {
        return g1;
    }

    public float getB1() {
        return b1;
    }

    public float getA1() {
        return a1;
    }

    public float getR2() {
        return r2;
    }

    public float getG2() {
        return g2;
    }

    public float getB2() {
        return b2;
    }

    public float getA2() {
        return a2;
    }

    public float getTime() {
        return time;
    }

    public float getTimeMax() {
        return timeMax;
    }

    public float getSize1() {
        return size1;
    }

    public float getSize2() {
        return size2;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public Particle() {
        position = new Vector2(0, 0);
        velocity = new Vector2(0, 0);
        size1 = 1.0f;
        size2 = 1.0f;
    }

    public void init(float x, float y, float vx, float vy, float timeMax, float size1, float size2, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
        this.position.x = x;
        this.position.y = y;
        this.velocity.x = vx;
        this.velocity.y = vy;
        this.r1 = r1;
        this.r2 = r2;
        this.g1 = g1;
        this.g2 = g2;
        this.b1 = b1;
        this.b2 = b2;
        this.a1 = a1;
        this.a2 = a2;
        this.time = 0.0f;
        this.timeMax = timeMax;
        this.size1 = size1;
        this.size2 = size2;
        this.active = true;
    }

    public void deactivate() {
        active = false;
    }

    public void update(float dt) {
        time += dt;
        position.mulAdd(velocity, dt);
        if (time > timeMax) {
            deactivate();
        }
    }
}
