package com.dune.game.core.interfaces;

public interface GameMap {
    int getSizeX();
    int getSizeY();
    boolean isCellPassable(int cellX, int cellY, boolean isFlyable);
    int getCellCost(int cellX, int cellY);
}
