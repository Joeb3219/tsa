package com.charredsoftware.tsa.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

import com.charredsoftware.tsa.Main;

/**
 * The Dialog class. Used to hold actual dialog messages.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 10th, 2014
 */

public class Dialog {

	public DialogAuthor author;
	public String text;
	public ArrayList<ArrayList<String>> slides = new ArrayList<ArrayList<String>>();
	public int displaySlide = 0, sidePadding = 16;
	
	/**
	 * Creates a new Dialog object
	 * @param author Author of the dialog
	 * @param text Dialog text
	 */
	public Dialog(DialogAuthor author, String text){
		this.author = author;
		this.text = text;
		splitIntoSlides();
	}
	
	/**
	 * Splits a slide into several lines.
	 */
	private void splitIntoLines(String slide){
		int usableWidth = getUsableWidth();
		ArrayList<String> slideLines = new ArrayList<String>();
		String currentLine = "";
		for(String word : slide.split(" ")){
			if(Main.font.getWidth(currentLine + " " + word) > usableWidth){
				slideLines.add(currentLine);
				currentLine = word;
			}else currentLine += ((currentLine.length() == 0) ? "" :" ") + word;
		}
		slideLines.add(currentLine);
		
		slides.add(slideLines);
	}
	
	/**
	 * Splits a message into several slides.
	 */
	public void splitIntoSlides(){
		slides = new ArrayList<ArrayList<String>>();
		if(!text.contains("@")) splitIntoLines(text);
		else{
			for(String s : text.split("@")) splitIntoLines(s);
		}
	}
	
	/**
	 * Increments the line currently being read.
	 */
	public void lineRead(){
		displaySlide ++;
	}

	/**
	 * @return Returns <tt>true</tt> if all of the lines have been viewed.
	 */
	public boolean outOfLines(){
		if(displaySlide >= slides.size()) return true;
		return false;
	}
	
	/**
	 * Renders the dialog.
	 * @param x X-Position
	 * @param y Y-Position
	 */
	public void render(float x, float y){
		Font font = Main.getInstance().font;
		float yPosition = y;
		float previousLineHeight = 0;
		for(String line : slides.get(displaySlide)){
			yPosition += previousLineHeight;
			font.drawString(getTextStartX(), yPosition, line, Color.black);
			previousLineHeight = font.getHeight(line) + 4;
		}
	}
	
	/**
	 * @return Returns the starting X-position of the text.
	 */
	public int getTextStartX(){
		return sidePadding;
		//return sidePadding + 64 + sidePadding;
	}
	
	/**
	 * @return Returns the non-padding, non-author depictor, width of the dialog.
	 */
	public int getUsableWidth(){
		return (Display.getWidth() - sidePadding * 2) - getTextStartX();
	}
	
}
