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
/*
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(RAND.nextFloat() < 0.001f){
					try {
						cells[i][j].b = 1;
						cells[i+1][j].b = 1;
						cells[i-1][j].b = 1;
						cells[i][j+1].b = 1;
						cells[i][j-1].b = 1;
						cells[i+1][j+1].b = 1;
						cells[i+1][j-1].b = 1;
						cells[i-1][j+1].b = 1;
						cells[i-1][j-1].b = 1;
					} catch(IndexOutOfBoundsException e){
						cells[i][j].b = 1;
					}
				}
			}
		}*/
	}
	
	public void copyFrom(Grid other){
		//System.arraycopy(other.cells, 0, cells, 0, cells.length);

		for(int i = 0; i < cells.length; i++){
			for(int j = 0; j < cells[i].length; j++){
				cells[i][j].a = other.cells[i][j].a;
				cells[i][j].b = other.cells[i][j].b;
				cells[i][j].col.r = other.cells[i][j].col.r;
				cells[i][j].col.g = other.cells[i][j].col.g;
				cells[i][j].col.b = other.cells[i][j].col.b;
			}
		}
	}
}
