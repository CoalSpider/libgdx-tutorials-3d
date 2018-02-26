/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.ai;

import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Ben Norman
 *
 * Direction enum holds a xy pair to denote direction  <pre>
 *     +Y
 * -X <[]> +X
 *     -Y
 * </pre>
 */
public enum Direction {
    NORTH(new Vector2(0, 1)), SOUTH(new Vector2(0, -1)), WEST(new Vector2(-1, 0)), EAST(new Vector2(1, 0));
    private final Vector2 vect;

    Direction(Vector2 dir) {
        vect = dir.cpy();
    }

    boolean isOpposite(Direction dir) {
        switch (this) {
            case NORTH:
                return dir.equals(SOUTH);
            case SOUTH:
                return dir.equals(NORTH);
            case WEST:
                return dir.equals(EAST);
            case EAST:
                return dir.equals(WEST);
        }
        return false;
    }
    
    public Vector2 getVect(){
        return vect.cpy();
    }
}
