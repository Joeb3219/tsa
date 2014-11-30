package com.charredsoftware.three;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.lwjgl.Sys;

import com.charredsoftware.three.entity.Player;
import com.charredsoftware.three.util.FileUtilities;
import com.charredsoftware.three.world.Position;
import com.charredsoftware.three.world.World;

public class CrashReport {

	public File file;
	private Throwable t;
	public String synop = "";
	
	public CrashReport(Throwable t){
		this.t = t;
		file = new File(FileUtilities.crashesPath + System.currentTimeMillis() + ".txt");
		try {
			file.createNewFile();
			generateReport();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String lnBreak = System.getProperty("line.separator");
		Sys.alert(Main.getInstance().gameName + " " + Main.getInstance().version + ": Crash Report", Main.getInstance().gameName + " has crashed." + lnBreak + lnBreak + synop + lnBreak + lnBreak + "Refer to " + file.getAbsolutePath());
		Main.getInstance().cleanDisplay();
		
		System.exit(0);
	}
	
	private void generateReport(){
		try{
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			
			writer.println("Crash Report || " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
			writer.println("========");
			writer.println("");
			Player player = Main.getInstance().player;
			Camera camera = Main.getInstance().camera;
			World world = Main.getInstance().player.world;
			writer.println("[x/y/z]: {" + player.x + "/" + player.y + "/" + player.z + "} REGION: " + world.findRegion(player.x, player.z).toString() + " [currentJumpingVelocity] {" + player.currentJumpingVelocity + "}" + " isJumping: " + player.isJumping);
			writer.println("[rx/ry/rz]: {" + camera.rx + "/" + camera.ry + "/" + camera.rz + "} [cx/cy/cz]" + camera.x + "/" + camera.y + "/" + camera.z + "} yOffset: " + Main.getInstance().camera.yOffset);
			writer.println("Standing on : " + Main.getInstance().player.world.getBlock(-player.x, -player.y - 1, -player.z).base.name + " [highest rel. solid/roof]: {" + Main.getInstance().player.world.getRelativeHighestSolidBlock(new Position(-player.x, -player.y, -player.z)).base.name + "/" + Main.getInstance().player.world.getClosestSolidRoofBlock(new Position(-player.x, (-player.y + 2), -player.z)).base.name + "}");
			writer.println("Looking at " + world.lookingAt.base.name + " [" + world.lookingAt.x + ", " + world.lookingAt.y + ", " + world.lookingAt.z + "]");
			writer.println("fps: " + Main.getInstance().displayFPS + "; blocksRendered: " + world.renderedBlocks);
			
			writer.println("");
			writer.println("");
			
			if(t instanceof Exception){
				Exception e = ((Exception) t);
				synop += e.getMessage() + System.getProperty("line.separator");
				writer.println(e.getMessage());
				for(StackTraceElement s : e.getStackTrace()){
					synop += s.toString() + System.getProperty("line.separator");
					writer.println(s.toString());
				}
			}
			
			writer.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
}
