package com.charredsoftware.three.computer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.charredsoftware.three.CrashReport;

public class Script {

	public File dir;
	public String name;
	public ArrayList<String> lines = new ArrayList<String>();
	
	public Script(File dir, String name){
		this.dir = dir;
		this.name = name;
	}
	
	public void saveScript(ArrayList<String> lines){
		this.lines = lines;
		
		File file = new File(dir, name);
		
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			
			for(String s : lines){
				writer.println(s);
			}
			
			writer.close();
		} catch (Exception e){new CrashReport(e);}
	}
	
	public void loadScript(){
		File f = new File(dir, name);
		try{
			if(!f.exists()) f.createNewFile();
		}catch(Exception e){new CrashReport(e);}
		try{
			BufferedReader br = new BufferedReader(new FileReader(f));
			for(String line; (line = br.readLine()) != null; ) {
			        lines.add(line);
			}
			
			br.close();
		}catch(Exception e){new CrashReport(e);}
	}
	
}
