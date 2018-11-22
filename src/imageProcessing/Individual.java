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
        for(int i=0; i<n; i++) {
        	genome.add(new ConvexPolygon(ConvexPolygon.maxEdges));
        }
        /*int random = 3 + r.nextInt(ConvexPolygon.maxEdges);
        for(int i=0; i<n; i++) {
        	genome.add(new ConvexPolygon(random));
        }*/
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
			if(!image.getChildren().contains(p)) {
				image.getChildren().add(p);
			}
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
		this.fitness = Math.sqrt(res);
    }
    
    /**
     * The polygon has one chance out of two to undergo either a color mutation
     * or an opacity mutation
     * @param polygon
     */
    public void colorMutation(ConvexPolygon polygon) {
    	Random gen = new Random();
    	int random = gen.nextInt(2);
    	if(random ==1) {
        	/*
        	 * Color mutation
        	 */
			int r = gen.nextInt(256);
			int g = gen.nextInt(256);
			int b = gen.nextInt(256); 
			polygon.setFill(Color.rgb(r, g, b));
    	}else {
    		/*
    		 * Opacity mutation
    		 */
			polygon.setOpacity(gen.nextDouble());        		
    	}
    }
    
    /**
     * The polygon has one chance out of two to get one more vertex or to get
     * one less vertex (if the conditions are verified)
     * @param polygon
     */
    public void shapeMutation(ConvexPolygon polygon,Color[][] target) {
    	Random rand = new Random();
    	int random = rand.nextInt(3);
    	if(random == 1) {
    		/*
    		 * Adding vertex
    		 */
    		if(polygon.verteces < ConvexPolygon.maxEdges) {
    			int x = rand.nextInt(target.length);
    			int y = rand.nextInt(target[0].length);
    			polygon.addPoint(x,y);
    			polygon.verteces++;
    		}
    	}else if(random == 2) {
    		/*
    		 * Removing vertex
    		 */
    		if(polygon.verteces > 3) {
    			double x = polygon.getPoints().get(0);
    			double y = polygon.getPoints().get(1);
    			polygon.getPoints().remove(x);
    			polygon.getPoints().remove(y);
    			polygon.verteces--;
    		}
    	}else {
    		/*
    		 * Translation mutation
    		 */
    		boolean canTranslate = true;
    		int maxX = target.length;
    		int maxY = target[0].length;
    		int translationFactor = rand.nextInt(Math.min(maxX,maxY));
    		for(double point : polygon.getPoints()) {
    			if(point%2 == 0) {
    				// This point correspond to a X coordinate
        			if(point+translationFactor > maxX || point-translationFactor < 0) {
        				canTranslate = false;
        			}
    			}else {
    				// This point correspond to a Y coordinate
    				if(point+translationFactor > maxY || point-translationFactor < 0) {
    					canTranslate = false;
    				}
    			}
    		}
    		if(canTranslate) {
    			int randomTranslation = rand.nextInt(2);
    			if(randomTranslation == 1) {
        			for(int i = 0; i<polygon.getPoints().size(); i++) {
        				double newPoint = polygon.getPoints().get(i) + translationFactor;
        				polygon.getPoints().set(i, newPoint);
        			}
    			}else {
    				for(int i = 0; i<polygon.getPoints().size(); i++) {
        				double newPoint = polygon.getPoints().get(i) - translationFactor;
        				polygon.getPoints().set(i, newPoint);
        			}
    			}
    		}
    	}
    }
    
    /**
     * There is one chance out of two to either add a new random polygon or to remove the
     * given polygon
     * @param polygon
     */
    public void addRemoveMutation(ConvexPolygon polygon) {
    	Random rand = new Random();
    	int random = rand.nextInt(2);   
    	if(random == 1) {
    		/*
    		 * Removing the polygon
    		 */
    		if(genome.size() > 5) {
    			genome.remove(polygon);
    		}
    	}else {
    		/*
    		 * Adding a new polygon
    		 */
    		if(genome.size()<50) {
    			int randomEdges = 3 + rand.nextInt(ConvexPolygon.maxEdges);
    			genome.add(new ConvexPolygon(ConvexPolygon.maxEdges));
    		}
    	}
    }

    /**
     * If this function is called the Individual will undergo a certain mutation. The mutation can be the following :
     * 1- new random color for a random polygon
     * 2- one chance out of two to add or remove a vertex for a random polygon
     * 3- one chance out of two to add a new random polygon or remove one
     */    
    public void mutation(Color[][] target) {
    	if(genome.size()>0) {
	    	Random rand = new Random();
	        int mutationChance = rand.nextInt(100);
	        int randomPolygon = rand.nextInt(genome.size());
	        
	        /*
	         * We randomly choose the gene that will mutate
	         */
	        ConvexPolygon chosen = genome.get(randomPolygon);
	        
	        /*
	         * 3 random mutation can occur    
	         */
	        if(mutationChance <= 33) {
	    		colorMutation(chosen);
	        }else if(33 < mutationChance && mutationChance <= 66){
	        	shapeMutation(chosen,target);  	
	        }else {
	        	addRemoveMutation(chosen);        	
	        }
    	}
    }
}
