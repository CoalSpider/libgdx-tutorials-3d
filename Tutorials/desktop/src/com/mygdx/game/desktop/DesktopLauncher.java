package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.Lesson1;
import com.mygdx.game.Lesson2;
import com.mygdx.game.Lesson3;
import com.mygdx.game.Lesson4;
import com.mygdx.game.Lesson5;
import com.mygdx.game.Lesson6;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
                config.useGL30 = false;
                config.width = 1000;
                config.height = 1000;
		new LwjglApplication(
                        //new MyGdxGame(),
                        //new Lesson1(),
                        //new Lesson2(),
                        //new Lesson3(),
                        //new Lesson4(),
                        //new Lesson5(),
                        new Lesson6(),
                        config);
	}
}
// TODO: initilization of static stuff in lessons 1-5 you dont need to make a mass or local inertia should mirror lesson 6 changess
// TODO: lessons 5: bad colliison detection see lesson 6
// TODO: add motion state on moving player for lessons 5-rest
// TODO: change gravity to 10 and reduce jump velocity for lessons 5-6

// lesson 1 start with ground
// lesson 2 player movement
// lesson 3 jumping
// lesson 4 camera movement
// lesson 5 basic physics
// lesson 6 detecting collisions w/ specfics
// Lesson 7 todo: moving platform