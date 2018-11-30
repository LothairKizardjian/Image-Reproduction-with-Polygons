package imageProcessing;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class GeneticAlgorithm {
	
	public static Color[][] target;
	public static int maxGenerationNumber;
	public static double acceptableFitnessThreshold;
	
	public GeneticAlgorithm(int maxGNb, double fThreshold, Color[][] t) {
		maxGenerationNumber = maxGNb;
		acceptableFitnessThreshold = fThreshold;
		target = t;
	}
	
	/**
	 * Creates, from the target image, a list of polygon that approximates the most the target
	 * We will then run the algorithm over this list of polygon
	 * @return
	 */
	public List<ConvexPolygon> clustering() {
		List<ConvexPolygon> cluster = new ArrayList<ConvexPolygon>();
		
		return cluster;
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
		String format = "black";		
		Individual t1 = new Individual(50,"T1",format);
		t1.run();
		Individual bestIndividual=t1;
		return bestIndividual;
	}
}
