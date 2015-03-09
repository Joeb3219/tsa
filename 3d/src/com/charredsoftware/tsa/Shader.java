package com.charredsoftware.tsa;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.BufferedReader;
import java.io.FileReader;

import org.lwjgl.input.Keyboard;

import com.charredsoftware.tsa.util.FileUtilities;

/**
 * Shader class.
 * Creates a shader which shades the environment.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since February 10, 2015
 */

public class Shader {

	public int programId, vertexShaderId, fragmentShaderId;
	
	/**
	 * Creates a new Shader
	 */
	public Shader(){
		programId = glCreateProgram();
		vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
		fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
		String vsSource = "";
		String fsSource = "";
		
		try {
			BufferedReader vertexReader = new BufferedReader(new FileReader(FileUtilities.getBaseDirectory() + "res/" + FileUtilities.shadersPath + "vertexShader.csf"));
			String line = "";
			while ((line = vertexReader.readLine()) != null) {
				vsSource += line + System.getProperty("line.separator");
			}
			vertexReader.close();
		} catch (Exception e) {new CrashReport(e);}
		
		try {
			BufferedReader fragmentReader = new BufferedReader(new FileReader(FileUtilities.getBaseDirectory() + "res/" + FileUtilities.shadersPath + "fragmentShader.csf"));
			String line = "";
			while ((line = fragmentReader.readLine()) != null) {
				fsSource += line + System.getProperty("line.separator");
			}
			fragmentReader.close();
		} catch (Exception e) {new CrashReport(e);}
		
		glShaderSource(vertexShaderId, vsSource);
		glCompileShader(vertexShaderId);

		glShaderSource(fragmentShaderId, fsSource);
		glCompileShader(fragmentShaderId);

		glAttachShader(programId, vertexShaderId);
		glAttachShader(programId, fragmentShaderId);
		glLinkProgram(programId);
		
		glDeleteShader(vertexShaderId);
		glDeleteShader(fragmentShaderId);
	}
	
	/**
	 * Renders the shader
	 */
	public void renderShader(){
		if(Keyboard.isKeyDown(Keyboard.KEY_0)) return;
		glUseProgram(programId);
	}
	
	/**
	 * Closes the shader at the end of the render loop.
	 */
	public void closeShader(){
		glUseProgram(0);
	}
	
	/**
	 * Deletes shader from the memory.
	 */
	public void cleanUp(){
		glDeleteShader(vertexShaderId);
		glDeleteShader(fragmentShaderId);
	}
	
}
