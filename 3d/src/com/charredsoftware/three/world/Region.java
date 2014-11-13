package com.charredsoftware.three.world;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.charredsoftware.three.Main;
import com.charredsoftware.three.computer.Computer;
import com.charredsoftware.three.computer.Peripheral;

public class Region {

	public static final float _SIZE = 16, _HEIGHT = 128;
	public ArrayList<BlockInstance> blocks = new ArrayList<BlockInstance>();
	public ArrayList<Peripheral> peripherals = new ArrayList<Peripheral>();
	public float x, z;
	public float renderedBlocks = 0f; //Used to calculate # of blocks rendered per render.
	
	public Region(float x, float z){
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
				writer.println("</block>");
			}
			
			writer.println("</Blocks>");
			
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void generate(File file){
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			NodeList base = d.getElementsByTagName("block");
			for(int i = 0; i < base.getLength() - 1; i ++){
				Node n = base.item(i);
				NodeList children = n.getChildNodes();
				float bx = 0, by = 0, bz = 0, bspecial = 0;
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
					}
				}
				BlockInstance b = new BlockInstance(bbase, bx, by, bz);
				b.special = bspecial;
				addBlock(b);
				
			}
			
		} catch (IOException e) {e.printStackTrace();} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void generate(){
		if(x == 0 && z == 0){
			for(float x = 0 / 2; x < _SIZE - 1; x ++ ){
				for(float y = 0; y < 24; y ++){
					addBlock(new BlockInstance(Block.bricks, x, y, _SIZE - 1));
					addBlock(new BlockInstance(Block.bricks, x, y, 0));
				}
			}
			
			for(float z = 0 / 2; z < _SIZE - 1; z ++){
				for(float y = 0; y < 24; y ++){
					addBlock(new BlockInstance(Block.bricks, _SIZE - 1, y, z));
					addBlock(new BlockInstance(Block.bricks, 0, y, z));
				}
			}
			
			for(float x = 0 / 2; x < _SIZE - 1; x ++){
				for(float z = 0; z < _SIZE - 1; z ++){
					addBlock(new BlockInstance(Block.grass, x, -1.0f, z));
					if(!(x == 0 && z == 0)) addBlock(new BlockInstance(Block.ceiling, x, 24, z));
				}
			}
			
			for(float x = 6; x < 12; x ++){
				for(float z = 6; z < 12; z ++){
					for(float y = 0; y < 3; y ++){
						addBlock(new BlockInstance(Block.bricks, x, y, z));
					}
				}
			}
			
			addBlock(new BlockInstance(Block.boost, 10, 3, 10));
			
			addBlock(new BlockInstance(Block.wood, 4, 0, 6));
			addBlock(new BlockInstance(Block.wood, 4, 1, 8));
			addBlock(new BlockInstance(Block.wood, 4, 2, 10));
			addBlock(new BlockInstance(Block.wood, 4, 0, 8));
			addBlock(new BlockInstance(Block.wood, 4, 0, 10));
			addBlock(new BlockInstance(Block.wood, 4, 1, 10));
			
			addBlock(new BlockInstance(Block.computer, 8, 3, 8));
			
			return;
		
		}
		
		Random r = new Random();
		for(float y = -2f; y <= -1f; y ++){
			for(float x = 0; x < _SIZE; x ++){
				for(float z = 0; z < _SIZE; z ++){
					float fX = this.x * _SIZE + x;
					float fZ = this.z * _SIZE + z;
					if(y == -2) addBlock(new BlockInstance(Block.grass, fX, y, fZ));
					else{
						if(r.nextInt(2) == 1) addBlock(new BlockInstance(Block.grass, fX, y, fZ));
						else if(r.nextInt(2) == 1) addBlock(new BlockInstance(Block.bricks, fX, y, fZ));
						else if(r.nextInt(2) == 1) addBlock(new BlockInstance(Block.boost, fX, y, fZ));
						else if(r.nextInt(2) == 1) addBlock(new BlockInstance(Block.ceiling, fX, y, fZ));
						else if(r.nextInt(2) == 1) addBlock(new BlockInstance(Block.wood, fX, y, fZ));
						//else if(r.nextInt(5) == 1) world.addBlock(new BlockInstance(Block.grass, x, y, z));
						else{} //Air
					}
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
		
		if(block.base == Block.computer) addPeripheral(new Computer(block.x, block.y, block.z, block.special));
		
		blocks.add(block);
	}

	public void removeBlock(BlockInstance block){
		if(blocks.contains(block)){
			blocks.remove(block);
			removePeripheral(new Peripheral(block.x, block.y, block.z, block.special));
		}
	}

	public void addPeripheral(Peripheral peripheral){
		if(peripherals.size() == 0) peripherals.add(peripheral);
		for(int i = 0; i < peripherals.size() - 1; i ++){
			Peripheral p = peripherals.get(i);
			if(p.x == peripheral.x && p.y == peripheral.y && p.z == peripheral.z){
				return;
			}
		}
		
		peripherals.add(peripheral);
	}
	
	public void removePeripheral(Peripheral p){
		if(peripherals.contains(p)) peripherals.remove(p);
	}
	
	public void render(){
		renderedBlocks = 0f;
		
		for(BlockInstance b : blocks){
			if(!Main.camera.frustum.BlockInFrustum(b)) continue;
			renderedBlocks ++;
			if(b == Main.world.lookingAt) b.draw(100);
			else b.draw();
		}
	}
	
	public String toString(){
		return "[x/z]: {" + x + "/" + z + "}";
	}
	
}