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

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Position;

/**
 * Options menu, extends Menu.
 * Options menu shows all of the options that can be set.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */
public class OptionsMenu extends Menu{

	private Texture logo;
	
	public OptionsMenu() {
		super(new Position(0, 0, 0), Display.getWidth(), Display.getHeight());
		float textHeight = Main.getInstance().font.getHeight("sample text");
		Button b = new Button(this, 60, "Developer Mode");
		b.identifier = "developer_mode";
		b.checked = Main.getInstance().controller.developerMode;
		widgets.add(b);
		b = new Button(this, 60 + textHeight + 10, "Fullscreen");
		b.identifier = "fullscreen";
		b.checked = Main.getInstance().controller.fullscreen;
		widgets.add(b);
		b = new Button(this, 60 + textHeight + 10 + textHeight + 10, "Settings");
		b.identifier = "settings";
		widgets.add(b);
		Slider s = new Slider(this, 60 + textHeight + 10 + textHeight + 10 + textHeight + 10, "Volume", 0, 100, 50);
		s.identifier = "volume_slider";
		widgets.add(s);
		s = new Slider(this, 60 + textHeight + 10 + textHeight + 10 + textHeight + 10 + textHeight + 10, "Testing", 0, 100, 50);
		s.identifier = "testing";
		widgets.add(s);
		try {
			logo = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "charredsoftware.png"));
		} catch (IOException e) {new CrashReport(e);}
	}
	
	private float cooldown = 10f;
	public void update(){
		if(cooldown > 0) cooldown -= 1;
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && cooldown == 0) Main.getInstance().gameState = Main.getInstance().previousState;
		if(Mouse.isButtonDown(0) && cooldown == 0){
			for(Widget w : widgets){
				if(!w.mouseInBounds()) continue;
				cooldown = 10f;
				if(w.identifier.equalsIgnoreCase("developer_mode")){
					((Button) w).checked = !((Button) w).checked;
					Main.getInstance().controller.developerMode = ((Button) w).checked;
				}
				if(w.identifier.equalsIgnoreCase("fullscreen")){
					((Button) w).checked = !((Button) w).checked;
					Main.getInstance().controller.fullscreen = ((Button) w).checked;
					try {
						if(Main.getInstance().controller.fullscreen) Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
						else Display.setFullscreen(false);
					} catch (LWJGLException e) {new CrashReport(e);}
				}
				if(w.identifier.equalsIgnoreCase("settings")){
					
				}
				if(w.identifier.equalsIgnoreCase("volume_slider")){
					Slider s = (Slider) w;
					s.update();
					Main.getInstance().controller.soundVolume = s.current / 100f;
				}
			}
		}
	}
	
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
