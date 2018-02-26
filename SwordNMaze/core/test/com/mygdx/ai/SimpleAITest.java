/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.ai;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.util2.TreeNode;
import com.mygdx.maze.MazeCell;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Ben Norman
 */
public class SimpleAITest {

//    public SimpleAITest() {
//    }
//    @BeforeClass
//    public static void setUpClass() {
//    }
//    
//    @AfterClass
//    public static void tearDownClass() {
//    }
//    
    TreeNode<MazeCell> root;
    TreeNode<MazeCell> c1;
    TreeNode<MazeCell> c2;
    TreeNode<MazeCell> c3;

    @Before
    public void setUp() {
        root = new TreeNode<MazeCell>(new MazeCell(0, 1));
        // corridor
        c1 = new TreeNode<MazeCell>(new MazeCell(0, 2));
        c2 = new TreeNode<MazeCell>(new MazeCell(0, 3));
        c3 = new TreeNode<MazeCell>(new MazeCell(0, 4));
        root.addChild(c1);
        c1.addChild(c2);
        c2.addChild(c3);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void setTarget() {
        assertNotNull(root);
        assertNotNull(root.getChildren().get(0));
        SimpleAI ai = new SimpleAI();
        ai.setTarget(root);
        ai.setTarget(root.getChildren().get(0));
        assertEquals(root, ai.current);
        assertEquals(root.getChildren().get(0), ai.target);
    }

    @Test
    public void testDirection() {
        SimpleAI ai = new SimpleAI();
        // cells are denoted by row then column or y then x
        MazeCell center = new MazeCell(0, 0);
        MazeCell above = new MazeCell(1, 0);
        MazeCell below = new MazeCell(-1, 0);
        MazeCell right = new MazeCell(0, 1);
        MazeCell left = new MazeCell(0, -1);
        assertEquals(Direction.NORTH, ai.getDirection(center, above));
        assertEquals(Direction.SOUTH, ai.getDirection(center, below));
        assertEquals(Direction.EAST, ai.getDirection(center, right));
        assertEquals(Direction.WEST, ai.getDirection(center, left));
        // do not need to test adjacency becuase there coming from a tree
        // do not need to test null bcause they are always non null from the tree
    }

    @Test
    public void pickNextTargetCooridorTest() {
        SimpleAI ai = new SimpleAI();
        ai.setTarget(root);
        ai.setTarget(c1);
        ai.pickNextTarget();
        assertEquals(c2, ai.target);
        ai.pickNextTarget();
        assertEquals(c3, ai.target);
        ai.pickNextTarget();
        assertEquals(c2, ai.target);
        ai.pickNextTarget();
        assertEquals(c1, ai.target);
        ai.pickNextTarget();
        assertEquals(root, ai.target);
        ai.pickNextTarget();
        assertEquals(c1, ai.target);
        ai.pickNextTarget();
        assertEquals(c2, ai.target);
        ai.pickNextTarget();
        assertEquals(c3, ai.target);
    }

    @Test
    public void moveTest() {
        SimpleAI ai = new SimpleAI(root, c1);
        Vector2 start = root.getData().asVector();
        System.out.println(ai.position);
        System.out.println(ai.direction);
        ai.move();
        System.out.println(ai.position + "," + start);
        assertFalse(ai.position.epsilonEquals(start, 1e-5f));
        Vector2 oldPos = ai.position.cpy();
        ai.move();
        assertFalse(ai.position.epsilonEquals(oldPos, 1e-5f));
    }

    @Test
    public void atTargetTest() {
        SimpleAI ai = new SimpleAI(root, c1);
        int iterations = 0;
        boolean passed = false;
        while (++iterations < 1000) {
            if (ai.arrivedAtTarget()) {
                passed = true;
            } else {
                ai.move();
            }
        }
        if (passed) {

        } else {
            fail("Max iters exceeded didnt reach target");
        }
    }
}
