package imageProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javafx.scene.paint.Color;


public class Population{
    private ArrayList<Individual> population;
    private Color[][] target;
    private Individual bestIndividual;
    
    public Population(Color[][] target) {
    	this.target = target;
    	population = new ArrayList<Individual>();
    	for(int i=0; i<100; i++) {
    		Random r = new Random();
    		int random = 1 + r.nextInt(50);
    		population.add(new Individual(random,target));
    	}
    	setBestIndividual();
    }
    
    public ArrayList<Individual> getPopulation(){
    	return this.population;
    }
    
    public void setBestIndividual() {
    	Collections.sort(population,new IndividualCompare());
    	this.bestIndividual = population.get(0);
    }
    
    public Individual getBestIndividual() {
    	return this.bestIndividual;
    }
    
    public Individual crossover(Individual father, Individual mother){
    	ArrayList<ConvexPolygon> l = new ArrayList<ConvexPolygon>();
    	for(int i=0;i<50;i++) {
    		if(i<25) {
    			l.add(father.getGenome().get(i));
    		}else {
    			l.add(mother.getGenome().get(i));
    		}
    	}
    	Individual son = new Individual(l,target);
    	return son;
    }
    
    public ArrayList<Individual> selection(){
    	ArrayList<Individual> selected = new ArrayList<Individual>();
    	
    	// We add the 20 best individuals in the selected list and remove them from the sorted list
    	for(int i=0; i<20; i++) {
    		selected.add(population.get(i));
    		population.remove(i);
    	}
    	
    	// We add up 5 more individuals from the sorted list but with the same chance for everyone to be picked
    	int added = 0;
    	while(added != 5) {
	    	for(Individual i : population) {
	    		Random r = new Random();
	            int random = r.nextInt(100);
	            if(random == 50) {
	            	selected.add(i);
	            	added++;
	            }
	            if(added == 5) {
	            	break;
	            }
	    	}
    	}    	
    	// Now we add up a crossover between all the remaining individuals in the population
    	// TODO
    	
    	return selected;
    }
}
