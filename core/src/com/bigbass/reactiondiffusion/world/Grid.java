package com.bigbass.reactiondiffusion.world;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;

public class Grid {
	
	public static final Random RAND = new Random();

	public Vector2 pos;
	
	public Cell[][] cells;
	
	public Grid(float x, float y, int width, int height){
		
		pos = new Vector2(x, y);
		
		Vector2 tmp = new Vector2(width * 0.5f, height * 0.5f);
		
		cells = new Cell[width][height];
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				cells[i][j] = new Cell();
				
				
				/*if(i >= 200 && i <= 250 && j >= 200 && j <= 250){
					cells[i][j].b = 1;
				}*/
				
				tmp.set(width * 0.5f, height * 0.5f);
				if(tmp.dst(i, j) < 3){
					cells[i][j].b = 1;
				}

				
			}
		}
	}
}
