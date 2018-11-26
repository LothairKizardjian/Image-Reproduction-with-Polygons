package imageProcessing;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class GeneticAlgorithm {
	
	public static double mutationChance;
	public static double crossoverRate;
	public static Color[][] target;
	private int maxGenerationNumber;
	private double acceptableFitnessThreshold;
	private Individual bestIndividual;
	
	public GeneticAlgorithm(double mutChance, double crossoverR, int maxGNb, double fThreshold, Color[][] t) {
		this.maxGenerationNumber = maxGNb;
		this.acceptableFitnessThreshold = fThreshold;
		bestIndividual = null;
		mutationChance = mutChance;
		crossoverRate = crossoverR;
		target = t;
	}
	
	/**
	 * @param father
	 * @param mother
	 * @return a two offSprings with a combination of the father's and the mother's genome
	 */
    public Individual crossover(Individual father, Individual mother) {
    	Individual offSpring = new Individual();
	    for(int i = 0; i<Individual.SIZE; i++) {
	    	if(new Random().nextDouble() < 0.5) {
	    		offSpring.getGenome().add(father.getGenome().get(i));
	    	}else {
	    		offSpring.getGenome().add(mother.getGenome().get(i));
	    	}
	   	}
    	return offSpring;
    }
	
	/**
	 * Runs the genetic algorithm over the population given
	 * @return the best individual generated
	 */
	public Individual run() {
		Population pop = new Population();
		bestIndividual = pop.getBestIndividual();

		// main loop
        
        for(int i=0; i<maxGenerationNumber; i++) {
        	
            System.out.println("Best Fitness So Far = " + bestIndividual.getFitness());
            
        	Random gen = new Random();            
        	ArrayList<Individual> newPop = new ArrayList<>();
    		
            // Elitism
            ArrayList<Individual> elit = pop.elitism(5);
            for(Individual id : elit) {
            	Individual indtmp = new Individual(id.getGenome());
            	newPop.add(indtmp);
            }
            
        	while(newPop.size() < Population.POP_SIZE && bestIndividual.getFitness() > acceptableFitnessThreshold) {
        		int tournamentSize = 5;
            	
            	Individual father = pop.tournamentSelection(tournamentSize);
            	Individual mother = pop.tournamentSelection(tournamentSize);
            	Individual offSpring = pop.tournamentSelection(tournamentSize);
            	if(gen.nextDouble() < crossoverRate) {
            		offSpring = crossover(father,mother);
            	}
            	if(gen.nextDouble() < mutationChance) {
            		offSpring.mutation();
            	}
            	newPop.add(offSpring);
            	
        	}
        	pop.setPopulation(newPop);
        	pop.evaluate();
        	if(bestIndividual.getFitness() > pop.getBestIndividual().getFitness()) {
        		System.out.println("Current best fitness : "+bestIndividual.getFitness());
        		System.out.println("Potential better fitness : "+pop.getBestIndividual().getFitness());
        		bestIndividual = pop.getBestIndividual();
        	}  
        	  	
           
        	Test.createResult(bestIndividual, target.length, target[0].length,"result");
        	
        }
		return bestIndividual;
	}
}
