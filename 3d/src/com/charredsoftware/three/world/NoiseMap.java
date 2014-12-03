package com.charredsoftware.three.world;

import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class NoiseMap {

	/*
	 * An implementation of a Noise Map.
	 * Code from Notch: http://www.twitch.tv/notch/b/302867302 @ 03:25:00
	 */
	
	public int width, height;
	public double[][] values;
	private Random r;
	
	public NoiseMap(int width, int height){
		this.width = width;
		this.height = height;
		
		values = new double[width][height];
		r = new Random();
		
		int stepSize = width;
		do{
			int halfStep = stepSize / 2;
			double scale = 1.0 / width;
			
			for(int y = 0; y < width; y += stepSize){
				for(int x = 0; x < width; x += stepSize){
					double a = sample(x, y);
					double b = sample(x + stepSize, y);
					double c = sample(x, y + stepSize);
					double d = sample(x + stepSize, y + stepSize);
					
					double e = (a + b + c + d) / 4.0 + (r.nextFloat() * 2 - 1) * stepSize * scale;
					setSample(x + halfStep, y + halfStep, e);
				}
			}
			for(int y = 0; y < width; y += stepSize){
				for(int x = 0; x < width; x += stepSize){
					double a = sample(x, y);
					double b = sample(x + stepSize, y);
					double c = sample(x, y + stepSize);
					double d = sample(x + halfStep, y + halfStep);
					double e = sample(x + halfStep, y - halfStep);
					double f = sample(x - halfStep, y + halfStep);
					
					double g = (a + c + d + f) / 4.0 + (r.nextFloat() * 2 - 1) * stepSize * scale;
					double h = (a + b + d + e) / 4.0 + (r.nextFloat() * 2 - 1) * stepSize * scale;
					setSample(x + halfStep, y, h);
					setSample(x, y + halfStep, g);
				}
			}
			
			stepSize /= 2;
		}while(stepSize > 1);
	
		factorToAverage();
		
		JOptionPane.showMessageDialog(null, null, "Map", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getAsImage()));
		JOptionPane.showMessageDialog(null, null, "Map", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getMassAsImage()));
		
	}
	
	private double sample(int x, int y){
		if(x >= width || y >= width) return values[width - 1][height - 1];
		if(x <= 0 || y <= 0) return values[0][0];
		return values[x][y];
	}
	
	private void setSample(int x, int y, double value){
		value = Math.abs(value);
		values[x][y] = value;
	}
	
	public BufferedImage getAsImage(){
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		int[] pixels = new int[width * height];
		int i = 0;
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				int br = (int) (values[x][y] * 64 + 128);
				pixels[i] = br << 16 | br << 8 | br;
				i ++;
			}
		}
		img.setRGB(0, 0, width, height, pixels, 0, width);
		
		return img;
	}
	
	public BufferedImage getMassAsImage(){
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		int[] pixels = new int[width * height];
		
		int i = 0;
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				if(-values[x][y] * 2 > 0.7) pixels[i] = 0xff000000;
				else{
					int br = (int) (values[x][y] * 64 + 128);
					pixels[i] = br << 16 | br << 8 | br;
				}
				i ++;
			}
		}
		
		img.setRGB(0, 0, width, height, pixels, 0, width);
		
		return img;
	}
	
	private void factorToAverage(){
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				double value = values[x][y];
				value = (0.5 - value);
				value = r.nextDouble() * value + value;
				values[x][y] = value;
			}
		}
	}
	
	public double[][] getMassAsArray(){
		width = this.width - 1;
		height = this.height - 1;
		double[][] pixels = new double[width][height];
		
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				double value = Math.abs(values[x][y]);
				if(value > 0.95) pixels[x][y] = 0;
				if(value == 0.0) pixels[x][y] = value + 0.1;
				else pixels[x][y] = value;
			}
		}
		
		return pixels;
	}
	
	public double[][] getHeightAsArray(){
		width = this.width - 1;
		height = this.height - 1;
		double[][] pixels = new double[width][height];
		
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				double value = Math.abs(values[x][y]);
				if(value == 0.0) pixels[x][y] = value + 0.1;
				else pixels[x][y] = value;
			}
		}
		
		return pixels;
	}
	
}
