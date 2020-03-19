package com.bigbass.reactiondiffusion.world;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;

public class Grid {
	
	public static final RandomXS128 RAND = new RandomXS128();

	public Vector2 pos;
	
	public Cell[][] cells;
	
	public Grid(float x, float y, int width, int height){
		pos = new Vector2(x, y);
		
		//Vector2 tmp = new Vector2(width * 0.5f, height * 0.5f);
		
		cells = new Cell[width][height];
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				cells[i][j] = new Cell();
				
				/*tmp.set(width * 0.5f, height * 0.5f);
				if(tmp.dst(i, j) < 3){
					cells[i][j].b = 1;
				}*/
				
				if(i != 0 && j != 0 && i != width - 1 && j != height - 1){
					if((RAND.nextInt() & 0x1FF) < 3){
						cells[i][j].b = 1 - RAND.nextFloat() * 0.25f;
					}
				}
			}
		}
	}
}
