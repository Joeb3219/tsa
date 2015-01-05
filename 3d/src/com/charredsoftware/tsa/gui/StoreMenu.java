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
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.IOException;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.GameState;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Position;

public class StoreMenu extends Menu{

	private Texture logo;
	private float cooldown = 0f;
	
	/**
	 * Creates a StoreMenu object.
	 */
	public StoreMenu() {
		super(new Position(0, 0, 0), Display.getWidth(), Display.getHeight());
		float textHeight = Main.getInstance().font.getHeight("sample text");
		TransactionButton tb = new TransactionButton(1, 160, FileUtilities.texturesPath + "upgrade_radius.png", "Light radius upgrade", "Increases radius of arrow light!", 45);
		tb.identifier = "upgrade_radius";
		widgets.add(tb);
		tb = new TransactionButton(2, 160, FileUtilities.texturesPath + "upgrade_radius.png", "More Damage", "Arrows cause more damage!", 45);
		tb.identifier = "upgrade_damage";
		widgets.add(tb);
		tb = new TransactionButton(3, 160, FileUtilities.texturesPath + "upgrade_radius.png", "Range upgrade", "Increases range of arrow", 45);
		tb.identifier = "upgrade_range";
		widgets.add(tb);
		try {
			logo = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "charredsoftware.png"));
		} catch (IOException e) {new CrashReport(e);}
	}
	
	/**
	 * Updates the menu.
	 * Catches mouse clicks.
	 */
	public void update(){
		if(cooldown > 0) cooldown -= 1;
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && cooldown == 0) Main.getInstance().gameState = Main.getInstance().previousState;
		if(Mouse.isButtonDown(0)){
			for(Widget b : widgets){
				if(!b.mouseInBounds()) continue;
				if(b.identifier.equalsIgnoreCase("upgrade_radius")){
					TransactionButton tb = (TransactionButton) b;
					if(!tb.used && Main.getInstance().player.coins >= tb.cost){
						tb.used = true;
						Sys.alert("Upgrade purchased", "You just purchased the " + tb.item + " item for " + tb.cost + " coins!");
						Main.getInstance().player.bow.UPGRADE_LARGER_RADIUS = true;
					}
				}
				if(b.identifier.equalsIgnoreCase("upgrade_damage")){
									
								}
				if(b.identifier.equalsIgnoreCase("upgrade_range")){
					Main.getInstance().previousState = Main.getInstance().gameState;
					Main.getInstance().gameState = GameState.SETTINGS;
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
		
		String title = "Upgrades Menu";
		Main.getInstance().titleFont.drawString((Display.getWidth() - Main.getInstance().titleFont.getWidth(title)) / 2, 64, title);
		String escapeMessage = "Press Escape to return to the previous screen.";
		Main.getInstance().font.drawString((Display.getWidth() - Main.getInstance().font.getWidth(escapeMessage)) / 2, 64 + Main.getInstance().titleFont.getHeight(title) + 4, escapeMessage);
		
		for(Widget w : widgets) w.render();
		
		
		glEnable(GL_LIGHTING);
		glEnable(GL_TEXTURE_2D);
		
		logo.bind();
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); glVertex2f(Display.getWidth() - 16 - 512, Display.getHeight() - 512 - 16);
		glTexCoord2f(1f, 0f); glVertex2f(Display.getWidth() - 16, Display.getHeight() - 512 - 16);
		glTexCoord2f(1f, 1f); glVertex2f(Display.getWidth() - 16, Display.getHeight() - 16);
		glTexCoord2f(0f, 1f); glVertex2f(Display.getWidth() - 16 - 512, Display.getHeight() - 16);
		glEnd();
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
	}

	
}
