package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.GameState;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.Sound;
import com.charredsoftware.tsa.world.Position;
import com.charredsoftware.tsa.world.World;

/**
 * GameOverMenu class.
 * Menu that is presented when the game is over.
 * @author joeb3219
 * @since January 16, 2015
 */
public class GameOverMenu extends Menu{

	/**
	 * Creates a new GameOverMenu menu
	 */
	public GameOverMenu() {
		super(new Position(0, 0, 0), Display.getWidth(), Display.getHeight());
		float textHeight = Main.getInstance().font.getHeight("sample text");

		Button b = new Button(this, 100, "The game is over! Here's how you did:");
		b.checkable = false;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Score: " + Main.getInstance().player.score);
		b.checkable = false;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Mobs killed: " + Main.getInstance().player.mobsKilled + "/" + Main.getInstance().player.world.getTotalMobs());
		b.checkable = false;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Chests looted:" + Main.getInstance().player.chestsFound + "/" + Main.getInstance().player.world.getTotalChests(Main.getInstance().player.chestsFound));
		b.checkable = false;
		widgets.add(b);
		
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  40, "Play Again");
		b.identifier = "play";
		b.checkable = false;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Submit Bug/Feature Request");
		b.identifier = "bug_feature";
		b.checkable = false;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Quit");
		b.identifier = "quit";
		b.checkable = false;
		widgets.add(b);
	}
	
	/**
	 * Updates the menu.
	 */
	public void update(){
		if(Mouse.isButtonDown(0)){
			for(Widget b : widgets){
				if(!b.mouseInBounds()) continue;
				Sound.BUTTON_CLICKED.playSfxIfNotPlaying();
				if(b.identifier.equalsIgnoreCase("play")){
					Main.getInstance().player.world = new World(0);
					Main.getInstance().player.reset();
					Main.getInstance().gameState = GameState.GAME;
				}
				if(b.identifier.equalsIgnoreCase("quit")){
					Main.getInstance().controller.saveSettings();
					AL.destroy();
					Display.destroy();
					System.exit(0);
				}
			}
		}
	}
	
	/**
	 * Renders the menu
	 */
	public void render(){
		glDisable(GL_TEXTURE_2D);
		this.height = Display.getHeight();
		this.width = Display.getWidth();
		
		glLoadIdentity();
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0f, Display.getWidth(), Display.getHeight(), 0f, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST); 
		glDisable(GL_LIGHTING);
		glDisable(GL_TEXTURE_2D);
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();

		glColor4f(red, green, blue, alpha);
		glBegin(GL_QUADS);
		glVertex2f(0, 0);
		glVertex2f(width, 0);
		glVertex2f(width, height);
		glVertex2f(0, height);
		glEnd();
		
		String title = "Game Over!";
		Main.getInstance().titleFont.drawString((Display.getWidth() - Main.getInstance().titleFont.getWidth(title)) / 2, 64, title);
		
		for(Widget b : widgets) b.render();
		
		glEnable(GL_LIGHTING);
		glEnable(GL_TEXTURE_2D);
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
	}
	
}
