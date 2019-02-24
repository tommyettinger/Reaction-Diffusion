package com.bigbass.reactiondiffusion.world;

public class Cell {
	
	private double garbage1;
	private double garbage2;
	private double garbage3;
	private double garbage4;
	private double garbage5;
	private double garbage6;
	private double garbage7;
	private double garbage8;
	private double garbage9;
	
	public float a;
	public float b;
	
	//public Color col;
	public float red;
	public float green;
	public float blue;
	
	public Cell(){
		garbage1 = 0;
		garbage2 = 0;
		garbage3 = 0;
		garbage4 = 0;
		garbage5 = 0;
		garbage6 = 0;
		garbage7 = 0;
		garbage8 = 0;
		garbage9 = 0;
		
		a = 1;
		b = 0;
		
		//col = new Color(0, 0, 0, 1);
		updateColor();
	}
	
	public void updateColor(){
		float val = a - b;
		
		if(val < 0){
			val = 0;
		} else if(val > 1){
			val = 1;
		}

		//col.r = val;
		//col.g = val;
		//col.b = val;
		red = val;
		green = val;
		blue = val;
	}
}
