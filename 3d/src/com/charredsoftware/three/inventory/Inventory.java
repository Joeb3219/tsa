package com.charredsoftware.three.inventory;

import java.util.ArrayList;

public class Inventory {

	public int size = 0;
	public ArrayList<ItemGroup> items = new ArrayList<ItemGroup>();
	
	public Inventory(int size){
		this.size = size;
	}
	
	public void expand(int num){
		size += num;
	}
	
	public ItemGroup getItem(int position){
		if(position >= size) return items.get(0);
		return items.get(position);
	}
	
	public void removeItem(ItemGroup item){
		items.remove(item);
	}
	
	public void removeItem(int position){
		if(position >= size) return;
		items.remove(position);
	}
	
	public void addItem(ItemGroup item, int position){
		if(position == -1 || position >= size) return;
		items.add(position, item);
	}
	
	public void addItem(ItemGroup item){
		addItem(item, getFirstOpenSpot());
	}
	
	public int getFirstOpenSpot(){
		if(items.size() < size - 1) return items.size();
		return -1;
	}
	
}
