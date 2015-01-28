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
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.GameState;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.Sound;
import com.charredsoftware.tsa.world.Position;
import com.charredsoftware.tsa.world.World;

/**
 * Puzzle Class.
 * Creates a new Puzzle.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since January 17, 2015
 */
public class Puzzle extends Menu{

	public static final float _PADDING = 16f;
	public float length = 5f;
	public long puzzle;
	public Random r = new Random();
	public static final int _TICKS_IN_DISPLAYING = (int) (Main.DESIRED_TPS * 3.5); 
	public int displayTicks = 0;
	public long enteredValue = -1;
	private float cooldown = 10f;
	
	/**
	 * Creates a new Puzzle menu.
	 */
	public Puzzle(){
		super(new Position(0, 0, 0), Display.getWidth(), Display.getHeight());
		for(int i = -1; i <= 9; i ++){
			if(i < 0 || i > 9) continue;
			widgets.add(new PuzzleButton(this, i));
		}
		
		length += Main.getInstance().controller.difficulty * 2 - 1;
		generatePuzzle();
		displayTicks = _TICKS_IN_DISPLAYING;
	}
	
	/**
	 * @return Returns the y positional offset for buttons to be drawn (space for stuff above buttons).
	 */
	public float getButtonsYOffset(){
		return getUsableHeight() / 5;
	}
	
	public float getXStart(){
		return (Display.getWidth() - getUsableWidth()) / 2;
	}
	
	public float getYStart(){
		return (Display.getHeight() - getUsableHeight()) / 2;
	}
	
	public float getUsableWidth(){
		return Display.getWidth() / 3;
	}
	
	public float getUsableHeight(){
		return Display.getHeight() * 2 / 3;
	}
	
	/**
	 * Generates a new puzzle.
	 */
	public void generatePuzzle(){
		String puzzle = "";
		for(int i = 0; i < length; i ++){
			int a = r.nextInt(10);
			if((i == 0 || i == length) && a == 0) a ++;
			puzzle += a;
		}
		this.puzzle = Long.parseLong(puzzle);
	}
	
	/**
	 * Updates the puzzle.
	 */
	public void update(){
		if(cooldown > 0) cooldown --;
		if(displayTicks > 0) displayTicks --;
		else{
			for(Widget w : widgets){
				if(!(w instanceof PuzzleButton)) continue;
				PuzzleButton button = (PuzzleButton) w;
				button.highlight = false;
				if(button.mouseInBounds()){
					if(Mouse.isButtonDown(0) && cooldown == 0){
						cooldown = 10f;
						Sound.BUTTON_CLICKED.playSfxIfNotPlaying();
						addDigitToValue(button.value);
						evaluatePuzzle();
					}else button.highlight = true;
				}
			}
		}
	}
	
	private void addDigitToValue(int digit){
		String val = ((enteredValue == -1) ? "" : enteredValue) + "" + digit + "";
		enteredValue = Long.parseLong(val);
	}
	
	private void evaluatePuzzle(){
		if(String.valueOf(enteredValue).length() == length){
			if(enteredValue == puzzle){
				Sound.PUZZLE_SOLVED.playSfxIfNotPlaying();
				Main.getInstance().gameState = GameState.GAME;
				Main.getInstance().player.world = new World(Main.getInstance().player.world.id + 1);
				Main.getInstance().player.spawn();
			}else{
				enteredValue = -1;
				displayTicks = _TICKS_IN_DISPLAYING;
			}
		}
	}
	
	/**
	 * Renders the puzzle
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

		glColor4f(0, 0, 0, 1f);
		glBegin(GL_QUADS);
		glVertex2f(0, 0);
		glVertex2f(width, 0);
		glVertex2f(width, height);
		glVertex2f(0, height);
		glEnd();
		
		glColor4f(110 / 255f, 110 / 255f, 110 / 255f, 1f);
		glBegin(GL_QUADS);
		glVertex2f(getXStart() - _PADDING, getYStart() - _PADDING);
		glVertex2f(getXStart() + getUsableWidth() + _PADDING, getYStart() - _PADDING);
		glVertex2f(getXStart() + getUsableWidth() + _PADDING, getYStart() + getUsableHeight() + _PADDING);
		glVertex2f(getXStart() - _PADDING, getYStart() + getUsableHeight() + _PADDING);
		glEnd();
		
		float barHeight = getButtonsYOffset() - _PADDING;
		
		glColor4f(200 / 255f, 200 / 255f, 200 / 255f, 1f);
		glBegin(GL_QUADS);
		glVertex2f(getXStart(), getYStart());
		glVertex2f(getXStart() + getUsableWidth(), getYStart());
		glVertex2f(getXStart() + getUsableWidth(), getYStart() + barHeight);
		glVertex2f(getXStart(), getYStart() +  barHeight);
		glEnd();
		
		glEnable(GL_TEXTURE_2D);
		
		String val = ((enteredValue == -1) ? "" : enteredValue + "");
		Main.getInstance().font.drawString(getXStart() + (getUsableWidth() - Main.getInstance().font.getWidth(val)) / 2, getYStart() + (barHeight - Main.getInstance().font.getHeight(val)) / 2, val);
		
		Main.getInstance().controller.drawRemainingTime();
		
		if(displayTicks > 0){
			float ticksPerNumber = _TICKS_IN_DISPLAYING / length;
			int numberPlace = (int) (length - ((_TICKS_IN_DISPLAYING - displayTicks) / ticksPerNumber));
			int selectedNumber = (int) ((int) (puzzle / Math.pow(10, numberPlace)) % 10);
			
			for(Widget w : widgets){
				if(!(w instanceof PuzzleButton)) continue;
				PuzzleButton b = (PuzzleButton) (w);
				b.highlight = (displayTicks != 1 && b.value == selectedNumber);
			}
		}
		
		for(Widget w : widgets) w.render();
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
	}
	
}
