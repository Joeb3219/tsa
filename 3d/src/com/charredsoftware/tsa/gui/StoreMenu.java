package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import com.charredsoftware.tsa.Mouse;

import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.GameState;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.Sound;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Position;

/**
 * A StoreMenu!
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since January 4, 2015
 */

public class StoreMenu extends Menu{

	private Texture logo;
	private float cooldown = 0f;
	
	/**
	 * Creates a StoreMenu object.
	 */
	public StoreMenu() {
		super(new Position(0, 0, 0), Display.getWidth(), Display.getHeight());
		TransactionButton tb = new TransactionButton(1, 180, FileUtilities.texturesPath + "upgrade_radius.png", "Light Radius Upgrade", "Increases radius of arrow light!", 45);
		tb.identifier = "upgrade_radius";
		widgets.add(tb);
		tb = new TransactionButton(2, 180, FileUtilities.texturesPath + "upgrade_damage.png", "More Damage", "Arrows cause more damage!", 85);
		tb.identifier = "upgrade_damage";
		widgets.add(tb);
		tb = new TransactionButton(3, 180, FileUtilities.texturesPath + "upgrade_range.png", "Arrow Range upgrade", "Increases range of arrow", 65);
		tb.identifier = "upgrade_range";
		widgets.add(tb);
		Button back = new Button(this, 350, "Back");
		back.identifier = "back";
		back.checkable = false;
		widgets.add(back);
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
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && cooldown == 0){
			GameState s = Main.getInstance().previousState;
			Main.getInstance().previousState = Main.getInstance().gameState;
			Main.getInstance().gameState = s;
		}
		if(Mouse.isButtonDown(0)){
			for(Widget b : widgets){
				if(cooldown != 0 || !b.mouseInBounds()) continue;
				cooldown = 15f;
				Sound.BUTTON_CLICKED.playSfxIfNotPlaying();
				if(b.identifier.equalsIgnoreCase("upgrade_radius")){
					TransactionButton tb = (TransactionButton) b;
					if(!tb.used && Main.getInstance().player.coins >= tb.cost){
						tb.used = true;
						Main.getInstance().player.bow.UPGRADE_LARGER_RADIUS = true;
						Main.getInstance().player.coins -= tb.cost;
					}
				}
				if(b.identifier.equalsIgnoreCase("upgrade_damage")){
					TransactionButton tb = (TransactionButton) b;
					if(!tb.used && Main.getInstance().player.coins >= tb.cost){
						tb.used = true;
						Main.getInstance().player.bow.UPGRADE_MORE_DAMAGE = true;
						Main.getInstance().player.coins -= tb.cost;
					}
				}else if(b.identifier.equalsIgnoreCase("upgrade_range")){
					TransactionButton tb = (TransactionButton) b;
					if(!tb.used && Main.getInstance().player.coins >= tb.cost){
						tb.used = true;
						Main.getInstance().player.bow.UPGRADE_FURTHER_SHOTS = true;
						Main.getInstance().player.coins -= tb.cost;
					}
				}else if(b.identifier.equalsIgnoreCase("back")){
					GameState s = Main.getInstance().previousState;
					Main.getInstance().previousState = Main.getInstance().gameState;
					Main.getInstance().gameState = s;
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

		preRender();

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
		String coins = "Total coins: " + Main.getInstance().player.coins;
		Main.getInstance().font.drawString((Display.getWidth() - Main.getInstance().font.getWidth(coins)) / 2, 64 + Main.getInstance().titleFont.getHeight(title) + 4 + Main.getInstance().font.getHeight(escapeMessage) + 16, coins);
		
		for(Widget w : widgets) w.render();
		
		
		glDisable(GL_LIGHTING);
		glEnable(GL_TEXTURE_2D);
		
		logo.bind();
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); glVertex2f(Display.getWidth() - 16 - 512, Display.getHeight() - 512 - 16);
		glTexCoord2f(1f, 0f); glVertex2f(Display.getWidth() - 16, Display.getHeight() - 512 - 16);
		glTexCoord2f(1f, 1f); glVertex2f(Display.getWidth() - 16, Display.getHeight() - 16);
		glTexCoord2f(0f, 1f); glVertex2f(Display.getWidth() - 16 - 512, Display.getHeight() - 16);
		glEnd();
		
		glEnable(GL_DEPTH_TEST);
		
		postRender();
	}

	
}
