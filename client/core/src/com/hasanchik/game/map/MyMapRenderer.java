package com.hasanchik.game.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.hasanchik.shared.map.MyMap;
import lombok.Getter;
import lombok.Setter;

//Nope, this class possibly couldn't be based off of another one, no nope nein nuh uh
@Getter
public class MyMapRenderer implements Disposable {
    //TODO: implement this class
    @Setter
    private MyMap map;

    private final SpriteBatch spriteBatch;

    protected Rectangle viewBounds;
    private final OrthographicCamera camera;

    private final float box2DUnitScale;

    public MyMapRenderer(OrthographicCamera camera, float box2DUnitScale, SpriteBatch spriteBatch) {
        this.camera = camera;
        this.spriteBatch = spriteBatch;

        this.box2DUnitScale = box2DUnitScale;
    }

    public void setMap() {

    }

    public void setView (OrthographicCamera camera) {
        spriteBatch.setProjectionMatrix(camera.combined);
        float width = camera.viewportWidth * camera.zoom;
        float height = camera.viewportHeight * camera.zoom;
        float w = width * Math.abs(camera.up.y) + height * Math.abs(camera.up.x);
        float h = height * Math.abs(camera.up.y) + width * Math.abs(camera.up.x);
        viewBounds.set(camera.position.x - w / 2, camera.position.y - h / 2, w, h);
    }

    public void render() {
    }

    @Override
    public void dispose() {

    }
}
