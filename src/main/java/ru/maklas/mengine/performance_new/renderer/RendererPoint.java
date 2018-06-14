package ru.maklas.mengine.performance_new.renderer;

public class RendererPoint {

    public int engineUpdate;
    public int engineRender;
    public int events;
    public int entities;

    public int getHighest(){
        int max = engineUpdate;

        if (engineRender > max){
            max = engineRender;
        }

        if (events > max){
            max = events;
        }

        return max;
    }

}
