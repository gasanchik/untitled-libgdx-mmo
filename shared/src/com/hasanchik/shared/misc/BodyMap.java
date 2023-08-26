package com.hasanchik.shared.misc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static com.hasanchik.shared.misc.MyMath.BIG_NUMBER;
import static com.hasanchik.shared.misc.MyMath.SMALL_NUMBER;

public class BodyMap extends ConcurrentHashMap<Vector2, List<Body>> {
    private static final Logger logger = LogManager.getLogger(BodyMap.class);

    //Only use methods this class implements

    //Keep this to powers of two, it'll glitch otherwise
    //For example: 0.5 okay, 0.625 okay, 2.125 okay, 0.3 not okay
    private final float tileSize;

    //Max amount of empty lists before deleting them all
    public final static int GARBAGE_COLLECT_EMPTY_LIST_THRESHOLD = 30000;

    public BodyMap(float tileSize) {
        super();

        this.tileSize = 0.125f;
    }

    private void putBodyInArea(Rectangle area, Body body) {
        //A single point if somehow it doesn't put anything in the list
        for (float x = Math.round(area.x/tileSize)*tileSize-tileSize; x < area.x + area.width + tileSize; x += tileSize) {
            for (float y = Math.round(area.y/tileSize)*tileSize-tileSize; y < area.y + area.height + tileSize; y += tileSize) {
                if (body == null) {
                    continue;
                }
                //logger.info("setting bodies " + new Vector2(x, y));

                putBodyIntoBodyList(new Vector2(x,y), body);
            }
        }
    }

    public ArrayList<Body> getBodiesInArea(Rectangle area) {
        ArrayList<Body> bodiesInArea = new ArrayList<>();

        for (float x = Math.round(area.x/tileSize)*tileSize; x < area.x + area.width; x += tileSize) {
            for (float y = Math.round(area.y/tileSize)*tileSize; y < area.y + area.height; y += tileSize) {
                List<Body> bodiesList = super.get(new Vector2(x, y));
                if (bodiesList == null) {
                    continue;
                }

                synchronized (bodiesList) {
                    bodiesList.stream()
                            .filter(body -> !bodiesInArea.contains(body) && body != null)
                            .forEach(bodiesInArea::add);
                }
            }
        }
        return bodiesInArea;
    }

    private Rectangle getPolygonBoundingBox(Vector2[] vertices) {
        float minX = BIG_NUMBER, maxX = SMALL_NUMBER
            , minY = BIG_NUMBER, maxY = SMALL_NUMBER;
        for (Vector2 vector: vertices) {
            float x = vector.x;
            float y = vector.y;

            minX = Math.min(minX, x); maxX = Math.max(maxX, x);
            minY = Math.min(minY, y); maxY = Math.max(maxY, y);
        }
        return new Rectangle(minX, minY, maxX-minX, maxY-minY);
    }

