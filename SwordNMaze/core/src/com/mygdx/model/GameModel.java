/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.model;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;

/**
 *
 * @author Ben Norman
 */
public class GameModel extends ModelInstance implements Disposable{

    private final Vector3 renderCenter = new Vector3();
    private final Vector3 renderDimensions = new Vector3();
    public final float renderRadius;
    // only need 1
    private static final BoundingBox renderBounds = new BoundingBox();

    public GameModel(Model model) {
        super(model);
        model.calculateBoundingBox(renderBounds);
        renderBounds.getCenter(renderCenter);
        renderBounds.getDimensions(renderDimensions);
        renderRadius = renderDimensions.len() / 2f;
    }

    // temp variable for isVisibleByCamera method
    private static Vector3 position = new Vector3();
    /**
     * @param cam the camera currently being used to render the GameModel
     * @return if this GameModel is visible by the given camera*
     */
    public boolean isVisibleByCamera(Camera cam) {
        // get the position
        this.transform.getTranslation(position);
        position.add(renderCenter);
        // if we are not rotated check the bounding box
        if (this.transform.hasRotationOrScaling() == false) {
            return cam.frustum.boundsInFrustum(position, renderDimensions);
        }
        // if we are rotated we check a sphere of all possible orientations
        return cam.frustum.sphereInFrustum(position, renderRadius);
    }

    @Override
    public void dispose() {
       model.dispose();
    }

}
