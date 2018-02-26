package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
                config.width = 1920-500;
                config.height = (int) (config.width*0.5625f);
                config.useGL30 = false;
		new LwjglApplication(new MyGdxGame(), config);
	//	new LwjglApplication(new BulletTest(), config);
	}
}
