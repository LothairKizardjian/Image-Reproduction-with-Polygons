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
	
	public static Color[][] target;
	public static int maxGenerationNumber;
	public static double acceptableFitnessThreshold;
	
	public GeneticAlgorithm(int maxGNb, double fThreshold, Color[][] t) {
		maxGenerationNumber = maxGNb;
		acceptableFitnessThreshold = fThreshold;
		target = t;
	}
	
	/**
	 * @return the best individual generated
	 */
	public Individual run() {		
		Individual t1 = new Individual(50,"T1");
		t1.run();
		Individual bestIndividual=t1;
		return bestIndividual;
	}
}
