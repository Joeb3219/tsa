package com.charredsoftware.tsa.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.tsa.CrashReport;

public class Loader {

	public static Model load(File file){
		Model m = new Model();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = reader.readLine()) != null){
				if(line.startsWith("v ")){
					float x = Float.valueOf(line.split(" ")[1]);
					float y = Float.valueOf(line.split(" ")[2]);
					float z = Float.valueOf(line.split(" ")[3]);
					m.vertices.add(new Vector3f(x, y, z));
				}else if(line.startsWith("vn ")){
					float x = Float.valueOf(line.split(" ")[1]);
					float y = Float.valueOf(line.split(" ")[2]);
					float z = Float.valueOf(line.split(" ")[3]);
					m.normals.add(new Vector3f(x, y, z));
				}else if(line.startsWith("f ")){
					Vector3f vInd = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[0]), Float.valueOf(line.split(" ")[2].split("/")[0]), Float.valueOf(line.split(" ")[3].split("/")[0]));
					Vector3f nInd = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[2]), Float.valueOf(line.split(" ")[2].split("/")[2]), Float.valueOf(line.split(" ")[3].split("/")[2]));
					m.faces.add(new Face(vInd, nInd));
				}
			}
		}catch(Exception e){new CrashReport(e);}
		return m;
	}
	
}
