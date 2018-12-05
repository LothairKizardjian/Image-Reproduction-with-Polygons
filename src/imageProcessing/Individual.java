package imageProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.time.Duration;
import java.time.Instant;
import javafx.scene.Group;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class Individual{
	public static final int SIZE = 50;
    private ArrayList<ConvexPolygon> genome;
    private double fitness;
    private String name;
    
    public Individual() {
    	this.genome = new ArrayList<ConvexPolygon>();
    }
    
    public Individual(int n,String s,boolean isConvex,String format){  
    	name = s;
        genome=new ArrayList<ConvexPolygon>();
        for(int i=0; i<n; i++) {
        	genome.add(new ConvexPolygon(5,isConvex,format));
        }
        evaluate();
    }
    
    public Individual(List<ConvexPolygon> gen) {    	
        genome=new ArrayList<ConvexPolygon>();
    	for(ConvexPolygon p : gen) {
    		genome.add(new ConvexPolygon(p));
    	}
    	evaluate();
    }
    
    public void setName(String s) {
    	name =s;
    }
    
    public String getName() {
    	return name;
    }

	public List<ConvexPolygon> getGenome() {
        return genome;
    }
    
    public void setGenome(List<ConvexPolygon> gen) {
    	this.genome = new ArrayList<ConvexPolygon>();
    	for(ConvexPolygon p : gen) {
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
		fitness = Math.sqrt(res);
		return res;
    }
    
    /**
     * @return true if we can add a new polygon to the genome
     */
    public boolean canAddPolygon() {
    	return this.getGenome().size()<50;
    }
    
    /**
     * Add a random new polygon to the genome
     */
    public void addNewRandomPolygon(){
    	Random gen = new Random();
    	int vertexNumber = 3+gen.nextInt(ConvexPolygon.maxNumPoints-3);
    	int formatId = gen.nextInt(3);
    	String format;
    	if(formatId == 0) {
    		format = "color";
    	}else if(formatId ==1) {
    		format = "white";
    	}else {
    		format = "black";
    	}
    	ConvexPolygon newPolygon = new ConvexPolygon(vertexNumber,false,format);
    	this.getGenome().add(newPolygon);
    }
    
    /**
     * @return if we can remove a polygon from the genome
     */
    public boolean canRemovePolygon() {
    	return this.getGenome().size()>0;
    }
    
    /**
     * Removes the given polygon from the genome
     * @param polygon
     */
    public void removePolygon(ConvexPolygon polygon) {
    	this.getGenome().remove(polygon);
    }
    
    /**
     * Swaps the position of 2 random polygon
     */
    public void swapTwoRandomPolygons() {
    	Random gen = new Random();
    	int id1,id2;
    	do {
    		id1 = gen.nextInt(genome.size());
    		id2 = gen.nextInt(genome.size());
    	}while(id1 == id2);
    	ConvexPolygon temp = genome.get(id1);
    	genome.set(id1, genome.get(id2));
    	genome.set(id2, temp);
    }
    
    /**
     * Changes a parameter (R,G,B,Opacity,X,Y) of a random polygon by a small delta
     */
    public void softMutation(double d) {   
    	Random gen = new Random();
    	double delta = gen.nextDouble() * d;
    	int polygonID = gen.nextInt(genome.size());
    	double mutationChoice = gen.nextDouble();
    	ConvexPolygon polygon = genome.get(polygonID);
    	if(mutationChoice < 0.3) {
        	/*
        	 * Color mutation
        	 */
        	int color = gen.nextInt(3);
        	polygon.changeColor(color,delta);
    	}else if(mutationChoice > 0.3 && mutationChoice <= 0.6) {
        	/*
        	 * Opacity mutation
        	 */
    		polygon.changeOpacity(delta);
    	}else {
    		/*
        	 * Vertex mutation
        	 */
    		int x,y;
    		do {
    			x = gen.nextInt(polygon.getPoints().size());
    		}while(x%2 != 0);
    		y = x+1;
    		if(gen.nextDouble() <= 0.5) {
    			double maxX = GeneticAlgorithm.target.length;
    			polygon.changeVertex(maxX,x,delta);
    		}else {
    			double maxY = GeneticAlgorithm.target[0].length;
    			polygon.changeVertex(maxY,y,delta);    			
    		}
    		polygon.convexify();
    	}
    	polygon.commitChanges();
    }
    
    /**
     * Changes a parameter (R,G,B,Opacity,X,Y) of a random polygon by a random value
     * Or swaps 2 random polygon in the list or translates a polygon
     */
    public void mediumMutation() {
    	Random gen = new Random();
    	int polygonID = gen.nextInt(genome.size());
    	double mutationChoice = gen.nextDouble();
    	ConvexPolygon polygon = genome.get(polygonID);
    	if(mutationChoice <= 0.20) {
        	/*
        	 * Color mutation
        	 */
        	int color = gen.nextInt(3);
        	int newVal = gen.nextInt(256);
        	polygon.colors[color] = newVal;
    	}else if(mutationChoice <= 0.40) {
        	/*
        	 * Opacity mutation
        	 */
        	double newVal = gen.nextDouble();
        	polygon.opacity = newVal;
    	}else if(mutationChoice <= 0.60){
    		/*
        	 * Vertex mutation
        	 */
    		double vertexMutation = gen.nextDouble();
    		int x,y;
    		do {
    			x = gen.nextInt(polygon.getPoints().size());
    		}while(x%2 != 0);
    		y = x+1;
    		if(vertexMutation <= 0.25) {
    			/*
    			 * Setting a x coordinate to a new random value
    			 */
    			int maxX = GeneticAlgorithm.target.length;
    			double newX;
    			newX = (double) gen.nextInt(maxX);    			
    			polygon.getPoints().set(x, newX);
    		}else if(vertexMutation <= 0.5) {
    			/*
    			 * Setting an y coordinate to a new random value
    			 */
    			int maxY = GeneticAlgorithm.target[0].length;
    			double newY;
    			newY = (double) gen.nextInt(maxY);
    			polygon.getPoints().set(y, newY); 	
    		}else if(vertexMutation <= 0.75) { 
    			polygon.addRandomVertex();
    		}else {
    			polygon.removeRandomVertex();
    		}
    		polygon.convexify();
    	}else if(mutationChoice <= 0.80){
    		swapTwoRandomPolygons();
    	}else {
    		polygon.translate();
    	}
    	polygon.commitChanges();
    }
    
    /**
     * Changes a parameter (R,G,B,Opacity,X,Y) of a random polygon by a random value
     * Not generating convex polygon
     */
    public void mediumMutation2() {
    	Random gen = new Random();
    	int polygonID = gen.nextInt(genome.size());
    	double mutationChoice = gen.nextDouble();
    	ConvexPolygon polygon = genome.get(polygonID);
    	if(mutationChoice <= 0.33) {
        	/*
        	 * Color mutation
        	 */
        	int color = gen.nextInt(3);
        	int newVal = gen.nextInt(256);
        	polygon.colors[color] = newVal;
    	}else if(mutationChoice <= 0.66) {
        	/*
        	 * Opacity mutation
        	 */
        	double newVal = gen.nextDouble();
        	polygon.opacity = newVal;
    	}else {
    		/*
        	 * Vertex mutation
        	 */
    		double vertexMutation = gen.nextDouble();
    		int x,y;
    		do {
    			x = gen.nextInt(polygon.getPoints().size());
    		}while(x%2 != 0);
    		y = x+1;
    		if(vertexMutation <= 0.5) {
    			/*
    			 * Setting a x coordinate to a new random value
    			 */
    			int maxX = GeneticAlgorithm.target.length;
    			double newX;
    			newX = (double) gen.nextInt(maxX);    			
    			polygon.getPoints().set(x, newX);
    		}else {
    			/*
    			 * Setting an y coordinate to a new random value
    			 */
    			int maxY = GeneticAlgorithm.target[0].length;
    			double newY;
    			newY = (double) gen.nextInt(maxY);
    			polygon.getPoints().set(y, newY); 
    		}
    	}
    	polygon.commitChanges();
    }
    
    /**
     * Changes the a color(R,G or B), opacity and one vertex to a random value
     * Or add/removes a random polygon or add/remove a vertex
     */
    public void hardMutation() {
    	Random gen = new Random();
    	double mutationChoice = gen.nextDouble();
    	int polygonID = gen.nextInt(genome.size());
    	ConvexPolygon polygon = genome.get(polygonID);
    	
    	if( mutationChoice <= 0.5) {
	       	/*
	       	 * Color and Opacity mutation
	       	 */
	       	int color = gen.nextInt(3);
	       	int newVal = gen.nextInt(256);
	       	double newOpacity = gen.nextDouble();
	       	polygon.colors[color] = newVal;
	       	polygon.opacity = newOpacity;
	    	/*
	       	 * Vertex mutation
	       	 */
	   		int x,y;
	    	do {
	    		x = gen.nextInt(polygon.getPoints().size());
	   		}while(x%2 != 0);
	    	y = x+1;
			double maxX = GeneticAlgorithm.target.length;
			double maxY = GeneticAlgorithm.target[0].length;
			double newX;
			double newY;
			newX= gen.nextDouble() * maxX;
			newY = gen.nextDouble() * maxY;
			polygon.getPoints().set(x, newX);
			polygon.getPoints().set(y, newY);  
			polygon.convexify();	
    	}else {
    		/*
    		 * Adding or removing a polygon
    		 */
    		double mut = gen.nextDouble();
    		if(mut <= 0.25) {
        		if(canAddPolygon()) {
        			addNewRandomPolygon();
        		}    			
    		}else if(mut <= 0.5){
    			if(canRemovePolygon()) {
    				removePolygon(polygon);
    			}
    		}else if(mut <= 0.75) {    			
    			polygon.addRandomVertex();
    		}else {
    			polygon.removeRandomVertex();
    		}
    	}
    	polygon.commitChanges();	
    }
    
    public void run() {
    	Instant startTime = Instant.now();
        long duration = 0;
    	int maxX = GeneticAlgorithm.target.length;
    	int maxY = GeneticAlgorithm.target[0].length;
    	Individual bestIndividual = this;
        System.out.println("Fitness =  " + bestIndividual.getFitness() +"");
        double lastFitness = this.getFitness();
		// main loop
        
        while(bestIndividual.getFitness() > GeneticAlgorithm.acceptableFitnessThreshold) {
            Instant currentTime = Instant.now();
            Duration timeElapsed = Duration.between(startTime, currentTime);
            if(timeElapsed.toMinutes() >= 1) {
            	System.out.println("time elpased since start : "+ duration +" minutes");
            	duration += timeElapsed.toMinutes();
            	startTime = currentTime;
            }
            
            Individual[] copies = new Individual[3];
            Individual best = bestIndividual;
            int copieNumber = 2;
            for(int j=0; j<copieNumber; j++) {
            	if(j==0) {
            		copies[j] = new Individual(bestIndividual.getGenome());
            	}else {
            		copies[j] = new Individual(copies[j-1].getGenome());
            	}
            	copies[j].mediumMutation();
            	copies[j].evaluate();
            	if(best.getFitness() > copies[j].getFitness()) {
            		best = copies[j];
            	}
            }
            
            if(best != bestIndividual) {
            	bestIndividual = best;
            	fitness = 100 * (1 - (bestIndividual.getFitness()/ (3*maxX*maxY)));
                System.out.println("Fitness =  " + bestIndividual.getFitness() +"");
            	Test.createResult(bestIndividual,maxX,maxY,Test.imgName+"_currentBest");	
            }
        }
    }
}
