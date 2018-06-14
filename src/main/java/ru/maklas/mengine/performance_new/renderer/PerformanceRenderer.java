package ru.maklas.mengine.performance_new.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private final Array<RendererPoint> pointQueue;

    public BitmapFont font;
    public Color borderColor = Color.BLACK;
    public Color updateColor = Color.GREEN;
    public Color renderColor = Color.BLUE;
    public Color eventColor  = Color.RED;
    public Color entityColor  = Color.YELLOW;
    public Color netColor  = new Color(0.2f, 0.2f, 0.2f, 0.2f);

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
        batch = new SpriteBatch();
        font = new BitmapFont();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        pointQueue = new Array<RendererPoint>(true, points);
    }

    public void draw(){
        updatePoints();
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        drawNet();
        drawBorders();
        if (pointQueue.size > 1){
            renderGraphics();
        }
        shapeRenderer.end();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        renderGraphicNumbers();
        if (pointQueue.size > 0) {
            renderText();
        }
        batch.end();
    }

    private void drawNet() {
        shapeRenderer.setColor(netColor);
        int top = calculateTopValue();
        int horLines = top / 1000;

        for (int i = 1; i <= horLines; i++) {
            float y = getY(i * 1000, top);
            shapeRenderer.line(x, y, x + width, y);
        }
    }

    private void renderGraphicNumbers() {
        font.setColor(Color.WHITE);
        font.draw(batch, String.valueOf(calculateTopValue()), x - 10, y + height + 15);
    }

    private int calculateTopValue(){
        int max = 250;
        for (RendererPoint point : pointQueue) {
            int top = point.getHighest();
            if (top > max) max = top;
        }
        return (int) (max * 1.1f);
    }

    private void renderText() {
        float x = getTextX();
        float y = getTextY();
        float step = getTextStep();

        RendererPoint current = pointQueue.peek();
        font.setColor(updateColor);
        font.draw(batch, "update: " + current.engineUpdate + " us", x, y);
        y -= step;
        font.setColor(renderColor);
        font.draw(batch, "render: " + current.engineRender + " us", x, y);
        y -= step;
        font.setColor(eventColor);
        font.draw(batch, "event: " + current.events + " us", x, y);
        y -= step;
        font.setColor(entityColor);
        font.draw(batch, "entities: " + current.entities, x, y);

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
        point.entities = result.entities;

        int microEvents = 0;
        for (EventData event : result.events) {
            microEvents += getMicro(event.internalTime, event.calls);
        }
        point.events = (int) (microEvents / (float) result.totalFrames);

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
        Array<RendererPoint> queue = this.pointQueue;

        int size = queue.size;
        int topValue = calculateTopValue();


        shapeRenderer.setColor(updateColor);

        for (int i = 0; i < size - 1; i++) {
            RendererPoint prev = queue.get(i);
            RendererPoint next = queue.get(i + 1);
            float x1 = getX(i);
            float x2 = getX(i + 1);
            float y1 = getY(prev.engineUpdate, topValue);
            float y2 = getY(next.engineUpdate, topValue);
            shapeRenderer.line(x1, y1, x2, y2);
        }

        shapeRenderer.setColor(renderColor);

        for (int i = 0; i < size - 1; i++) {
            RendererPoint prev = queue.get(i);
            RendererPoint next = queue.get(i + 1);
            float x1 = getX(i);
            float x2 = getX(i + 1);
            float y1 = getY(prev.engineRender, topValue);
            float y2 = getY(next.engineRender, topValue);
            shapeRenderer.line(x1, y1, x2, y2);
        }

        shapeRenderer.setColor(eventColor);

        for (int i = 0; i < size - 1; i++) {
            RendererPoint prev = queue.get(i);
            RendererPoint next = queue.get(i + 1);
            float x1 = getX(i);
            float x2 = getX(i + 1);
            float y1 = getY(prev.events, topValue);
            float y2 = getY(next.events, topValue);
            shapeRenderer.line(x1, y1, x2, y2);
        }
    }

    private float getTextX(){
        return x + width;
    }

    private float getTextY(){
        return y + height;
    }

    private float getTextStep(){
        return 20;
    }

    private float getX(int pointId){
        int pointsSize = points - 1;
        return x + (width * (1 - ((pointsSize - pointId) / (float) pointsSize)));
    }

    private float getY(float value, float topValue){
        return y + ((height * value) / topValue);
    }

    private int getMicro(NamedData data){
        return getMicro(data.totalTime, data.calls);
    }
    private int getMicro(long nano, int calls){
        return (int) ((nano/1000) / calls);
    }

}