    // Rasterizes all the body fixtures and puts it in the body map
    public void rasterizeBodyIntoMap(Body body) {
        synchronized (body) {
            Array<Fixture> fixtureList = body.getFixtureList();

            fixtureList.forEach(fixture -> {
                Rectangle boundingBox = new Rectangle();
                switch (fixture.getType()) {
                    case Circle -> {
                        CircleShape circleShape = (CircleShape) fixture.getShape();
                        Vector2 position = body.getWorldPoint(circleShape.getPosition());
                        float radius = circleShape.getRadius();
                        boundingBox = new Rectangle(
                                position.x - radius,
                                position.y - radius,
                                radius * 2,
                                radius * 2);
                    }
                    case Polygon -> {
                        PolygonShape polygonShape = (PolygonShape) fixture.getShape();
                        Vector2[] vertices = new Vector2[polygonShape.getVertexCount()];
                        IntStream.range(0, polygonShape.getVertexCount())
                                .forEach(i -> {
                                    Vector2 vector2 = new Vector2();
                                    polygonShape.getVertex(i, vector2);
                                    vertices[i] = body.getTransform().mul(vector2);
                                });
                        boundingBox = getPolygonBoundingBox(vertices);
                    }
                    case Edge -> {
                        EdgeShape edgeShape = (EdgeShape) fixture.getShape();
                        ArrayList<Vector2> vertices = new ArrayList<>(4);
                        Vector2 vertex1 = new Vector2();
                        Vector2 vertex2 = new Vector2();
                        edgeShape.getVertex1(vertex1);
                        edgeShape.getVertex2(vertex2);
                        vertices.add(body.getWorldPoint(vertex1));
                        vertices.add(body.getWorldPoint(vertex2));
                        boundingBox = getPolygonBoundingBox((Vector2[]) vertices.toArray());
                    }
                    case Chain -> {
                        ChainShape chainShape = (ChainShape) fixture.getShape();
                        Vector2[] vertices = new Vector2[chainShape.getVertexCount()];
                        IntStream.range(0, chainShape.getVertexCount())
                                .forEach(i -> {
                                    Vector2 vector2 = new Vector2();
                                    chainShape.getVertex(i, vector2);
                                    vertices[i] = body.getTransform().mul(vector2);
                                });
                        boundingBox = getPolygonBoundingBox(vertices);
                    }
                }
                putBodyInArea(boundingBox, body);
            });
            //});
        }
    }

    public boolean checkIfRefreshBody(Body body, boolean refreshStaticBodies) {
        synchronized (body) {
            return (
                !body.isAwake() &&
                !(refreshStaticBodies && body.getType().equals(BodyDef.BodyType.StaticBody)));
        }
    }

    public synchronized void refresh(World world, boolean refreshStaticBodies) {
        if (this.size() > GARBAGE_COLLECT_EMPTY_LIST_THRESHOLD) {
            this.clear();
        }

        this.forEach((vector2, bodiesList) -> {
            if (bodiesList.size() == 0) {
                return;
            }

            bodiesList.removeIf(body -> {
                if (body.getUserData() == null && body != null) {
                    //Body is removed from world, but it's still somewhere in memory
                    return true;
                }
                return checkIfRefreshBody(body, refreshStaticBodies);
            });
        });

        Array<Body> bodiesArray = new Array<>();
        synchronized (world) {
            world.getBodies(bodiesArray);
            bodiesArray
                    .select(body -> body != null && checkIfRefreshBody(body, refreshStaticBodies))
                    .forEach(this::rasterizeBodyIntoMap);
        }
    }

    public List<Body> putBodyIntoBodyList(Vector2 vector2, Body body) {
        List<Body> bodiesList = this.computeIfAbsent(vector2, key -> Collections.synchronizedList(new ArrayList<>(3)));
        if (!bodiesList.contains(body)) {
            bodiesList.add(body);
        }
        return bodiesList;
    }

    //Debug rendering
    public synchronized void drawSquares(ShapeRenderer shapeRenderer) {
        this.forEach((key, body) -> {
            boolean b = System.currentTimeMillis()%2 == 0;
            if (body.size() > 1) {
                shapeRenderer.rect((float) ((key.x+4.5) * 50), (key.y + 8) * 50, tileSize * 50, tileSize * 50, new Color(1, 0, 0, 0.5f), new Color(1, 0, 0, 0.5f), new Color(1, 0, 0, 0.5f),new Color(1, 0, 0, 0) );

            } else if (body.size() == 1) {
                shapeRenderer.rect((float) ((key.x+4.5) * 50), (key.y + 8) * 50, tileSize * 50, tileSize * 50);
            } else {
                shapeRenderer.rect((float) ((key.x+4.5) * 50), (key.y + 8) * 50, tileSize * 50, tileSize * 50, new Color(0, 1, 0, 0.5f), new Color(0, 1, 0, 0.5f), new Color(0, 1, 0, 0.5f),new Color(1, 0, 0, 0) );
            }
        });
    }
}
