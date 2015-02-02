package com.charredsoftware.tsa.entity;

import java.util.Random;

import org.newdawn.slick.opengl.Texture;

/**
 * A Henchman mob!
 * This mob is called upon by a Worker, and follows the player around.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 12th, 2014
 */

public class Henchman extends Mob{

	public float facing = 0; //In degrees.
	public static Texture texture = null;
	private Random r = new Random();
	
}
