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
	
	public GeneticAlgorithm(double mutChance, double crossoverR, int maxGNb, double fThreshold, Color[][] t) {
		this.maxGenerationNumber = maxGNb;
		this.acceptableFitnessThreshold = fThreshold;
		mutationChance = mutChance;
		crossoverRate = crossoverR;
		target = t;
	}
	
	/**
	 * @param father
	 * @param mother
	 * @return a two offSprings with a combination of the father's and the mother's genome
	 */
    public ArrayList<Individual> crossover(Individual father, Individual mother) {
    	ArrayList<Individual> offSprings = new ArrayList<Individual>();
    	offSprings.add(new Individual());
    	offSprings.add(new Individual());
    	
    	if(new Random().nextDouble() < 0.5) {
	    	int crossoverPoint = Individual.SIZE / 2;
	    	for(int i = 0; i<Individual.SIZE; i++) {
	    		if(i <crossoverPoint) {
	    			offSprings.get(0).getGenome().add(father.getGenome().get(i));
	    			offSprings.get(1).getGenome().add(mother.getGenome().get(i));
	    		}else {
	    			offSprings.get(1).getGenome().add(father.getGenome().get(i));
	    			offSprings.get(0).getGenome().add(mother.getGenome().get(i));
	    		}
	    	}
    	}else {
    		for(int i = 0; i<Individual.SIZE; i++) {
	    		if(i%2 == 0) {
	    			offSprings.get(0).getGenome().add(father.getGenome().get(i));
	    			offSprings.get(1).getGenome().add(mother.getGenome().get(i));
	    		}else {
	    			offSprings.get(1).getGenome().add(father.getGenome().get(i));
	    			offSprings.get(0).getGenome().add(mother.getGenome().get(i));
	    		}
	    	}
    	}
    	offSprings.get(0).evaluate();
    	offSprings.get(1).evaluate();
    	return offSprings;
    }
    
	
	/**
	 * Runs the genetic algorithm over the population given
	 * @return the best individual generated
	 */
	public Individual run() {
		Random gen = new Random();
		Population pop = new Population();
		Individual bestIndividual = pop.getBestIndividual();
		
		
		

		// main loop
        
        for(int i=0; i<maxGenerationNumber; i++) {
        	Random gen2 = new Random();
        	for(Individual indivi : pop.getPopulation()) {
        		int count = 0;
        		for(Individual i2 : pop.getPopulation()) {
        			if(indivi.equals( i2)) {
        				count++;
        			}
        		}
        		System.out.println(--count);
        	}
            
    		ArrayList<Individual> newPop = new ArrayList<Individual>();
            System.out.println("Best Fitness = " + bestIndividual.getFitness());
    		
        	int count;

        	/*
        	 * we add the ELITISM_SIZE best individuals to the pop 
        	 */
        	ArrayList<Individual> elit = pop.elitism(Population.ELITISM_SIZE);
        	for(count=0; count<Population.ELITISM_SIZE; count++) {
        		Individual ind = elit.get(count);
        		if(gen2.nextDouble() < mutationChance) {
        			ind.mutation();
        		}
        		newPop.add(ind);
        	}
        	
        	/*
        	 * We now indivual in the new pop with roulette wheel selection
        	 */
        	while(count < Population.POP_SIZE) {
        		int tournamentSize = 2;

            	Random gen3 = new Random();
            	Random gen4 = new Random();
            	
        		if(gen3.nextDouble() < crossoverRate) {
            		Individual father = pop.tournamentSelection(tournamentSize);
            		Individual mother = pop.tournamentSelection(tournamentSize);
            		ArrayList<Individual> indivToAdd = crossover(father,mother);
            		
            		if(gen4.nextDouble() < mutationChance) {
            			indivToAdd.get(0).mutation();
            			indivToAdd.get(1).mutation();
            		}
            		newPop.add(indivToAdd.get(0));
            		newPop.add(indivToAdd.get(1));            		
        		}else {
        			Individual ind = pop.tournamentSelection(tournamentSize);
        			if(gen4.nextDouble() < mutationChance) {
            			ind.mutation();
            		}
        			newPop.add(ind);
        		}        		
        		count++;
        	}
        	pop.setPopulation(newPop);
        	pop.evaluate();
        	bestIndividual = pop.getBestIndividual();
        	Test.createResult(bestIndividual, target.length, target[0].length,"result");
        	
        }
		return bestIndividual;
	}
}
