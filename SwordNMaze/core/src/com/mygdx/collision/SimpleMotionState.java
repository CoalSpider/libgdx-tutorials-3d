/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.collision;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

/**
 *
 * @author Ben Norman
 */
public class SimpleMotionState extends btMotionState{
    private final Matrix4 worldTrans = new Matrix4();
    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        this.worldTrans.set(worldTrans);
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.set(this.worldTrans);
    }
    
}
