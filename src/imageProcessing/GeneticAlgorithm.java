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
	
	private Population population;
	private int maxGenerationNumber;
	private double acceptableFitnessThreshold;
	private Color[][] target;
	
	public GeneticAlgorithm(Population pop, int maxGNb, double fThreshold,Color[][] target) {
		this.population = pop;
		this.maxGenerationNumber = maxGNb;
		this.acceptableFitnessThreshold = fThreshold;
		this.target = target;
	}
	
	public Color[][] getTarget(){
		return this.target;
	}
	
	/**
	 * Here we generate a new Individual that will result off the crossover between the first half of the father's genom and the second half
	 * of the mother's genom 
	 * @param father
	 * @param mother
	 * @return a new offSpring based on the father's and mother's genome
	 */
	public Individual singlePointCrossover(Individual father, Individual mother){
		
    	ArrayList<ConvexPolygon> l = new ArrayList<ConvexPolygon>();
    	ArrayList<ConvexPolygon> alreadySeen = new ArrayList<ConvexPolygon>();
    	int fatherSize = father.getGenome().size();
    	int motherSize = mother.getGenome().size();
    	int newGenomeSize = fatherSize/2 + motherSize/2;

    	int i = 0;
    	int j = 0;
    	while(i<newGenomeSize) {
    		if(i<fatherSize/2) {
        		ConvexPolygon f = father.getGenome().get(i);
    			if(!alreadySeen.contains(f)){
    				l.add(f);
    				alreadySeen.add(f);
    			}
    		}else {
        		ConvexPolygon m = mother.getGenome().get(j);
        		if(!alreadySeen.contains(m)){
        			l.add(m);
    				alreadySeen.add(m);
        			j++;
        		}
    		}
    		i++;
    	}
    	Individual offSpring = new Individual(l,target);
    	return offSpring;
    }
	
	public Individual uniformCrossover(Individual father,Individual mother) {
		ArrayList<ConvexPolygon> l = new ArrayList<ConvexPolygon>();
		int fatherSize = father.getGenome().size();
    	int motherSize = mother.getGenome().size();
    	int newGenomeSize = fatherSize/2 + motherSize/2;
		
    	Individual offSpring = new Individual(l,target);
    	return offSpring;
	}
	
	/**
	 * For the current population we take the n best individuals and we return them so that they can participate in the reproduction
	 * This n number is arbitrary
	 * @return the n best individuals of the population based on their fitness
	 */
	public ArrayList<Individual> elitism(int n){
		ArrayList<Individual> populationList = population.getPopulation();
		ArrayList<Individual> selected = new ArrayList<Individual>();
    	ArrayList<Individual> copy = new ArrayList<Individual>(populationList);
    	Collections.sort(copy,new IndividualCompare());
		for(int i=0; i<n; i++) {
			selected.add(copy.get(i));
		}
		return selected;
	}
    
	/**
	 * The higher a fitness of an individual the higher the probability to select him
	 * @param n the number of individuals we want to select
	 * @return a new population of the same size as the original one with individuals with a higher fitness
	 */
	public ArrayList<Individual> rouletteWheelSelection(int n){
		ArrayList<Individual> selected = new ArrayList<Individual>();
		ArrayList<Individual> populationList = population.getPopulation();
		
		double fitnessSum = 0;
		for(Individual indiv : populationList) {
			fitnessSum += indiv.getFitness();
		}

		for(Individual indiv : populationList) {
			Random rand = new Random();
			double alpha = (fitnessSum) * rand.nextDouble();
			double iSum = 0;
			int j = 0;
			do{
				iSum+=populationList.get(j).getFitness();
				j+=1;
			}while(iSum<alpha && j<n);
			selected.add(populationList.get(j));
		}		
		return selected;
	}
	
	public Individual selection() {
				
		Individual selected = new Individual();
		ArrayList<Individual> breeders = new ArrayList<Individual>();
		ArrayList<Individual> populationList = population.getPopulation();
		boolean satisfied = false;
		int currentGenerationNumber = 0;
		
		while(!satisfied && currentGenerationNumber < maxGenerationNumber) {
			System.out.println("Generation n°"+currentGenerationNumber);
			
			ArrayList<Individual> newPopulation = new ArrayList<Individual>();
			currentGenerationNumber++;
			/*
			 * I want to add one quarter of the best individuals of the total population's size to the breeders' list
			 */
			breeders.addAll(elitism((int)populationList.size()/2));
			/*
			 * Now I add the last 3/4 of the population using the Roulette Wheel Selection algorithm to the breeders' list
			 */
			breeders.addAll(rouletteWheelSelection((int)populationList.size()/2));
			/*
			 * Now we add the offSprings of the breeders in the new population. The breeders reproducts randomly
			 */
			while(newPopulation.size() < populationList.size()) {
				Random rand = new Random();
				int id1 = rand.nextInt(populationList.size());
				int id2 = rand.nextInt(populationList.size());
				Individual father = populationList.get(id1);
				Individual mother = populationList.get(id2);
				newPopulation.add(singlePointCrossover(father,mother));
			}
			
			/*
			 * A value of 10 means 1 chance out of 10 to undergo mutation
			 */
			int mutationChance = 10;
			
			/*
			 * Each individual has a 1 out of mutationChance chance to undergo mutation
			 */
			for(Individual indiv : newPopulation) {
				Random rand = new Random();
				int r = rand.nextInt(mutationChance);
				if(r==1) {
					indiv.mutation(target);
				}
			}
			
			this.population.setPopulation(newPopulation);
			if(population.getBestIndividual().getFitness() <= acceptableFitnessThreshold) {
				System.out.println("fitness acceptable : "+population.getBestIndividual().getFitness());
				selected = population.getBestIndividual();
				satisfied = true;
			}
			selected = population.getBestIndividual();
			/*
			Group image = new Group();
			int maxX = target.length;
			int maxY = target[0].length;
			WritableImage wimg = new WritableImage(maxX,maxY);
			for(ConvexPolygon cp : selected.getGenome()) {
				image.getChildren().add(cp);
			}
			image.snapshot(null,wimg);
			RenderedImage renderedImage = SwingFXUtils.fromFXImage(wimg, null); 
			try {
				ImageIO.write(renderedImage, "png", new File("generatedImages/result.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			*/
		}
		System.out.println("Not enough generations");
		return selected;		
	}
}
