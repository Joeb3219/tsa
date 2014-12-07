package com.charredsoftware.tsa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import com.charredsoftware.tsa.CrashReport;

public class FileUtilities {

	public static String computersPath = "data/computers/"; //Relative to saves
	public static String dataPath = "data/"; //Relative to saves
	public static String defaultsPath = "res/default/";
	public static String defaultProgramsPath = defaultsPath + "/scripts/";
	public static String texturesPath = "textures/";
	public static String soundsPath = "sounds/";
	public static String savesPath = "res/saves/";
	public static String crashesPath = "res/crashes/";
	public static File scriptTempFile = null;
	
	static{
		try {
			scriptTempFile = File.createTempFile("script_temp_csf", null);
			scriptTempFile.deleteOnExit();
		} catch (IOException e) {new CrashReport(e);}
	}
	
	public static ArrayList<String> getChildDirectoriesAsString(String path){
		ArrayList<String> list = new ArrayList<String>();
		for(String s : getEntries(path)){
			File f = new File(path, s);
			if(f.exists() && f.isDirectory()) list.add(s);
		}
		
		return list;
	}
	
	public static ArrayList<File> getChildDirectories(String path){
		ArrayList<File> list = new ArrayList<File>();
		for(String s : getEntries(path)){
			File f = new File(path, s);
			if(f.exists() && f.isDirectory()) list.add(f);
		}
		
		return list;
	}
	
	public static String[] getEntries(String path){
		return (new File(path)).list();
	}
	
	public static void copyFile(File src, File dest){
		try {
			dest.createNewFile();
			FileChannel inputChannel = null;
			FileChannel outputChannel = null;
			try {
				inputChannel = new FileInputStream(src).getChannel();
				outputChannel = new FileOutputStream(dest).getChannel();
				outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			} finally {
				inputChannel.close();
				outputChannel.close();
			}
		} catch (IOException e) {new CrashReport(e);}
	}
	
}
