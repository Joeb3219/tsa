package com.charredsoftware.tsa.world;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.entity.Player;

public class Region {

	public static final float _SIZE = 16, _HEIGHT = 128;
	public ArrayList<BlockInstance> blocks = new ArrayList<BlockInstance>();
	public float x, z;
	public World world;
	
	public Region(World world, float x, float z){
		this.world = world;
		this.x = x;
		this.z = z;
	}
	public void save(File dir){
		File file = new File(dir.getAbsolutePath() + "/region_" + x + "l" + z + ".csf");
		try {
			if(!file.exists()) file.createNewFile();
			
			PrintWriter writer = new PrintWriter(file, "UTF-8");

			writer.println("<Blocks>");
			
			for(BlockInstance b : blocks){
				writer.println("<block>");
				writer.println("<position>" + b.x + ":" + b.y + ":" + b.z + "</position>");
				writer.println("<data>" + b.base.id + ":" + b.base.meta + ":" + b.special + "</data>");
				writer.println("<json>" + b.getSpecialJson() + "</json>");
				writer.println("</block>");
			}
			
			writer.println("<block>");
			writer.println("<position>" + x + ":-1000:" + z + "</position>");
			writer.println("<data>0:0:0</data>");
			writer.println("</block>");
			
			writer.println("</Blocks>");
			
			writer.close();
		} catch (IOException e) {new CrashReport(e);}
		
	}
	
	public boolean playerInRegion(){
		int px = ((int) Main.getInstance().player.x) / ((int) _SIZE);
		int pz = ((int) Main.getInstance().player.z) / ((int) _SIZE);
		if(x == -px && z == -pz) return true;
		return false;
	}
	
	public ArrayList<BlockInstance> getFrustumTestBlocks(float y){
		ArrayList<BlockInstance> testBlocks = new ArrayList<BlockInstance>();
		float baseX = x * _SIZE;
		float baseZ = z * _SIZE;
		for(float x = 0; x < _SIZE; x += 2){
			for(float z = 0; z < _SIZE; z += 2){
				float fX = baseX + x;
				float fZ = baseZ + z;
				testBlocks.add(getBlock(new Position(fX, y, fZ)));
			}
		}
		
		return testBlocks;
	}
	
	public BlockInstance getBlock(Position p){
		p.normalizeCoords();
		for(BlockInstance b : blocks){
			if(b.x == p.x && b.y == p.y && b.z == p.z) return b;
		}
		return new BlockInstance(Block.air, p.x, p.y, p.z);
	}
	
	public void generate(File file){
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			NodeList base = d.getElementsByTagName("block");
			for(int i = 0; i < base.getLength() - 1; i ++){
				Node n = base.item(i);
				NodeList children = n.getChildNodes();
				float bx = 0, by = 0, bz = 0, bspecial = 0;
				String json = "";
				Block bbase = Block.air;
				for(int l = 0; l < children.getLength() - 1; l ++){
					Node c = children.item(l);
					String value = c.getTextContent();
					if(value.equals("") || value.equals(" ")) continue;
					if(c.getNodeName().equals("position")){
						bx = Float.parseFloat(value.split(":")[0]);
						by = Float.parseFloat(value.split(":")[1]);
						bz = Float.parseFloat(value.split(":")[2]);
					}else if(c.getNodeName().equals("data")){
						bspecial = Float.parseFloat(value.split(":")[2]);
						bbase = Block.getBlock(value.split(":")[0] + ":" + value.split(":")[1]);
					}else if(c.getNodeName().equals("json")){
						json = value;
					}
				}
				BlockInstance b = new BlockInstance(bbase, bx, by, bz);
				b.special = bspecial;
				b.initJson = json;
				addBlock(b);
				
			}
			
		} catch (IOException e) {new CrashReport(e);} catch (SAXException e) {
			new CrashReport(e);
		} catch (ParserConfigurationException e) {
			new CrashReport(e);
		}
	}
	
	public void generate(){
		for(float x = 0; x < 16; x ++){
			for(float z = 0; z < 16; z ++){
				for(float y = 0; y < 3; y ++){
					addBlock(new BlockInstance(Block.grass, this.x * _SIZE + x, y, this.z * _SIZE + z));
				}
			}
		}
	}
	
	public void addBlock(BlockInstance block){
		for(int i = 0; i < blocks.size() - 1; i ++){
			BlockInstance b = blocks.get(i);
			if(b.x == block.x && b.y == block.y && b.z == block.z){
				if(b.base != Block.air) return;
			}
		}
		
		blocks.add(block);
	}

	public void removeBlock(BlockInstance block){
		if(blocks.contains(block)){
			blocks.remove(block);
		}
	}

	public float blocksChecked = 0;
	
	public ArrayList<BlockInstance> getRenderableBlocks(){
		ArrayList<BlockInstance> renderableBlocks = new ArrayList<BlockInstance>();
		
		blocksChecked = 0;
		
		if(!playerInRegion() && !Main.getInstance().camera.frustum.regionInFrustum(this)) return renderableBlocks;
		
		for(BlockInstance b : blocks){
			blocksChecked ++;
			if(!Main.getInstance().camera.frustum.BlockInFrustum(b)) continue;
			if(!checkIfCovered(b)) continue;
			renderableBlocks.add(b);
		}
		return renderableBlocks;
	}
	
	private boolean checkIfCovered(BlockInstance b){
		Player player = Main.getInstance().player;
		if(world.getBlockWithoutNewRegion(new Position(b.x, b.y + 1, b.z)).base == Block.air && player.y + player.height > b.y) return true;
		if(world.getBlockWithoutNewRegion(new Position(b.x, b.y - 1, b.z)).base == Block.air && player.y + player.height < b.y) return true;
		if(world.getBlockWithoutNewRegion(new Position(b.x + 1, b.y, b.z)).base == Block.air && player.x > b.x) return true;
		if(world.getBlockWithoutNewRegion(new Position(b.x - 1, b.y, b.z)).base == Block.air && player.x < b.x) return true;
		if(world.getBlockWithoutNewRegion(new Position(b.x, b.y, b.z + 1)).base == Block.air && player.z > b.z) return true;
		if(world.getBlockWithoutNewRegion(new Position(b.x, b.y, b.z - 1)).base == Block.air && player.z < b.z) return true;
		
		return false;
	}
	
	public Position getPosition(){
		return new Position(x, 0, z);
	}
	
	public String toString(){
		return "[x/z]: {" + x + "/" + z + "}";
	}
	
}
