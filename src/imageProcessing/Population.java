package imageProcessing;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Population{
    private ArrayList<Individual> population;
    Color[][] target;
    
    public Population(Color[][] target) {
    	this.target = target;
    	population = new ArrayList<Individual>();
    	for(int i=0; i<100; i++) {
    		population.add(new Individual(50,target));
    	}
    }
    public ArrayList<Individual> getPopulation(){
    	return this.population;
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
    	ArrayList<Individual> sorted = new ArrayList<Individual>();
    	ArrayList<Individual> selected = new ArrayList<Individual>();
    	
    	sorted = (ArrayList<Individual>) MergeSort.Sort(population);
    	
    	// We add the 20 best individuals in the selected list and remove them from the sorted list
    	for(int i=0; i<20; i++) {
    		selected.add(sorted.get(sorted.size()-i));
    		sorted.remove(sorted.size()-1);
    	}
    	
    	// We add up 5 more individuals from the sorted list but with the same chance for everyone to be picked
    	int added = 0;
    	while(added != 5) {
	    	for(Individual i : sorted) {
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
    	
    	
    	return selected;
    }
}
