package com.bigbass.reactiondiffusion.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import io.anuke.gif.GifRecorder;

public class Simulation {
	
	private Grid gridActive;
	private Grid gridTemp;
	
	private float dA = 0.9f;
	private float dB = 0.5f;
	private float feed = 0.035f;
	private float kill = 0.062f;
	
	private int stepsPerFrame = 3;
	
	private float adj = 0.2f;
	private float diag = 0.05f;
	
	private int generations = 0;
	
	private boolean isRendering = false;
	
	private SpriteBatch gifBatch;
	private GifRecorder gifRecorder;
	
	public Simulation(){
		gridActive = new Grid(150, 50, 512, 512);
		gridTemp = new Grid(150, 50, 512, 512);
		
		gifBatch = new SpriteBatch();
		gifRecorder = new GifRecorder(gifBatch);
		gifRecorder.open();
		gifRecorder.setBounds(150 - 400, 50 - 300, 512, 512);
		gifRecorder.setFPS(40);
		//gifRecorder.startRecording(); // Remove this to try recording
	}
	
	public void updateAndRender(ShapeRenderer sr){
		isRendering = (Gdx.input.isKeyPressed(Keys.SPACE)/* || generations % 50 == 0*/); // Remove this comment to only record one frame every n generation
		
		if(isRendering){
			sr.begin(ShapeType.Line);
		}
		for(int z = 0; z < stepsPerFrame; z++){
			
			// Reassign all A and B values in gridTemp, using data from gridActive
			for(int i = 1; i < gridTemp.cells.length - 1; i++){
				for(int j = 1; j < gridTemp.cells[i].length - 1; j++){
					Cell c = gridActive.cells[i][j], t = gridTemp.cells[i][j];
					
					final float abb = (c.a * c.b * c.b);
					t.a = c.a + (dA * laplacianA(i, j)) - abb + (feed * (1 - c.a));
					t.b = c.b + (dB * laplacianB(i, j)) + abb - ((kill + feed) * c.b);
					
					t.updateColor();
					
					if(isRendering){
						//sr.setColor(t.col);
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
			//gifRecorder.setFPS(Gdx.graphics.getFramesPerSecond() < 15 ? 15 : Gdx.graphics.getFramesPerSecond() - 5);
			gifRecorder.update();
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
}
