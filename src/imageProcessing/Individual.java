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
    private String name;
    
    public Individual() {
    	this.genome = new ArrayList<ConvexPolygon>();
    }
    
    public Individual(int n,String s,String format){  
    	name = s;
        genome=new ArrayList<ConvexPolygon>();
        Random rand = new Random();
        for(int i=0; i<n; i++) {
			int randomEdges = 3 + rand.nextInt(ConvexPolygon.maxEdges);
        	genome.add(new ConvexPolygon(ConvexPolygon.maxEdges,format));
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
		fitness = res;
		return res;
    }
    
    
    /**
     * Checks if the color C can change by delta amount (i.e it's 0 < c+delta > 255)
     * @param c
     * @param delta
     * @return
     */
    public boolean canChangeColor(int c, int delta) {
    	if(c+delta < 0 || c+delta > 255) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * Change the color c of the polygon by a percentage (delta)
     * If the color c is the Red value : c = 144 for example and delta = 0.1
     * then the new value of c is 144 + 144*0.1 = 144 + 14.4 = 144 + 14 = 158
     * @param polygon
     * @param c
     * @param delta
     */
    public void changeColor(ConvexPolygon polygon, int c, double delta) {
    	int colorVal = polygon.colors[c];
    	int deltaColor = (int) (colorVal * delta);
    	if(canChangeColor(colorVal,deltaColor)) {
    		polygon.colors[c] += delta;
    	}else if(canChangeColor(colorVal,-deltaColor)) {
    		polygon.colors[c] -= delta;
    	}
    }
    
    /**
     * Checks if the opacity can change by delta amount
     * @param opacity
     * @param delta
     * @return
     */
    public boolean canChangeOpacity(double opacity, double delta) {
    	if(opacity + delta < 0 || opacity + delta > 1) {
    		// an opacity of 0 is stupid
    		return false;
    	}
    	return true;
    }
    
    /**
     * Change the opacity of the polygon by a percentage (delta)
     * If the opacity is 0.84 and delta is 0.1 then the new value
     * of the opacity is 0.84 + 0.84 * 0.1 = 0.84 + 0.084 = 0.924
     * @param polygon
     * @param delta
     */
    public void changeOpacity(ConvexPolygon polygon, double delta) {
    	double deltaOp = polygon.opacity * delta;
    	if(canChangeOpacity(polygon.opacity,deltaOp)) {
    		polygon.opacity += deltaOp;
    	}else {
    		polygon.opacity -= deltaOp;
    	}
    }
    
    /**
     * Checks if the value x can change by delta
     * it must be between 0 and max
     * @param max
     * @param x
     * @param delta
     * @return
     */
    public boolean canChangeValue(double max, double x, double delta) {
    	if(x+delta < 0 || x+delta > max) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * Change the value of a vertex of the polygon by a percentage (delta) 
     * of its value.
     * @param polygon
     * @param max
     * @param v
     * @param delta
     */
    public void changeVertex(ConvexPolygon polygon,double max, int v, double delta) {
    	double valV = polygon.getPoints().get(v);
    	double deltaV = valV * delta;
    	if(canChangeValue(max,v,deltaV)) {
    		polygon.getPoints().set(v, valV+deltaV);
    	}else {
    		polygon.getPoints().set(v, valV-deltaV);
    	}
    }
    
    /**
     * Changes a parameter (R,G,B,Opacity,X,Y) of a random polygon by a small delta
     */
    public void softMutation(double d) {    
    	Random gen = new Random();
    	double delta = gen.nextDouble() * d;
    	int polygonID = gen.nextInt(genome.size());
    	ConvexPolygon polygon = genome.get(polygonID);
    	if(gen.nextDouble() < 0.3) {
        	/*
        	 * Color mutation
        	 */
        	int color = gen.nextInt(3);
        	changeColor(polygon,color,delta);
    	}else if(gen.nextDouble() > 0.3 && gen.nextDouble() <= 0.6) {
        	/*
        	 * Opacity mutation
        	 */
    		changeOpacity(polygon,delta);
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
    			changeVertex(polygon,maxX,x,delta);
    		}else {
    			double maxY = GeneticAlgorithm.target[0].length;
    			changeVertex(polygon,maxY,y,delta);    			
    		}
    	}
    	polygon.commitChanges();
    }
    
    /**
     * Changes a parameter (R,G,B,Opacity,X,Y) of a random polygon by a random value
     */
    public void mediumMutation() {
    	Random gen = new Random();
    	int polygonID = gen.nextInt(genome.size());
    	ConvexPolygon polygon = genome.get(polygonID);
    	if(gen.nextDouble() <= 0.333) {
        	/*
        	 * Color mutation
        	 */
        	int color = gen.nextInt(3);
        	int newVal = gen.nextInt(256);
        	polygon.colors[color] = newVal;
    	}else if(gen.nextDouble() > 0.333 && gen.nextDouble() <= 0.666) {
        	/*
        	 * Opacity mutation
        	 */
        	double newVal = gen.nextDouble();
        	polygon.opacity = newVal;
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
    			double newX = gen.nextDouble() * maxX;
    			polygon.getPoints().set(x, newX);
    		}else {
    			double maxY = GeneticAlgorithm.target[0].length;
    			double newY = gen.nextDouble() * maxY;
    			polygon.getPoints().set(y, newY);    			
    		}
    	} 
    	polygon.commitChanges();
    }
    
    /**
     * Changes the a color(R,G or B), opacity and one vertex to a random value
     */
    public void hardMutation() {
    	Random gen = new Random();
    	int polygonID = gen.nextInt(genome.size());
    	ConvexPolygon polygon = genome.get(polygonID);
    	
    	if(gen.nextDouble() <= 0.5) {
	       	/*
	       	 * Color and Opacity mutation
	       	 */
	       	int color = gen.nextInt(3);
	       	int newVal = gen.nextInt(256);
	       	double newOpacity = gen.nextDouble();
	       	polygon.colors[color] = newVal;
	       	polygon.opacity = newOpacity;
    	}else {
	    	/*
	       	 * Vertex mutation
	       	 */
	   		int x,y;
	    	do {
	    		x = gen.nextInt(polygon.getPoints().size());
	   		}while(x%2 != 0);
	    	y = x+1;
			double maxX = GeneticAlgorithm.target.length;
			double newX = gen.nextDouble() * maxX;
			polygon.getPoints().set(x, newX);
			double maxY = GeneticAlgorithm.target[0].length;
			double newY = gen.nextDouble() * maxY;
			polygon.getPoints().set(y, newY);    	
    	}
    	polygon.commitChanges();	
    }
    
    public void run() {
    	Individual bestIndividual = this;
		Test.createResult(bestIndividual, GeneticAlgorithm.target.length, GeneticAlgorithm.target[0].length,getName()+"startingImage");
		// main loop
        for(int i=0; i<GeneticAlgorithm.maxGenerationNumber && bestIndividual.getFitness() > GeneticAlgorithm.acceptableFitnessThreshold; i++) {
        	
        	
            Individual copy = new Individual(bestIndividual.getGenome());
            double copyFitness = copy.getFitness();
            if(copyFitness > 15000) {
            	copy.hardMutation();
            }else if(copyFitness > 5000) {
            	copy.mediumMutation(); 
            }else if(copyFitness > 4000){
            	copy.softMutation(0.5);
            }else if(copyFitness > 3000){
            	copy.softMutation(0.25);
            }else if(copyFitness > 2000){
            	copy.softMutation(0.125);
            }else if(copyFitness > 1000){
            	copy.softMutation(0.0625);
            }else if(copyFitness > 500){
            	copy.softMutation(0.03125);
            }else {
            	copy.softMutation(0.15625);
            }
            copy.evaluate();
            
            if(copy.getFitness() < bestIndividual.getFitness()) {
            	bestIndividual = copy;
                System.out.println(getName()+" fitness =  " + bestIndividual.getFitness());
                Test.createResult(bestIndividual, GeneticAlgorithm.target.length, GeneticAlgorithm.target[0].length,getName()+"bestSoFar");
            }
        	
        }
    }
}
