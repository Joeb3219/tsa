package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.charredsoftware.tsa.Main;

/**
 * The DialogHUD class. Contains many HUD messages.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 10th, 2014
 */

public class DialogHUD {

	public ArrayList<Dialog> dialogs = new ArrayList<Dialog>();
	public float x, y;
	public float cooldown = 0f;
	
	/**
	 * Creates a new DialogHUD object.
	 */
	public DialogHUD(){
		this.x = 0;
		this.y = Display.getHeight();
	}

	/**
	 * @return Returns <tt>true</tt> if more dialogs are waiting to be displayed.
	 */
	public boolean hasDialogs(){
		if(!Main.getInstance().controller.displayDialogs) return false;
		if(dialogs.size() > 0) return true;
		return false;
	}
	
	/**
	 * Updates all of the dialogs.
	 */
	public void update(){
		cooldown = Math.max(cooldown - 1, 0);
		if(!hasDialogs()) return;
		if(cooldown == 0 && Keyboard.isKeyDown(Keyboard.KEY_RETURN)){
			cooldown += 15f;
			dialogs.get(0).lineRead();
			if(dialogs.get(0).outOfLines()) dialogs.remove(0);
		}
	}
	
	/**
	 * @return Returns the width of the dialog text.
	 */
	public float getDialogWidth(){
		return Display.getWidth();
	}
	
	/**
	 * Resets the lines of a dialog, pending a width change (makes them adjust to size change).
	 */
	public void resetDialogLines(){
		for(Dialog d : dialogs) d.splitIntoSlides();
	}
	
	/**
	 * Renders the current dialog.
	 */
	public void render(){
		float width = Display.getWidth() - x;
		float height = 112;
		y = Display.getHeight();
		float y = this.y - height;
		
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		glDisable(GL_TEXTURE_2D);
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		glBegin(GL_QUADS);
		glVertex2f(x, y);
		glVertex2f(x + width, y);
		glVertex2f(x + width, y + height);
		glVertex2f(x, y + height);
		glEnd();
		
		glPopMatrix();
		
		float nameTabTop = Main.font.getHeight("ABCDEF") + 8;
		glPushMatrix();
		glColor3f(0.75f, 0.75f, 0.75f);
		glBegin(GL_QUADS);
		glVertex2f(x, y);
		glVertex2f(x + 192, y);
		glVertex2f(x + 192, y - nameTabTop);
		glVertex2f(x, y - nameTabTop);
		glEnd();
		glPopMatrix();
		
		glEnable(GL_TEXTURE_2D);
		
		String authorText = dialogs.get(0).author.name + ":";
		Main.font.drawString(8, y - nameTabTop + 4, authorText, Color.black);
		
		dialogs.get(0).render(x + 10, y + 10);
		String continueText = "Press Enter to continue...";
		Main.getInstance().font.drawString(Display.getWidth() - 16 - Main.getInstance().font.getWidth(continueText), y + height - Main.getInstance().font.getHeight("ABCDEF") - 2, continueText, Color.darkGray);
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glEnable(GL_LIGHTING);
		glMatrixMode(GL_MODELVIEW);
	}
	
}
