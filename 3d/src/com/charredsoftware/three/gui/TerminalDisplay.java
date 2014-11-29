package com.charredsoftware.three.gui;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.io.File;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.charredsoftware.three.GameState;
import com.charredsoftware.three.Main;
import com.charredsoftware.three.computer.Computer;

public class TerminalDisplay extends TextDisplay{

	public String current = "";
	
	public TerminalDisplay(){
		super();
	}
	
	public TerminalDisplay(float x, float y, float height, float width, ArrayList<String> lines){
		super(x, y, height, width, lines);
	}
	
	public void scroll(int num) {
	}

	public void navigate(int xNum, int yNum) {
	}
	
	private void enterCommand(){
		if(current.equals("") || current.equals(" ")) return;
		Computer c = (Computer) Main.getInstance().player.selectedPeripheral;
		lines.add(current);
		if(current.equals("help")) lines.add("Commands: help, clear, info, about, reboot, exit, run <script>, edit <script>, copy <source> <dest>, new <name>, delete <script>");
		if(current.equals("reboot")) c.reboot();
		if(current.equals("info")){
			for(String s : c.getInfo()) lines.add(s);
		}
		if(current.equals("about")){
			for(String s : c.getAbout()) lines.add(s);
		}
		if(current.equals("exit")){
			Main.getInstance().player.selectedPeripheral = null;
			Main.getInstance().gameState = GameState.GAME.resetGameState(GameState.GAME);
		}
		if(current.equals("clear")) lines = new ArrayList<String>();
		if(current.equals("list")){
			String line = "";
			for(String s : c.dir.list()){
				if(s.contains(".csf")) s = s.split(".csf")[0];
				line += s + "; ";
			}
			lines.add("Programs: " + line);
		}
		if(current.contains(" ") && current.split(" ").length == 2){
			String base = current.split(" ")[0];
			String param = current.split(" ")[1];
			if(base.equals("run")) c.runScript(param);
			if(base.equals("edit")) c.editScript(param);
			if(base.equals("new")) c.newScript(param);
			if(base.equals("delete")){
				new File(c.dir, param + (!param.contains(".csf") ? ".csf" : "")).delete();
				lines.add("Deleted program " + param);
			}
		}
			
		current = "";
	}

	public void update() {
		if(cooldown > 0) cooldown --;
		xCursor = current.length();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)) enterCommand();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_BACK) && cooldown == 0){
			cooldown = 10;
			if(current.length() > 0){
				current = current.substring(0, xCursor - 1);
				xCursor --;
			}
		}
		
		while(Keyboard.next()) {
			char a = Keyboard.getEventCharacter();
			if(!Character.isDefined(a)) continue;
			if(Character.isAlphabetic(a) || Character.isDigit(a) || a == '/' || a == '\\' || a == '~' || a == '{' || a == '}' || a == '[' || a == ']' || a == ',' || a == '.' || a == '<' || a == '>' || a == ' ' || a == '!' || a == '@' || a == '#' 
					|| a == '$' || a == '%' || a == '^' || a == '&' || a == '*' || a == '(' || a == ')' || a == '_' || a == '=' || a == '+' || a == '\'' || a == '\"' || a == '-' || a == '|'){
				current += a;
				xCursor ++;
			}
		}
	}

	public void draw() {
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST); 
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		glLoadIdentity();
		
		int yPos = -1;
		for(String s : lines){
			yPos ++;
			int netLine = yPos - yCursor;
			if(netLine < 0) continue;
			
			for(int i = 0; i < s.length(); i ++){
				Main.getInstance().font.drawString(x + 10 + (Main.getInstance().font.getWidth(s.substring(0, i)) + 1), y + (netLine * 20) + 10, s.charAt(i) + "");
			}
		}
		
		Main.getInstance().font.drawString(x + 2 + Main.getInstance().font.getWidth("F:/ "), (y + height) - Main.getInstance().font.getHeight(current) - 10, "F:/ " + current);

		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
	}

}
