package com.charredsoftware.tsa;

import java.applet.Applet;
import java.awt.Canvas;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class AppletMain extends Applet{

	public Canvas display_parent;
 
	public void init() {
		try {
			display_parent = new Canvas() {
				public final void addNotify() {
					super.addNotify();
					try {
						Display.setParent(display_parent);
						Main.main(new String[0]);  
					} catch (LWJGLException e) {
						e.printStackTrace();
					}
				}
				public final void removeNotify() {
					destroy();
					Main.getInstance().cleanDisplay();
					super.removeNotify();
				}
			};
			
			
			display_parent.setSize(getWidth(),getHeight());
			add(display_parent);
			display_parent.setFocusable(true);
			display_parent.requestFocus();
			display_parent.setIgnoreRepaint(true);
			setVisible(true);
			
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException("Unable to create display");
		}
	}
 
	public void start() {

	}
 
	public void stop() {
 
	}
 
 
	public void destroy() {
		remove(display_parent);
		super.destroy();
	}
	
}
