package com.charredsoftware.tsa.inventory;

import static org.lwjgl.opengl.GL11.*;

public class ItemGroup {

	public Item item;
	private int quantity;
	
	public ItemGroup(Item item, int quantity){
		this.item = item;
		this.quantity = quantity;
	}
	
	public float getQuantity(){
		return quantity;
	}
	
	public void addQuantity(int num){
		this.quantity += num;
	}
	
	public void draw(int x, int z){
		if(item == Item.NULL) return;
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		
		glPushMatrix();
		glViewport(x, z, x + 64, z + 64);
		glLoadIdentity();
		glRotatef(60, 1, 0, 0);
		glRotatef(60, 0, 1, 0);
		glRotatef(60, 0, 0, 1);
		item.draw(x, z);
		glPopMatrix();
		
		glMatrixMode(GL_MODELVIEW);
	}
	
}
