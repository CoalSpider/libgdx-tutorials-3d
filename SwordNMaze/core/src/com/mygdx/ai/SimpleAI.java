/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.ai;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.util2.TreeNode;
import com.mygdx.maze.MazeCell;
import com.mygdx.util2.Settings;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Ben Norman
 *
 * This AI is used to move around the maze, it moves locked to the middle of
 * grid cells randomly moving when reaching branches
 */
public class SimpleAI {

    TreeNode<MazeCell> current;
    TreeNode<MazeCell> target;
    // keep track what direction along axis we are moving
    Direction direction;

    Vector2 position = null;

    private static final float SPEED = 0.1f;
    private static final Random RANDOM = new Random(1993);
    private static final float NEARNESS = 1e-2f;

    SimpleAI() {
        // default
    }

    public SimpleAI(TreeNode<MazeCell> curr, TreeNode<MazeCell> tarr) {
        setTarget(curr);
        setTarget(tarr);
    }

    public void update() {
        if (arrivedAtTarget()) {
            pickNextTarget();
        }
        move();
    }

    // TODO: constructor with start node
    // not to sure on maze scale
    boolean arrivedAtTarget() {
        Vector2 targetCell = this.target.getData().asVector();
        // TODO: change to constant
        return position.epsilonEquals(targetCell, NEARNESS);
    }

    void pickNextTarget() {
        if (this.target.isLeaf()) {
            setTarget(this.target.getParent());
        } else if (this.target.isRoot()) {
            List<TreeNode<MazeCell>> children = this.target.getChildren();
            setTarget(children.get(RANDOM.nextInt(children.size())));
        } else {
            // add all connecting child cells
            List<TreeNode<MazeCell>> validCells = new ArrayList<TreeNode<MazeCell>>();
            for (TreeNode<MazeCell> child : this.target.getChildren()) {
                if (child.equals(current) == false) {
                    validCells.add(child);
                }
            }
            // if the parent isnt the previous target cell add it
            if (this.target.getParent().equals(current) == false) {
                validCells.add(this.target.getParent());
            }

            // choose a random cell from valid cells
            setTarget(validCells.get(RANDOM.nextInt(validCells.size())));
        }
    }
    
    void move() {
        Vector2 moveAmount = this.direction.getVect().scl(SPEED);
        float lengthMovement = moveAmount.len2();
        float distToTarget = getDistToTargetSqrd();
        // set to target if trying to pass target
        if(distToTarget < lengthMovement){
            this.position.set(this.target.getData().asVector());
        } else {
            this.position.add(moveAmount);
        }
    }
    
    float getDistToTargetSqrd(){
        Vector2 target = this.target.getData().asVector();
        return this.position.cpy().sub(target).len2();
    }

    void setTarget(TreeNode<MazeCell> target) {
        this.current = this.target;
        this.target = target;
        if (this.current != null && this.target != null) {
            direction = getDirection(this.current.getData(), this.target.getData());
            if (this.position == null) {
                this.position = this.current.getData().asVector().cpy();
            }
        }
    }

    /**
     * cells are assumed to be connecting
     */
    Direction getDirection(MazeCell current, MazeCell target) {
        if (current.getColumn() == target.getColumn()) {
            // moving up one row
            if (target.getRow() > current.getRow()) {
                return Direction.NORTH;
            } else { // moving down one row
                return Direction.SOUTH;
            }
        } else {
            // moving right one column
            if (target.getColumn() > current.getColumn()) {
                return Direction.EAST;
            } else { // moving left one column
                return Direction.WEST;
            }
        }
    }

    Direction getDirection(TreeNode<MazeCell> current, TreeNode<MazeCell> target) {
        return getDirection(current.getData(), target.getData());
    }
    
    public Vector2 getPosition(){
        return this.position;
    }
}
