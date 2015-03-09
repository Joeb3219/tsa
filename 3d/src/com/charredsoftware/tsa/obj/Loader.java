package com.charredsoftware.tsa.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.tsa.CrashReport;

/**
 * Loader class!
 * Static class that makes models and materials.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since January 24, 2015
 */

public class Loader {

	/**
	 * Creates a new model.
	 * @param file File to load the model from.
	 * @return Returns a new Model
	 */
	public static Model load(File file){
		Model m = new Model();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			Material currentMaterial = null;
			while((line = reader.readLine()) != null){
				if(line.startsWith("mtllib ")) m.materials = parseMaterials(new File(file.getParentFile(), line.split(" ")[1]));
				else if(line.startsWith("usemtl ")) currentMaterial = getMaterial(m, line.split(" ")[1]);
				else if(line.startsWith("v ")){
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
					m.faces.add(new Face(currentMaterial, vInd, nInd));
				}
			}
			reader.close();
		}catch(Exception e){new CrashReport(e);}
		return m;
	}
	
	/**
	 * @param m Model to search 
	 * @param name Material name to search for
	 * @return Returns the material with the given name in a model.
	 */
	private static Material getMaterial(Model m, String name){
		for(Material mat : m.materials){
			if(mat.name.equals(name)) return mat;
		}
		return null;
	}
	
	/**
	 * Creates an ArrayList of Materials from a file
	 * @param file File to create models from.
	 * @return Returns an ArrayList of Materials.
	 */
	private static ArrayList<Material> parseMaterials(File file){
		ArrayList<Material> materials = new ArrayList<Material>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			Material material = null;
			while((line = reader.readLine()) != null){
				if(line.startsWith("newmtl ")){
					if(material != null) materials.add(material);
					material = new Material(line.split(" ")[1]);
				}else if(line.startsWith("Ns ")) material.shininess = Float.valueOf(line.split(" ")[1]);
				else if(line.startsWith("Ka ")){
					float x = Float.valueOf(line.split(" ")[1]);
					float y = Float.valueOf(line.split(" ")[2]);
					float z = Float.valueOf(line.split(" ")[3]);
					material.ambient = Material.coordsToBuffer(x, y, z);
				}else if(line.startsWith("Kd ")){
					float x = Float.valueOf(line.split(" ")[1]);
					float y = Float.valueOf(line.split(" ")[2]);
					float z = Float.valueOf(line.split(" ")[3]);
					material.diffuse = new Vector3f(x, y, z);
				}else if(line.startsWith("Ks ")){
					float x = Float.valueOf(line.split(" ")[1]);
					float y = Float.valueOf(line.split(" ")[2]);
					float z = Float.valueOf(line.split(" ")[3]);
					material.specular = Material.coordsToBuffer(x, y, z);
				}else if(line.startsWith("d ") || line.startsWith("Tr ")) material.transparency = Float.valueOf(line.split(" ")[1]);
				else if(line.startsWith("illum ")) material.illum = Float.valueOf(line.split(" ")[1]);
			}
			materials.add(material);
			reader.close();
		}catch(Exception e){new CrashReport(e);}

		return materials;
	}
	
}
