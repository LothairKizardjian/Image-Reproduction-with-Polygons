package imageProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javafx.scene.paint.Color;


public class Population{
    private ArrayList<Individual> population;
    private Color[][] target;
    
    public Population() {
    	this.population = new ArrayList<Individual>();
    }
    
    public Population(Color[][] target) {
    	this.target = target;
    	population = new ArrayList<Individual>();
    	for(int i=0; i<100; i++) {
    		Random r = new Random();
    		int random = 5 + r.nextInt(45);
    		Individual indiv = new Individual(random);
    		indiv.setFitness(target);
    		population.add(indiv);
    	}
    }
    
    public Population(Population p) {
    	this.target = p.getTarget();
    	this.population = new ArrayList<Individual>(p.getPopulation());
    }
    
    public void setPopulation(ArrayList<Individual> p) {
    	this.population = p;
    }
    
    public ArrayList<Individual> getPopulation(){
    	return this.population;
    }
    
    public Color[][] getTarget(){
    	return this.target;
    }
    
    public void setTarget(Color[][] t){
    	this.target = t;
    }
    
    public void setBestIndividual() {
    }
    
    public Individual getBestIndividual() {
    	ArrayList<Individual> copy = new ArrayList<Individual>(population);
    	Collections.sort(copy,new IndividualCompare());
    	return copy.get(0);
    }
    
}
