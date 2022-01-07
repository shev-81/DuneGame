package com.dune.game.core;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjectPool  <T extends Poolable> {
    protected List<T> activeList;
    protected List<T> freeList;
    protected GameController gameController;
    protected int initialCapacity;

    public List<T> getActiveList() {
        return activeList;
    }

    public List<T> getFreeList() {
        return freeList;
    }

    protected abstract T newObject();

    public void free(int index){
        freeList.add(activeList.remove(index));
    }

    public ObjectPool(GameController gameController) {
        this.initialCapacity = 20;  //  размер пула объектов
        this.gameController = gameController;
        this.activeList = new ArrayList<>(initialCapacity);
        this.freeList = new ArrayList<>(initialCapacity);
    }

    public T getActiveElement() {
        if(freeList.size()==0){
            freeList.add(newObject());
        }
        T tempObject = freeList.remove(freeList.size()-1);
        activeList.add(tempObject);
        return tempObject;
    }

    public void checkPool(){
        for (int i = activeList.size()-1; i >=0 ; i--) {
            if(!activeList.get(i).isActive()){
                free(i);
            }
        }
    }
}
