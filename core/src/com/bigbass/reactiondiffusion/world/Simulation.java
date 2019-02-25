package com.bigbass.reactiondiffusion.world;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import io.anuke.gif.GifRecorder;

public class Simulation {
	
	private Grid gridActive;
	private Grid gridTemp;
	
	private float dA = 1.0f;
	private float dB = 0.5f;
	private float feed = 0.0545f;
	private float kill = 0.062f;
	
	private int stepsPerFrame = 8; // Decrease if FPS is too low. Controls the number of generations per frame
	
	private float adj = 0.2f;
	private float diag = 0.05f;
	
	private int generations = 0;
	
	private boolean isRendering = false;
	
	private SpriteBatch gifBatch;
	private GifRecorder gifRecorder;
	
	private ForkJoinPool pool;
	
	public Simulation(){
		gridActive = new Grid(150, 50, 512, 512);
		gridTemp = new Grid(150, 50, 512, 512);
		
		gifBatch = new SpriteBatch();
		gifRecorder = new GifRecorder(gifBatch);
		gifRecorder.open();
		gifRecorder.setBounds(150 - 400, 50 - 300, 512, 512);
		gifRecorder.setFPS(40);
		//gifRecorder.startRecording(); // Remove this to try recording
		
		pool = ForkJoinPool.commonPool();
	}
	
	public void updateAndRender(ShapeRenderer sr){
		isRendering = !Gdx.input.isKeyPressed(Keys.SPACE); // Disable rendering by holding SPACE
		
		if(isRendering){
			sr.begin(ShapeType.Line);
		}
		for(int z = 0; z < stepsPerFrame; z++){
			
			UpdateWorld wu = new UpdateWorld(gridTemp, gridActive, 1, gridTemp.cells.length - 1, 1, gridTemp.cells[1].length - 1);
			pool.execute(wu);
			wu.join();
			
			if(isRendering && z == stepsPerFrame - 1){
				for (int i = 1; i < gridTemp.cells.length - 1; i++) {
					for (int j = 1; j < gridTemp.cells[i].length; j++) {
						Cell t = gridTemp.cells[i][j];
	
						sr.setColor(t.red, t.green, t.blue, 1);
						sr.point(gridActive.pos.x + i, gridActive.pos.y + j, 0);
					}
				}
			}
			
			// Copy gridTemp data into gridActive
			Grid swap = gridActive;
			gridActive = gridTemp;
			gridTemp = swap;
			
			generations += 1;
		}
		
		if(isRendering){
			sr.end();
			
			if(gifRecorder.isRecording() && generations % 50 < stepsPerFrame){ // record new frame every n generations
				//gifRecorder.setFPS(Gdx.graphics.getFramesPerSecond() < 15 ? 15 : Gdx.graphics.getFramesPerSecond() - 5);
				gifRecorder.update();
			}
		}
		
		if(gifRecorder.isRecording() && Gdx.input.isKeyPressed(Keys.P)){
			gifRecorder.finishRecording();
			gifRecorder.writeGIF();
		}
	}
	
	public float laplacianA(int x, int y){
		return -gridActive.cells[x][y].a + 
				(gridActive.cells[x - 1][y].a + gridActive.cells[x + 1][y].a +  gridActive.cells[x][y - 1].a + gridActive.cells[x][y + 1].a) * adj +
				(gridActive.cells[x - 1][y - 1].a + gridActive.cells[x - 1][y + 1].a + gridActive.cells[x + 1][y - 1].a + gridActive.cells[x + 1][y + 1].a) * diag;
	}
	
	public float laplacianB(int x, int y){
		return -gridActive.cells[x][y].b + 
				(gridActive.cells[x - 1][y].b + gridActive.cells[x + 1][y].b +  gridActive.cells[x][y - 1].b + gridActive.cells[x][y + 1].b) * adj +
				(gridActive.cells[x - 1][y - 1].b + gridActive.cells[x - 1][y + 1].b + gridActive.cells[x + 1][y - 1].b + gridActive.cells[x + 1][y + 1].b) * diag;
	}
	
	public int getGenerations(){
		return generations;
	}
	
	public void dispose(){
		gifRecorder.finishRecording();
		gifRecorder.close();
		gifBatch.dispose();
	}
	
	
	@SuppressWarnings("serial")
	private class UpdateWorld extends RecursiveAction {
		private int threshold = 1000;
		private Grid tmp;
		private Grid active;
		private int startx;
		private int endx;
		private int starty;
		private int endy;

		public UpdateWorld(Grid tmp, Grid active, int startx, int endx, int starty, int endy) {
			this.tmp = tmp;
			this.active = active;
			this.startx = startx;
			this.endx = endx;
			this.starty = starty;
			this.endy = endy;
		}

		public void compute() {
			int work = (endx - startx) * (endy - starty);
			if (work > threshold) {
				int xdiff = (endx - startx) / 2;
				int ydiff = (endy - starty) / 2;

				UpdateWorld uwA = new UpdateWorld(tmp, active, startx, startx + xdiff, starty, starty + ydiff);
				UpdateWorld uwB = new UpdateWorld(tmp, active, startx + xdiff, endx, starty, starty + ydiff);
				UpdateWorld uwC = new UpdateWorld(tmp, active, startx, startx + xdiff, starty + ydiff, endy);
				UpdateWorld uwD = new UpdateWorld(tmp, active, startx + xdiff, endx, starty + ydiff, endy);

				invokeAll(uwA, uwB, uwC, uwD);
			} else {
				for (int i = startx; i < endx; i++) {
					for (int j = starty; j < endy; j++) {
						Cell c = active.cells[i][j];
						Cell t = tmp.cells[i][j];

						final float abb = (c.a * c.b * c.b);
						t.a = c.a + (dA * laplacianA(i, j)) - abb + (feed * (1 - c.a));
						t.b = c.b + (dB * laplacianB(i, j)) + abb - ((kill + feed) * c.b);
						
						// Clamp chemical A
						if(t.a > 1){
							t.a = 1;
						} else if(t.a < 0){
							t.a = 0;
						}
						
						// Clamp chemical B
						if(t.b > 1){
							t.b = 1;
						} else if(t.b < 0){
							t.b = 0;
						}

						t.updateColor();
					}
				}
			}
		}
	}
}
