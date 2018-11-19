package imageProcessing;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Individual{
    private List<ConvexPolygon> genome;
    private int geneNumber;
    private double fitness;
    
    public Individual(ArrayList<ConvexPolygon> genome,Color[][] target) {
    	this.genome = new ArrayList<ConvexPolygon>(genome);
    	setFitness(target);
    }

    public Individual(int n,Color[][] target){
    	int maxEdges = 3;
        genome=new ArrayList<ConvexPolygon>();
        this.geneNumber = n;
        Random r = new Random();
        int random = r.nextInt(maxEdges);
        for(int i=0; i<n; i++) {
        	genome.add(new ConvexPolygon(maxEdges	));
        }
        setFitness(target);
    }
    
    public List<ConvexPolygon> getGenome() {
        return genome;
    }
    
    public void setGenome(List<ConvexPolygon> genome) {
    	this.genome = new ArrayList<ConvexPolygon>(genome);
    }
   
    public double getFitness() {
    	return this.fitness;
    }
    
    public void setFitness(Color[][] target){
		Group image = new Group();
		for(ConvexPolygon p : this.genome){
		    image.getChildren().add(p);
		}
	
		int x = target.length;
		int y = target[0].length;
		
	    WritableImage wimg = new WritableImage(x,y);
		image.snapshot(null,wimg);
		PixelReader pr = wimg.getPixelReader();
		double res = 0;
		for(int i=0; i<x; i++){
		    for(int j=0; j<y; j++){
			Color c = pr.getColor(i,j);
			res+= Math.pow(c.getBlue()-target[i][j].getBlue(),2)
			    + Math.pow(c.getRed()-target[i][j].getRed(),2)
			    + Math.pow(c.getGreen()-target[i][j].getGreen(),2);
		    }
		}	
		this.fitness = res;
    }
}
