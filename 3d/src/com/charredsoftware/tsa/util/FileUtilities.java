package com.charredsoftware.tsa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import com.charredsoftware.tsa.CrashReport;

/**
 * FileUtilities class.
 * Used to simplify and unify File manipulation.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since November 29, 2014
 */

public class FileUtilities {

	public static String dataPath = "data/"; //Relative to saves
	public static String texturesPath = "textures/";
	public static String soundsPath = "sounds/";
	public static String savesPath = "res/saves/";
	public static String crashesPath = "res/crashes/";
	public static String themesPath = "themes/";
	public static String settingsFile = "settings.csf";
	
	/**
	 * @param path Path of directory.
	 * @return Returns an <code>ArrayList</code> of String of names of directories in path.
	 */
	public static ArrayList<String> getChildDirectoriesAsString(String path){
		ArrayList<String> list = new ArrayList<String>();
		for(String s : getEntries(path)){
			File f = new File(path, s);
			if(f.exists() && f.isDirectory()) list.add(s);
		}
		
		return list;
	}
	
	/**
	 * @param path Path of directory.
	 * @return Returns an <code>ArrayList</code> of <code>File</code> of directories in path.
	 */
	public static ArrayList<File> getChildDirectories(String path){
		ArrayList<File> list = new ArrayList<File>();
		for(String s : getEntries(path)){
			File f = new File(path, s);
			if(f.exists() && f.isDirectory()) list.add(f);
		}
		
		return list;
	}
	
	/**
	 * @param path Path of directory
	 * @return Returns all of the children in a path.
	 */
	public static String[] getEntries(String path){
		return (new File(path)).list();
	}
	
	/**
	 * Copies a file src to a destination dest.
	 * @param src Source file
	 * @param dest Destination file.
	 */
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
