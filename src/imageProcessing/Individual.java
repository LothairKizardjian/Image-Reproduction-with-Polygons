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
    
    public Individual(int n,String s){  
    	name = s;
        genome=new ArrayList<ConvexPolygon>();
        Random rand = new Random();
        for(int i=0; i<n; i++) {
			int randomEdges = 3 + rand.nextInt(ConvexPolygon.maxEdges);
        	genome.add(new ConvexPolygon(ConvexPolygon.maxEdges));
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
		res = Math.sqrt(res);
		fitness = res;
		return res;
}
    
    /**
     * The polygon has one chance out of two to undergo either a color mutation
     * or an opacity mutation
     * @param polygon
     */
    public void polygonColorMutation(ConvexPolygon polygon) {
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
     * The random-number first polygons will undergo a color mutation
     */
    public void genomeColorMutation() {
    	Random gen = new Random();
    	int random = gen.nextInt(genome.size());
    	for(int i=0; i<random; i++) {
    		polygonColorMutation(genome.get(i));
    	}
    }
    
    /**
     * Checks if the point of the polygon of value (valX,valY) can translation by (translationX,translationY) within the (maxX * maxY) image
     * @param maxX
     * @param maxY
     * @param valX
     * @param valY
     * @param translationX
     * @param translationY
     * @return
     */
    public boolean canTranslatePoint(ConvexPolygon polygon,int maxX, int maxY,int x,int y, double translationX, double translationY) {
    	double valX = polygon.getPoints().get(x);
    	double valY = polygon.getPoints().get(y);
    	if(valX + translationX > maxX || valX - translationX < 0 
				|| valY + translationY > maxY || valY - translationY < 0) {
			return false;
    	}
    	return true;
    }
    
    /**
     * Checks if the whole polygon can translate by (translationX,translationY) value within the (maxX * maxY) image
     * @param polygon
     * @param maxX
     * @param maxY
     * @param translationX
     * @param translationY
     * @return
     */
    public boolean canTranslatePolygon(ConvexPolygon polygon, int maxX, int maxY, double translationX, double translationY) {
    	for(int i=0; i<polygon.getPoints().size()-1;i++) {
    		if(canTranslatePoint(polygon,maxX,maxY,i,i+1,translationX,translationY) == false) {
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * Translate the (x,y) point of the polygon by (translationX,translationY) value
     * @param polygon
     * @param x
     * @param y
     * @param translationX
     * @param translationY
     */
    public void pointTranslation(ConvexPolygon polygon,int maxX,int maxY,int x,int y,double translationX, double translationY) {	
		double valX = polygon.getPoints().get(x);
		double valY = polygon.getPoints().get(y);
		
		if(canTranslatePoint(polygon,maxX,maxY,x,y,translationX,translationY)) {
	    	valX+=translationX;
	    	valY+=translationY;
	    	polygon.getPoints().set(x, valX);
	    	polygon.getPoints().set(y, valY);
		}
    }
    
    /**
     * translate the whole polygon by a random value
     * @param polygon
     */
    public void polygonTranslation(ConvexPolygon polygon,int maxX,int maxY,double translationX, double translationY) {
		if(canTranslatePolygon(polygon,maxX,maxY,translationX,translationY)) {
			for(int i=0; i<polygon.getPoints().size()-1;i++) {
	    		pointTranslation(polygon,maxX,maxY,i,i+1,translationX,translationY);  		
	    	}		
		}
    }
    
    /**
     * The polygon can have one of its vertex removed, or a new one added or a translation of only one of its vertex
     * @param polygon
     */
    public void shapeMutation(ConvexPolygon polygon) {
    	Random rand = new Random();
    	Color[][] target = GeneticAlgorithm.target;
		int maxX = target.length;
		int maxY = target[0].length;
    	double translationX;
    	double translationY;
    	if(rand.nextDouble() <= 0.3) {
			int x,y;
			do {
				x = rand.nextInt(polygon.getPoints().size());
			}while(x%2 != 0);
			y = x+1;
			do {
				translationX = maxX * rand.nextDouble();
				translationY = maxY * rand.nextDouble();	
				if(rand.nextBoolean() == true) {
					translationX*= -1;
				}
				if(rand.nextBoolean() == true) {
					translationY*= -1;
				}
	    	}while(!canTranslatePoint(polygon,maxX,maxY,x,y,translationX,translationY));
	    	pointTranslation(polygon,maxX,maxY,x,y,translationX,translationY);
    	}else {
    		do {
				translationX = maxX * rand.nextDouble();
				translationY = maxY * rand.nextDouble();	
				if(rand.nextBoolean() == true) {
					translationX*= -1;
				}
				if(rand.nextBoolean() == true) {
					translationY*= -1;
				}
	    	}while(!canTranslatePolygon(polygon,maxX,maxY,translationX,translationY));
    		polygonTranslation(polygon,maxX,maxY,translationX,translationY);
    	}
    	//removeVertex(polygon);
    	//addVertex(polygon);
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
    			genome.add(new ConvexPolygon(ConvexPolygon.maxEdges));
    		}
    	}
    }
    
    /**
     * Add a new point
     * @param polygon
     */
    public void addVertexMutation(ConvexPolygon polygon) {
    	
    }

    /**
     * If this function is called the Individual will undergo a random mutation
     */    
    public void mutation() {	
    	
    	if(genome.size()>0) {
	    	Random rand = new Random();
	    	double mutationChance = rand.nextDouble();
	        
	        /*
	         * We randomly choose the gene that will mutate
	         */
	        int randomPolygon = rand.nextInt(genome.size());
	        ConvexPolygon chosen = genome.get(randomPolygon);
	        
	        if(mutationChance <= 0.3) {
	        	polygonColorMutation(chosen); 
	        }else if(mutationChance > 0.3 && mutationChance <= 0.6) {
	        	shapeMutation(chosen); 		
	        }else if(mutationChance > 0.6 && mutationChance <= 0.9){
	        	addRemoveMutation(chosen);
	        }else{
	    		genomeColorMutation(); 	
	        }
    	}
    }
    
    public void run() {
    	Individual bestIndividual = this;
		Test.createResult(bestIndividual, GeneticAlgorithm.target.length, GeneticAlgorithm.target[0].length,getName()+"startingImage");
		// main loop
        for(int i=0; i<GeneticAlgorithm.maxGenerationNumber && bestIndividual.getFitness() > GeneticAlgorithm.acceptableFitnessThreshold; i++) {
        	
        	
            Individual copy = new Individual(bestIndividual.getGenome());
            copy.mutation();
            copy.evaluate();
            if(copy.getFitness() < bestIndividual.getFitness()) {
            	bestIndividual = copy;
                System.out.println(getName()+" fitness =  " + bestIndividual.getFitness());
            	Test.createResult(bestIndividual, GeneticAlgorithm.target.length, GeneticAlgorithm.target[0].length,getName()+"result");
            }
        	
        }
    }
}
