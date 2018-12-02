package imageProcessing;

import java.util.List;

import javafx.scene.paint.Color;

public class GeneticAlgorithm {
	
	public static Color[][] target;
	public static int maxGenerationNumber;
	public static double acceptableFitnessThreshold;
	public static Individual indiv;
	
	public GeneticAlgorithm(int maxGNb, double fThreshold, Color[][] t) {
		maxGenerationNumber = maxGNb;
		acceptableFitnessThreshold = fThreshold;
		target = t;
		indiv = null;
	}
	
	public GeneticAlgorithm(List<ConvexPolygon> gen,int maxGNb, double fThreshold, Color[][] t) {
		maxGenerationNumber = maxGNb;
		acceptableFitnessThreshold = fThreshold;
		target = t;
		indiv = new Individual(gen);
	}
	
	/**
	 * @return the best individual generated
	 */
	public Individual run() {		
		/*
		 * the String format determine the color of the random polygons that will be generated
		 * It should be either :
		 * "color" : all the polygons will have random (R,G,B )
		 * "black" : all the polygons will have (R=0,G=0,B=0)
		 * "white" : all the polygons will have (R=255,G=255,B=255)
		 */
		if(indiv == null) {
			String format = "black";	
			boolean isConvex = false;
			indiv = new Individual(1000,"T1",isConvex,format);
		}
		indiv.run();
		Individual bestIndividual=indiv;
		return bestIndividual;
	}
}
