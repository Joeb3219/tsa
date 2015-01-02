package com.charredsoftware.tsa.entity;

import java.util.Random;

import org.newdawn.slick.opengl.Texture;

/**
 * A Henchman mob!
 * This mob is a worker of the factory.
 * It just randomly paces around a defined area.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 12th, 2014
 */

public class Henchman extends Mob{

	public float facing = 0; //In degrees.
	public static Texture texture = null;
	private Random r = new Random();
	
}
