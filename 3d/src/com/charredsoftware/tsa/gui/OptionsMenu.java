package com.charredsoftware.tsa.gui;

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
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.Jukebox;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.Mouse;
import com.charredsoftware.tsa.Sound;
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
	public float cooldown = 10f;
	public String tabName = "main"; //Four values: main, sound, graphics, controls
	
	public OptionsMenu() {
		super(new Position(0, 0, 0), Display.getWidth(), Display.getHeight());

		float startingButtonHeight = 160f;
		float textHeight = Main.getInstance().font.getHeight("sample text");
		Button b = new Button(this, startingButtonHeight, "Developer Mode");
		b.identifier = "main_developer_mode";
		b.checked = Main.getInstance().controller.developerMode;
		widgets.add(b);
		ToggleSwitcher t = new ToggleSwitcher(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Difficulty", new ArrayList<String>(Arrays.asList("Easy", "Normal", "Hard")), Main.getInstance().controller.difficulty - 1);
		t.identifier = "main_difficulty";
		widgets.add(t);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Sounds");
		b.identifier = "main_sound_tab";
		b.checkable = false;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Graphics");
		b.identifier = "main_graphics_tab";
		b.checkable = false;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Controls");
		b.identifier = "main_controls_tab";
		b.checkable = false;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10 + 60, "Back");
		b.identifier = "main_goback";
		b.checkable = false;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Quit");
		b.identifier = "main_quit";
		b.checkable = false;
		widgets.add(b);
		
		//Sounds settings
		Slider s = new Slider(this, startingButtonHeight, "Sound Volume", 0, 100, Main.getInstance().controller.soundVolume * 100f);
		s.identifier = "sound_volume_slider";
		widgets.add(s);
		s = new Slider(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Music Volume", 0, 100, Main.getInstance().controller.musicVolume * 100f);
		s.identifier = "sound_music_slider";
		widgets.add(s);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10 + 60, "Back");
		b.identifier = "sound_goback";
		b.checkable = false;
		widgets.add(b);
		
		//Graphics settings
		b = new Button(this, startingButtonHeight, "Fullscreen");
		b.identifier = "graphics_fullscreen";
		b.checked = Main.getInstance().controller.fullscreen;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Enable V-Sync");
		b.identifier = "graphics_vsync";
		b.checked = Main.getInstance().controller.vsync;
		widgets.add(b);
		s = new Slider(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Field of View", 0, 150, Main.getInstance().camera.fov);
		s.identifier = "graphics_fov_slider";
		widgets.add(s);
		s = new Slider(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Gamma", 0, 100, Main.getInstance().controller.gamma * 100f);
		s.identifier = "graphics_gamma_slider";
		widgets.add(s);
		s = new Slider(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Brightness", 0, 100, Main.getInstance().controller.brightness * 100f + 50);
		s.identifier = "graphics_brightness_slider";
		widgets.add(s);
		s = new Slider(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Contrast", 0, 100, Main.getInstance().controller.contrast * 100f);
		s.identifier = "graphics_contrast_slider";
		widgets.add(s);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10 + 60, "Back");
		b.identifier = "graphics_goback";
		b.checkable = false;
		widgets.add(b);
		
		//Controls settings
		ControlSwitcher cs = new ControlSwitcher(this, startingButtonHeight, "Move forward", Main.getInstance().controller.control_forward);
		cs.identifier = "controls_control_forward";
		widgets.add(cs);
		cs = new ControlSwitcher(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Move Backward", Main.getInstance().controller.control_backward);
		cs.identifier = "controls_control_backward";
		widgets.add(cs);
		cs = new ControlSwitcher(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Strafe Left", Main.getInstance().controller.control_strafe_left);
		cs.identifier = "controls_control_strafe_left";
		widgets.add(cs);
		cs = new ControlSwitcher(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Strafe Right", Main.getInstance().controller.control_strafe_right);
		cs.identifier = "controls_control_strafe_right";
		widgets.add(cs);
		cs = new ControlSwitcher(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Crouch", Main.getInstance().controller.control_crouch);
		cs.identifier = "controls_control_crouch";
		widgets.add(cs);
		cs = new ControlSwitcher(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Jump", Main.getInstance().controller.control_jump);
		cs.identifier = "controls_control_jump";
		widgets.add(cs);
		cs = new ControlSwitcher(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "Buy/Sell", Main.getInstance().controller.control_buy);
		cs.identifier = "controls_control_buy";
		widgets.add(cs);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "To shoot a bow, hold down right click and then release.");
		b.identifier = "controls_null";
		b.checkable = false;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "The longer you hold the right click button, the further the shot will go.");
		b.identifier = "controls_null";
		b.checkable = false;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10, "To open chests, press left click.");
		b.identifier = "controls_null";
		b.checkable = false;
		widgets.add(b);
		b = new Button(this, widgets.get(widgets.size() - 1).pos.y + textHeight +  10 + 60, "Back");
		b.identifier = "controls_goback";
		b.checkable = false;
		widgets.add(b);
		try {
			logo = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "charredsoftware.png"));
		} catch (IOException e) {new CrashReport(e);}
	}
	
	public void update(){
		if(cooldown > 0) cooldown -= 1;
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && cooldown == 0){
			tabName = "main";
			Main.getInstance().gameState = Main.getInstance().previousState;
			Jukebox.getInstance().resume();
		}
		if(Mouse.isButtonDown(0) && cooldown == 0){
			for(Widget w : widgets){
				if(cooldown != 0 || !w.mouseInBounds()) continue;
				if(!w.identifier.startsWith(tabName)) continue;
				Sound.BUTTON_CLICKED.playSfxIfNotPlaying();
				cooldown = 10f;
				if(w.identifier.contains("sound_tab")) tabName = "sound";					
				if(w.identifier.contains("graphics_tab")) tabName = "graphics";					
				if(w.identifier.contains("controls_tab")) tabName = "controls";					
				if(w.identifier.contains("developer_mode")){
					((Button) w).checked = !((Button) w).checked;
					Main.getInstance().controller.developerMode = ((Button) w).checked;
				}
				if(w.identifier.contains("difficulty")){
					ToggleSwitcher t = (ToggleSwitcher) w;
					t.update();
					Main.getInstance().controller.difficulty = t.value + 1;
				}
				if(w.identifier.contains("fullscreen")){
					((Button) w).checked = !((Button) w).checked;
					Main.getInstance().controller.fullscreen = ((Button) w).checked;
					try {
						if(Main.getInstance().controller.fullscreen) Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
						else Display.setFullscreen(false);
						Main.getInstance().HUDDialog.resetDialogLines();
					} catch (LWJGLException e) {new CrashReport(e);}
				}
				if(w.identifier.contains("vsync")){
					((Button) w).checked = !((Button) w).checked;
					Main.getInstance().controller.vsync = ((Button) w).checked;
					Display.setVSyncEnabled(Main.getInstance().controller.vsync);
				}
				if(w.identifier.contains("settings")){
					
				}
				if(w.identifier.contains("volume_slider")){
					Slider s = (Slider) w;
					s.update();
					Main.getInstance().controller.soundVolume = s.value / 100f;
				}
				if(w.identifier.contains("music_slider")){
					Slider s = (Slider) w;
					s.update();
					Main.getInstance().controller.musicVolume = s.value / 100f;
					Jukebox.getInstance().changeVolume();
				}
				if(w.identifier.contains("fov_slider")){
					Slider s = (Slider) w;
					s.update();
					Main.getInstance().camera.fov = s.value;
					Main.getInstance().camera.resetAspectRatio(Main.getInstance().camera.aspectRatio);
				}
				if(w.identifier.contains("gamma_slider")){
					Slider s = (Slider) w;
					s.update();
					Main.getInstance().controller.gamma = s.value / 100f;
					try{
						Display.setDisplayConfiguration(Main.getInstance().controller.gamma, Main.getInstance().controller.brightness, Main.getInstance().controller.contrast);
					}catch(Exception e){}
				}
				if(w.identifier.contains("brightness_slider")){
					Slider s = (Slider) w;
					s.update();
					Main.getInstance().controller.brightness = ((s.value - 50) / 100f);
					try{
						Display.setDisplayConfiguration(Main.getInstance().controller.gamma, Main.getInstance().controller.brightness, Main.getInstance().controller.contrast);
					}catch(Exception e){}
				}
				if(w.identifier.contains("contrast_slider")){
					Slider s = (Slider) w;
					s.update();
					Main.getInstance().controller.contrast = s.value / 100f;
					try{
						Display.setDisplayConfiguration(Main.getInstance().controller.gamma, Main.getInstance().controller.brightness, Main.getInstance().controller.contrast);
					}catch(Exception e){}
				}
				if(w.identifier.contains("control_") && !w.identifier.contains("tab")){
					if(w instanceof ControlSwitcher){
						ControlSwitcher cs = (ControlSwitcher) w;
						cs.update();
						if(cs.identifier.contains("control_forward")) Main.getInstance().controller.control_forward = cs.value;
						if(cs.identifier.contains("control_backward")) Main.getInstance().controller.control_backward = cs.value;
						if(cs.identifier.contains("control_strafe_left")) Main.getInstance().controller.control_strafe_left = cs.value;
						if(cs.identifier.contains("control_strafe_right")) Main.getInstance().controller.control_strafe_right = cs.value;
						if(cs.identifier.contains("control_jump")) Main.getInstance().controller.control_jump = cs.value;
						if(cs.identifier.contains("control_crouch")) Main.getInstance().controller.control_crouch = cs.value;
						if(cs.identifier.contains("control_buy")) Main.getInstance().controller.control_buy = cs.value;
					}
				}
				if(w.identifier.contains("quit")){
					Main.getInstance().controller.saveSettings();
					AL.destroy();
					Display.destroy();
					System.exit(0);
				}
				if(w.identifier.contains("goback")){
					if(w.identifier.split("_")[0].equals(("main"))){
						Main.getInstance().gameState = Main.getInstance().previousState;
						Jukebox.getInstance().resume();
					}
					else tabName = "main";
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
		
		String title = "Settings";
		Main.getInstance().titleFont.drawString((Display.getWidth() - Main.getInstance().titleFont.getWidth(title)) / 2, 64, title);
		String escapeMessage = "Press Escape to return to the previous screen.";
		Main.getInstance().font.drawString((Display.getWidth() - Main.getInstance().font.getWidth(escapeMessage)) / 2, 64 + Main.getInstance().titleFont.getHeight(title) + 4, escapeMessage);
		
		for(Widget w : widgets){
			if(w.identifier.startsWith(tabName)) w.render();
		}
		
		
		glDisable(GL_LIGHTING);
		glEnable(GL_TEXTURE_2D);
		
		logo.bind();
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); glVertex2f(Display.getWidth() - 16 - 512, Display.getHeight() - 512 - 16);
		glTexCoord2f(1f, 0f); glVertex2f(Display.getWidth() - 16, Display.getHeight() - 512 - 16);
		glTexCoord2f(1f, 1f); glVertex2f(Display.getWidth() - 16, Display.getHeight() - 16);
		glTexCoord2f(0f, 1f); glVertex2f(Display.getWidth() - 16 - 512, Display.getHeight() - 16);
		glEnd();
		
		postRender();
	}

	
}
