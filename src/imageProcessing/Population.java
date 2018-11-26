package imageProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javafx.scene.paint.Color;


public class Population{
    private ArrayList<Individual> population;
    final static int ELITISM_SIZE = 5;
    final static int POP_SIZE = 100;
    private double totalFitness;
    
    public Population() {
    	population = new ArrayList<Individual>();
    	for(int i=0; i<POP_SIZE; i++) {
    		Individual indiv = new Individual(Individual.SIZE);
    		population.add(indiv);
    	}
    	this.evaluate();
    }
    
    public void setPopulation(ArrayList<Individual> p) {
    	for(int i=0; i<POP_SIZE; i++) {
    		this.population.set(i, p.get(i));
    	}
    	evaluate();
    }
    
    public ArrayList<Individual> getPopulation(){
    	return this.population;
    }
    
    public double evaluate() {
    	this.totalFitness = 0.0;
    	for(Individual i : this.population) {
    		this.totalFitness += i.evaluate();
    	}
    	return this.totalFitness;    	
    }
    
    /**
     * Gets the best individual of the population 
     * @return the best individual
     */
    public Individual getBestIndividual() {
    	ArrayList<Individual> copy = new ArrayList<Individual>(population);
    	Collections.sort(copy,new IndividualCompare());
    	return copy.get(0); // minimisation
    	//return copy.get(population.size()-1); //maximisation
    }
	
    /**
     * @param n
     * @return the n best individual of the population
     */
    public ArrayList<Individual> elitism(int n){
    	ArrayList<Individual> selected = new ArrayList<Individual>();
    	ArrayList<Individual> copy = new ArrayList<Individual>(population);
    	Collections.sort(copy,new IndividualCompare());
    	for(int i=0; i<n; i++) {
    		selected.add(copy.get(i));
    	}
    	return selected;
    }
    
    public Individual tournamentSelection(int tournamentSize) {
    	Individual winner=null;;
    	Random r = new Random();
    	for(int i=0; i<tournamentSize; i++) {
        	int rand = r.nextInt(POP_SIZE);
    		Individual ind = population.get(rand);
    		if(winner == null || ind.getFitness() < winner.getFitness()) {
    			winner = ind;
    		}
    	}
    	return winner;
    }
    
}
