package ru.maklas.mengine.performance_new.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.TestEngine;
import ru.maklas.mengine.performance_new.results.EventData;
import ru.maklas.mengine.performance_new.results.NamedData;
import ru.maklas.mengine.performance_new.results.PerformanceResult;

public class PerformanceRenderer {


    private final TestEngine engine;
    private final int updateFrame;
    private final int points;
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera cam;
    private final Array<RendererPoint> pointQueue;

    public Color borderColor = Color.BLACK;
    public Color updateColor = Color.GREEN;
    public Color renderColor = Color.BLUE;
    public Color eventColor  = Color.RED;

    private int currentFrame = 0;

    public PerformanceRenderer(TestEngine testEngine, int updateFrame, int points, float x, float y, float width, float height){
        this.engine = testEngine;
        this.updateFrame = updateFrame;
        this.points = points;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        shapeRenderer = new ShapeRenderer();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        pointQueue = new Array<RendererPoint>(true, points);
    }

    public void draw(){
        updatePoints();
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        drawBorders();
        if (pointQueue.size > 1){
            renderGraphics();
        }
        shapeRenderer.end();
    }

    private void updatePoints() {
        currentFrame++;
        if (currentFrame == updateFrame){
            addNewPoint();
            currentFrame = 0;
        }
    }

    private void addNewPoint() {
        PerformanceResult result = engine.captureResults();
        RendererPoint point = new RendererPoint();
        point.engineUpdate = getMicro(result.engineUpdate);
        point.engineRender = getMicro(result.engineRender);

        int microEvents = 0;
        for (EventData event : result.events) {
            microEvents += getMicro(event.totalTime, event.calls);
        }
        point.events = microEvents;

        pointQueue.add(point);
        if (pointQueue.size > points){
            pointQueue.removeIndex(0);
        }
    }

    private void drawBorders() {
        shapeRenderer.setColor(borderColor);
        shapeRenderer.rect(x, y, width, height);
    }

    private void renderGraphics() {
        int max = 1000;
        Array<RendererPoint> queue = this.pointQueue;
        for (RendererPoint point : queue) {
            int top = point.getHighest();
            if (top > max) max = top;
        }

        int size = queue.size;
        int topValue = (int) (max * 1.1f);


        shapeRenderer.setColor(updateColor);

        for (int i = 0; i < size - 1; i++) {
            RendererPoint prev = queue.get(i);
            RendererPoint next = queue.get(i + 1);
            float x1 = getX(i, size);
            float x2 = getX(i + 1, size);
            float y1 = getY(prev.engineUpdate, topValue);
            float y2 = getY(next.engineUpdate, topValue);
            shapeRenderer.line(x1, y1, x2, y2);
        }

        shapeRenderer.setColor(renderColor);

        for (int i = 0; i < size - 1; i++) {
            RendererPoint prev = queue.get(i);
            RendererPoint next = queue.get(i + 1);
            float x1 = getX(i, size);
            float x2 = getX(i + 1, size);
            float y1 = getY(prev.engineRender, topValue);
            float y2 = getY(next.engineRender, topValue);
            shapeRenderer.line(x1, y1, x2, y2);
        }

        shapeRenderer.setColor(eventColor);

        for (int i = 0; i < size - 1; i++) {
            RendererPoint prev = queue.get(i);
            RendererPoint next = queue.get(i + 1);
            float x1 = getX(i, size);
            float x2 = getX(i + 1, size);
            float y1 = getY(prev.events, topValue);
            float y2 = getY(next.events, topValue);
            shapeRenderer.line(x1, y1, x2, y2);
        }
    }


    private float getX(int pointId, int pointsSize){
        return x + (width * (1 - ((pointsSize - pointId) / (float) pointsSize)));
    }

    private float getY(float value, float topValue){
        return y + ((height * value) / topValue);
    }

    private int getMicro(NamedData data){
        return getMicro(data.totalTime, data.calls);
    }
    private int getMicro(long nano, int calls){
        return (int) ((nano /1000) / calls);
    }

}
