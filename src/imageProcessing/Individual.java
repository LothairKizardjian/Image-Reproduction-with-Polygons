package imageProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class Individual{
	public static final int SIZE = 50;
    private ArrayList<ConvexPolygon> genome;
    private double fitness;
    
    public Individual() {
    	this.genome = new ArrayList<ConvexPolygon>();
    }
    
    public Individual(int n){        
        genome=new ArrayList<ConvexPolygon>();
        for(int i=0; i<n; i++) {
        	genome.add(new ConvexPolygon(ConvexPolygon.maxEdges));
        }
        evaluate();
    }
    
    public Individual(List<ConvexPolygon> g) {
        genome=new ArrayList<ConvexPolygon>();
    	for(ConvexPolygon p : g) {
    		genome.add(p);
    	}
    	evaluate();
    }

	public List<ConvexPolygon> getGenome() {
        return genome;
    }
    
    public void setGenome(List<ConvexPolygon> genome) {
    	for(ConvexPolygon p : genome) {
    		genome.add(p);
    	}
    }
   
    public double getFitness() {
    	return this.fitness;
    }

   
    /**
     * computes the Indiviual's fitness according to the target parameter. In this case it will be the average of the euclidian distance 
     * for each of the pixels between the target and itself.
     * @param target
     * @return the fitness of the individual
     */
    public double evaluate() {
    	Group image = new Group();
    	Color[][] target = GeneticAlgorithm.target;
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
		
		res = Math.sqrt(res);
		fitness = res;
		return res;
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
     * Translate the (x,y) point of the polygon by a random value
     * @param polygon
     * @param x
     * @param y
     */
    public void pointTranslation(ConvexPolygon polygon) {
    	boolean canTranslate = true;
    	Color[][] target = GeneticAlgorithm.target;
		int maxX = target.length;
		int maxY = target[0].length;
    	Random rand = new Random();
		Point p = polygon.pointList.get(rand.nextInt(polygon.pointList.size()));
		int translationFactorX;
		int translationFactorY;
    	do {
			translationFactorX = rand.nextInt(maxX);
			translationFactorY = rand.nextInt(maxY);
			if(p.x + translationFactorX > maxX || p.x - translationFactorX < 0 || p.y + translationFactorY > maxY || p.y - translationFactorY < 0) {
				canTranslate = false;
			}else {
				canTranslate = true;
			}
    	}while(!canTranslate);
    	if(rand.nextDouble() < 0.5) {
    		if(rand.nextDouble() < 0.5) {
    			p.x+=translationFactorX;
    			p.y-=translationFactorY;
    		}else {
    			p.x-=translationFactorX;
    			p.y+=translationFactorY;
    		}
    	}else{
    		if(rand.nextDouble() < 0.5) {
    			p.x+=translationFactorX;
    			p.y+=translationFactorY;
    		}else {
    			p.x-=translationFactorX;
    			p.y-=translationFactorY;
    		}    		
    	}
    }
    
    /**
     * The polygon can have one of its vertex removed, or a new one added or a complete translation or a translation of only one of its vertex
     * @param polygon
     */
    public void shapeMutation(ConvexPolygon polygon) {
    	pointTranslation(polygon);
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
    		if(genome.size() > 1) {
    			genome.remove(polygon);
    		}
    	}else {
    		/*
    		 * Adding a new polygon
    		 */
    		if(genome.size()<50) {
    			int randomEdges = 3 + rand.nextInt(ConvexPolygon.maxEdges);
    			genome.add(new ConvexPolygon(randomEdges));
    		}
    	}
    }

    /**
     * If this function is called the Individual will undergo a certain mutation. The mutation can be the following :
     * 1- new random color for a random polygon
     * 2- one chance out of two to add or remove a vertex for a random polygon
     * 3- one chance out of two to add a new random polygon or remove one
     */    
    public void mutation() {	
    	
    	if(genome.size()>0) {
	    	Random rand = new Random();
	    	double mutationChance = rand.nextDouble();
	        int randomPolygon = rand.nextInt(genome.size());
	        
	        /*
	         * We randomly choose the gene that will mutate
	         */
	        ConvexPolygon chosen = genome.get(randomPolygon);
	        
	        /*
	         * 3 random mutation can occur    
	         */
	        if(mutationChance < 0.3) {
	    		colorMutation(chosen); 	
	        }else{
	        	shapeMutation(chosen); 	
	        }
    	}
    }
}
