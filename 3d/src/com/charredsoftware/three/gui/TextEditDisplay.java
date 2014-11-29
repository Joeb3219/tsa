package com.charredsoftware.three.gui;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.charredsoftware.three.Main;

public class TextEditDisplay extends TextDisplay{

	public TextEditDisplay(){
	}
	
	public TextEditDisplay(float x, float y, float height, float width, ArrayList<String> lines){
		super(x, y, height, width, lines);
		if(lines.size() == 0) lines.add("");
	}
	
	public void scroll(int num){
		if(cooldown > 0) return;
		if(num > 0 && yCursor > 0){
			yCursor --;
			xCursor = 0;
		}
		if(num < 0 && yCursor < lines.size() - 1){
			yCursor ++;
			xCursor = 0;
		}
		cooldown += 3f;
	}
	
	public void navigate(int xNum, int yNum){
		if(cooldown > 0) return;
		scroll(yNum);
		if(xNum > 0 && xCursor < lines.get(yCursor).length()) xCursor ++;
		if(xNum < 0 && xCursor > 0) xCursor --;
		cooldown += 3f;
	}
	
	public void update(){
		if(cooldown > 0) cooldown --;
		if(xCursor < 0) xCursor = 0;
		if(yCursor < 0) yCursor = 0;
		if(yCursor > lines.size()) yCursor = lines.size() - 1;
		if(xCursor > lines.get(yCursor).length()) xCursor = lines.get(yCursor).length();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_RETURN) && cooldown == 0){
			cooldown += 10f;
			int position = lines.size();
			if(yCursor != lines.size() - 1) position = yCursor + 1;
			lines.add(position, "");
			yCursor = position;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_BACK) && cooldown == 0){
			cooldown += 10f;
			if(xCursor == 0 && yCursor > 0){
				lines.remove(yCursor);
				yCursor --;
				return;
			}
			String n = "";
			for(int i = 0; i < lines.get(yCursor).length(); i ++){
				if(xCursor - 1 == i) continue;
				n += lines.get(yCursor).charAt(i);
			}
			lines.remove(lines.get(yCursor));
			lines.add(yCursor, n);
			xCursor --;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) scroll(-1);
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) scroll(1);
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) navigate(-1, 0);
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) navigate(1, 0);

		while(Keyboard.next()) {
			char a = Keyboard.getEventCharacter();
			if(!Character.isDefined(a)) continue;
			if(Character.isAlphabetic(a) || Character.isDigit(a) || a == '/' || a == '\\' || a == '~' || a == '{' || a == '}' || a == '[' || a == ']' || a == ',' || a == '.' || a == '<' || a == '>' || a == ' ' || a == '!' || a == '@' || a == '#' 
					|| a == '$' || a == '%' || a == '^' || a == '&' || a == '*' || a == '(' || a == ')' || a == '_' || a == '=' || a == '+' || a == '\'' || a == '\"' || a == '-' || a == '|'){
				String s = lines.get(yCursor);
				lines.remove(s);
				if(s.length() == 0) s += a;
				else s = s.substring(0, xCursor) + a + s.substring(xCursor, s.length());
				lines.add(yCursor, s);
				xCursor ++;
			}
		}
		
	}
	
	public void draw(){
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
		int maxLines = (int) ((height - 10) / 20);
		int linesAfterCursor = (lines.size() - 1) - yCursor;
		int linesBeforeCursor = maxLines - linesAfterCursor;
		int drawnLines = 0;
		for(String s : lines){
			yPos ++;
			int netLine = yPos - yCursor;
			if(lines.size() > maxLines){
				if(!(linesBeforeCursor > 0 && netLine < 0 && Math.abs(netLine) <= linesBeforeCursor)) continue;
			}
			
			drawnLines ++;
			
			for(int i = 0; i < s.length(); i ++){
				Color c = Color.white;
				if(yPos == yCursor && i == xCursor) c = Color.red;
				else if(yPos == yCursor) c = Color.green;
				Main.getInstance().font.drawString(x + 10 + (Main.getInstance().font.getWidth(s.substring(0, i)) + 1), y + (drawnLines * 20) + 10, s.charAt(i) + "", c);
			}
		}

		glColor3f(1f, 1f, 1f);
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
	}
	
}
