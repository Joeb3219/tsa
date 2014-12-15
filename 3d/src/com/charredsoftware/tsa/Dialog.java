package com.charredsoftware.tsa;

/**
 * The Dialog class. Used to hold actual dialog messages.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 10th, 2014
 */

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

public class Dialog {

	public DialogAuthor author;
	public String text;
	public ArrayList<String> lines = new ArrayList<String>();
	public int displayLine = 0;
	
	/**
	 * Creates a new Dialog object
	 * @param author Author of the dialog
	 * @param text Dialog text
	 */
	public Dialog(DialogAuthor author, String text){
		this.author = author;
		this.text = text;
		splitIntoLines();
	}
	
	/**
	 * Splits a message into several chunks.
	 */
	private void splitIntoLines(){
		if(!text.contains("@")) lines.add(text);
		else{
			for(String s : text.split("@")) lines.add(s);
		}
	}
	
	/**
	 * Increments the line currently being read.
	 */
	public void lineRead(){
		displayLine ++;
	}

	/**
	 * @return Returns <tt>true</tt> if all of the lines have been viewed.
	 */
	public boolean outOfLines(){
		if(displayLine >= lines.size()) return true;
		return false;
	}
	
	/**
	 * Renders the dialog.
	 * @param x X-Position
	 * @param y Y-Position
	 */
	public void render(float x, float y){
		Font font = Main.getInstance().font;
		font.drawString(x, y, lines.get(displayLine), Color.black);
	}
	
}
