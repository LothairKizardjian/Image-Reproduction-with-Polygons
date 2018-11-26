package imageProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javafx.scene.paint.Color;


public class Population{
    private ArrayList<Individual> population;
    static int ELITISM_SIZE = 5;
    static int POP_SIZE = 100;
    private double totalFitness;
    private double averageFitness;
    
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
    	this.averageFitness = totalFitness/POP_SIZE;
    	return this.totalFitness;    	
    }
    
    /**
     * Gets the best individual of the population 
     * @return the best individual
     */
    public Individual getBestIndividual() {
    	Individual bestIndiv = null;
    	for(Individual indiv : population) {
    		if(bestIndiv == null || bestIndiv.getFitness() > indiv.getFitness()) {
    			bestIndiv = indiv;
    		}
    	}
    	return bestIndiv;
    }
    
    /**
     * 
     * @return the worst individual of the population based on its fitness
     */
    public Individual getWorstIndividual() {
    	Individual worst = null;
    	for(Individual indiv : population) {
    		if(worst == null || worst.getFitness() < indiv.getFitness()) {
    			worst = indiv;
    		}
    	}
    	return worst;
    }
    
    
    /**
     * 
     * @param n
     * @return the n best individuals of the population
     */
    public ArrayList<Individual> elitism(int n){
    	ArrayList<Individual> selected = new ArrayList<>();
    	ArrayList<Individual> copy = new ArrayList<>();
    	for(Individual i : population) {
    		Individual tmpi = new Individual(i.getGenome());
    		copy.add(tmpi);
    	}
    	copy.sort(new IndividualCompare());
    	for(int i = 0; i<n; i++) {
    		selected.add(copy.get(i));
    	}
    	return selected;
    }
    
    /**
     * The tournament selection consists in randomly selecting an individual in the population and 
     * compare it to the best current individual. If its fitness is better he becomes the best individual. 
     * We do this tournamentSize times    
     * @param tournamentSize
     * @return the individual that won the tournament
     */
    public Individual tournamentSelection(int tournamentSize) {
    	Individual winner=null;;
    	Random r = new Random();
    	for(int i=0; i<tournamentSize; i++) {
        	int rand = r.nextInt(population.size());
    		Individual ind = population.get(rand);
    		if(winner == null || ind.getFitness() < winner.getFitness()) {
    			winner = ind;
    		}
    	}
    	return winner;
    }    
}
