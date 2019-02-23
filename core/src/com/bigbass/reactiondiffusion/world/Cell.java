package com.bigbass.reactiondiffusion.world;

import com.badlogic.gdx.graphics.Color;

public class Cell {
	
	public float a;
	public float b;
	
	public Color col;
	
	public Cell(){
		a = 1;
		b = 0;
		
		col = new Color(0, 0, 0, 1);
		updateColor();
	}
	
	public void updateColor(){
		float val = a - b;
		
		if(val < 0){
			val = 0;
		} else if(val > 1){
			val = 1;
		}

		col.r = val;
		col.g = val;
		col.b = val;
	}
}
