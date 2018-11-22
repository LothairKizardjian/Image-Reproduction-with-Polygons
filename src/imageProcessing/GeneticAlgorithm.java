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
	private int mutationChance;
	
	public GeneticAlgorithm(Population pop, int maxGNb, double fThreshold,Color[][] target,int mutChance) {
		this.population = pop;
		this.maxGenerationNumber = maxGNb;
		this.acceptableFitnessThreshold = fThreshold;
		this.target = target;
		this.mutationChance = mutChance;
	}
	
	public Color[][] getTarget(){
		return this.target;
	}
	
	/**
	 * 
	 * @param father
	 * @param mother
	 * @return a new offSpring with a uniform combination of the father's and the mother's genom
	 */
	public Individual uniformCrossover(Individual father,Individual mother) {
		ArrayList<ConvexPolygon> newGenome = new ArrayList<ConvexPolygon>();
		int fatherSize = father.getGenome().size();
    	int motherSize = mother.getGenome().size();
    	int newGenomeSize = fatherSize>motherSize?fatherSize:motherSize;
    	
    	Random gen = new Random();
    	int i = 0;
    	while(newGenome.size() < newGenomeSize) {
    		if(newGenomeSize == fatherSize) {
    			if(i>=motherSize) {
    				newGenome.add(father.getGenome().get(i));
    			}else {
		    		if(gen.nextInt(2) == 1) {
		    			newGenome.add(father.getGenome().get(i));
		    		}else {
		    			newGenome.add(mother.getGenome().get(i));
		    		}
    			}
    		}else {
    			if(i>=fatherSize) {
    				newGenome.add(mother.getGenome().get(i));
    			}else {
    				if(gen.nextInt(2) == 1) {
		    			newGenome.add(father.getGenome().get(i));
		    		}else {
		    			newGenome.add(mother.getGenome().get(i));
		    		}
    			}
    		}
    		i++;
    	}
		
    	Individual offSpring = new Individual(newGenome,target);
    	//TODO
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
			int j = 0;
			do{
				alpha-=populationList.get(j).getFitness();
				j+=1;
			}while(alpha>0 && j<n);
			selected.add(populationList.get(j-1));
		}
		return selected;
	}
	
    /**
     * Generates a new Population based on the crossover of the breeders
     * @param breeders
     * @return a new population of size breeders.size()
     */
    public ArrayList<Individual> generateNewPopulation(ArrayList<Individual> breeders) {
    	ArrayList<Individual> pop = new ArrayList<Individual>();
    	while(pop.size() < population.getPopulation().size()) {
			Random rand = new Random();
			int id1 = rand.nextInt(breeders.size());
			int id2 = rand.nextInt(breeders.size());
			Individual father = breeders.get(id1);
			Individual mother = breeders.get(id2);
			pop.add(uniformCrossover(father,mother));
		}
    	return pop;
    }
	
	public Individual selection() {
				
		Individual selected = new Individual();
		Individual bestSoFar = new Individual();
		double bestFitnessSoFar = 10000000000.0;
		boolean satisfied = false;
		int mutationChance = 3;
		int currentGenerationNumber = 0;
		ArrayList<Individual> breeders = new ArrayList<Individual>();
		
		while(!satisfied && currentGenerationNumber < maxGenerationNumber) {

			currentGenerationNumber++;
			breeders = rouletteWheelSelection(population.getPopulation().size());
			Population newPopulation = new Population();
			newPopulation.getPopulation().addAll(generateNewPopulation(breeders));
			
			for(Individual indiv : newPopulation.getPopulation()) {
				Random rand = new Random();
				int r = rand.nextInt(100);
				if(r<mutationChance) {
					indiv.mutation(target);
				}
			}
			
			this.population.setPopulation(newPopulation.getPopulation());
			if(population.getBestIndividual().getFitness() <= acceptableFitnessThreshold) {
				System.out.println("fitness acceptable : "+population.getBestIndividual().getFitness());
				selected = population.getBestIndividual();
				satisfied = true;
			}
			selected = population.getBestIndividual();
			System.out.println("Generation n°"+currentGenerationNumber+" Best current fitness : "+selected.getFitness());
			
			Group image = new Group();
			int maxX = target.length;
			int maxY = target[0].length;
			WritableImage wimg = new WritableImage(maxX,maxY);
			for(ConvexPolygon p : selected.getGenome()) {
				if(!image.getChildren().contains(p)) {
					image.getChildren().add(p);
				}
			}
			image.snapshot(null,wimg);
			RenderedImage renderedImage = SwingFXUtils.fromFXImage(wimg, null); 
			try {
				ImageIO.write(renderedImage, "png", new File("generatedImages/result.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		System.out.println("Not enough generations");
		return selected;		
	}
}
