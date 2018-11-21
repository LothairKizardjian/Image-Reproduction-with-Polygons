package imageProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class Individual{
    private ArrayList<ConvexPolygon> genome;
    private double fitness;
    
    public Individual() {
    	this.genome = new ArrayList<ConvexPolygon>();
    }
    
    public Individual(int n){        
        genome=new ArrayList<ConvexPolygon>();
        Random r = new Random();
        int random = 3 + r.nextInt(ConvexPolygon.maxEdges);
        for(int i=0; i<n; i++) {
        	genome.add(new ConvexPolygon(random));
        }
    }
    
    public Individual(ArrayList<ConvexPolygon> genome,Color[][] target) {
    	this.genome = new ArrayList<ConvexPolygon>(genome);
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
    
    /**
     * computes the Indiviual's fitness according to the target parameter. In this case it will be the average of the euclidian distance 
     * for each of the pixels between the target and itself.
     * @param target
     */
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
    

    /**
     * If this function is called the Individual will undergo a certain mutation. The mutation can be the following :
     * 1- new random color for a random polygon
     * 2- one chance out of two to add or remove a vertex for a random polygon
     * 3- one chance out of two to add a new random polygon or remove one
     */
    public void mutation() {
    	Random rand = new Random();
        int mutationChance = rand.nextInt(100);
        if(genome.size() <= 0)
        		System.out.println(genome.size());
        int randomPolygon = rand.nextInt(genome.size());
        
        /*
         * We randomly choose the gene that will mutate
         */
        ConvexPolygon chosen = genome.get(randomPolygon);
        
        /*
         * 3 random mutation can occur    
         */
        if(mutationChance <= 33) {
        	/*
        	 * Color mutation
        	 */
    		Random gen = new Random();
			int r = gen.nextInt(256);
			int g = gen.nextInt(256);
			int b = gen.nextInt(256); 
			chosen.setOpacity(gen.nextDouble());
			chosen.setFill(Color.rgb(r, g, b));
        }else if(33 < mutationChance && mutationChance <= 66){
        	/*
        	 * Shape mutation
        	 */
        	int random3 = rand.nextInt(2);
        	if(random3 == 1) {
        		/*
        		 * Adding vertex
        		 */
        		if(chosen.verteces < 20) {
        			chosen.genRandomConvexPolygone(chosen.verteces+1);
        		}
        	}else {
        		/*
        		 * Removing vertex
        		 */
        		chosen.genRandomConvexPolygone(chosen.verteces-1);
        	}        	
        }else {
        	/*
        	 * Add/Remove mutation
        	 */
        	int random4 = rand.nextInt(2);   
        	if(random4 == 1) {
        		/*
        		 * Removing the polygon
        		 */
        		if(genome.size() > 5) {
        			genome.remove(chosen);
        		}
        	}else {
        		/*
        		 * Adding a new polygon
        		 */
        		if(genome.size()<50) {
        			int random5 = 3 + rand.nextInt(ConvexPolygon.maxEdges);
        			genome.add(new ConvexPolygon(random5));
        		}
        	}
        	
        }
    }
}
