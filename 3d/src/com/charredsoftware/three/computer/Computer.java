package com.charredsoftware.three.computer;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.charredsoftware.three.CrashReport;
import com.charredsoftware.three.GameState;
import com.charredsoftware.three.Main;
import com.charredsoftware.three.gui.TerminalDisplay;
import com.charredsoftware.three.gui.TextDisplay;
import com.charredsoftware.three.gui.TextEditDisplay;
import com.charredsoftware.three.util.FileUtilities;

public class Computer extends Peripheral{

	public int id;
	public String mac;
	public File dir;
	public Script loadedScript = null;
	public TextDisplay t;
	public boolean editing = false, menu = false, executing = false;
	public HardwareSet hardware = new HardwareSet();
	
	public Computer(float x, float y, float z, float special){
		super(x, y, z, special);
		t = new TerminalDisplay(x, y, Display.getHeight() - 2 * y, Display.getWidth() - 2 * x, new ArrayList<String>());
	}
	
	public Computer(float x, float y, float z, float special, String json){
		super(x, y, z, special);
		t = new TerminalDisplay(x, y, Display.getHeight() - 2 * y, Display.getWidth() - 2 * x, new ArrayList<String>());
		if(!json.equals("")){
			hardware.loadFromJson(json);
		}
	}
	
	public Computer(){
		super(0, 0, 0, -1);
		t = new TerminalDisplay(x, y, Display.getHeight() - 2 * y, Display.getWidth() - 2 * x, new ArrayList<String>());
	}
	
	public float generateSpecialId(){
		if(special != -1){
			dir = new File(Main.getInstance().player.world.dir.getAbsolutePath(), FileUtilities.computersPath + special);
			dir.mkdirs();
			return special; //One is already assigned, everything is good.
		}
		dir = new File(Main.getInstance().player.world.dir.getAbsolutePath(), FileUtilities.computersPath);
		String[]entries = dir.list();
		float highest = -1f;
		for(String s : entries){
			//Look for highest directory -> next id will be the highest + 1.
			if(!new File(dir, s).isDirectory()) continue;
			float current = Float.parseFloat(s);
			highest = Math.max(current, highest);
		}
		highest ++;
		dir = new File(dir, highest + "");
		dir.mkdirs();
		copyDefaultScripts();
		return highest;
	}
	
	public void copyDefaultScripts(){
		File defaultDir = new File(FileUtilities.defaultProgramsPath);
		for(String s : defaultDir.list()){
			FileUtilities.copyFile(new File(defaultDir, s), new File(dir, s));
		}
	}
	
	public LuaValue executeScript(String name){
		Globals globals = JsePlatform.standardGlobals();
		try {
			globals.STDOUT = new PrintStream(FileUtilities.scriptTempFile);
		} catch (FileNotFoundException e) {new CrashReport(e);}
		LuaValue chunk = globals.loadfile(dir.getAbsolutePath() + "/" + name);
		return chunk.call();
	}
	
	public void changeLoadedScript(Script script){
		this.loadedScript = script;
		loadedScript.loadScript();
		t.lines = loadedScript.lines;
		t.yCursor = 0;
		t.xCursor = 0;
	}
	
	public void update(){
		if(Main.getInstance().gameState != GameState.COMPUTER) return;
		if((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))){
			if(Keyboard.isKeyDown(Keyboard.KEY_S) && editing){
				loadedScript.saveScript(t.lines);
				return;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_E)){
				if(editing) loadedScript.saveScript(t.lines);
				editing = false;
				executing = false;
				t = new TerminalDisplay(x, y, Display.getHeight() - 2 * y, Display.getWidth() - 2 * x, new ArrayList<String>());
			}
		}
		t.update();
	}
	
	public void draw(){
		glLoadIdentity();
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST); 
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		glBegin(GL_QUADS);
		glColor3f(.4f, .4f, .4f);
		
		glVertex2f(10f, 10f);
		glVertex2f(Display.getWidth() - 10f, 10f);
		glVertex2f(Display.getWidth() - 10f, Display.getHeight() - 10f);
		glVertex2f(10f, Display.getHeight() - 10f);
		
		glEnd();
		
		t.draw();
		
		if(executing){
			if(dir.list().length > 0){
				if(loadedScript == null) changeLoadedScript(new Script(dir, dir.list()[0]));
				executeScript(loadedScript.name);
				String output = "";
				try {
			          FileInputStream inputStream =  new FileInputStream(FileUtilities.scriptTempFile);
			          BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			          String line = "";
			          while ((line = reader.readLine()) != null) {
			             output += line;
			          }
			          reader.close();
			    }catch (Exception e) {new CrashReport(e);}
				Main.getInstance().font.drawString(15, 15, output);
			}
			t.clearScreen();
		}
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		
	}
	
	public ArrayList<String> getAbout(){
		ArrayList<String> about = new ArrayList<String>();
		about.add("NovaOS, v. 1.1.5");
		about.add("NovaOS presented by CharredSoftware, programmed by Joe Boyle.");
		about.add("Enjoy!");
		return about;
	}
	
	public ArrayList<String> getInfo(){
		ArrayList<String> info = new ArrayList<String>();
		info.add(getPosition().toString());
		info.add("Computer ID: " + special);
		info.add("Programs: (" + dir.list().length + ")");
		for(String s : hardware.getInfo()) info.add(s);
		return info;
	}
	
	public void newScript(String name){
		editScript(name + ".csf");
	}
	
	public void runScript(String name){
		if(!name.contains(".csf")) name += ".csf";
		t = new TextDisplay(x, y, Display.getHeight() - 2 * y, Display.getWidth() - 2 * x, new ArrayList<String>());
		changeLoadedScript(new Script(dir, name));
		executing = true;
		editing = false;
	}
	
	public void editScript(String name){
		if(!name.contains(".csf")) name += ".csf";
		changeLoadedScript(new Script(dir, name));
		t = new TextEditDisplay(x, y, Display.getHeight() - 2 * y, Display.getWidth() - 2 * x, loadedScript.lines);
		executing = false;
		editing = true;
	}
	
	public void reboot(){
		//reboot
	}
	
	public String getSpecialJson(){
		String response = "{";
		response += hardware.getJson();
		response += "}";
		return response;
	}
	
	
}
