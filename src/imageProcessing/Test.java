package imageProcessing;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Test extends Application{
	
	public static Stage stage;
	public static String imgName;
	
	public static void main(String[] args){
		launch(args);
	}
	
	public static Group createResult(Individual indiv,int maxX, int maxY, String name) {
		Group image = new Group();
		WritableImage wimg = new WritableImage(maxX,maxY);
		for(ConvexPolygon cp : indiv.getGenome()) {
			image.getChildren().add(cp);
		}
		image.snapshot(null,wimg);
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(wimg, null); 
		try {
			ImageIO.write(renderedImage, "png", new File("generatedImages/"+name+".png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return image;	
	}
	
	public static void showImage(Stage myStage,Scene scene,Group image,int maxX,int maxY) {
		/*
		 * affichage de l'image dans l'interface graphique
		 */
		myStage.setScene(scene);
		myStage.show();
	}
	
	public Color[][] createColorTab(String targetImage){
		Color[][] target=null;
		int maxX=0;
    	int maxY=0;
		try{
			BufferedImage bi = ImageIO.read(new File(targetImage));
			maxX = bi.getWidth();
			maxY = bi.getHeight();
        	ConvexPolygon.max_X= maxX;
        	ConvexPolygon.max_Y= maxY;
        	target = new Color[maxX][maxY];
        	for (int i=0;i<maxX;i++){
        		for (int j=0;j<maxY;j++){
        			int argb = bi.getRGB(i, j);
        			int b = (argb)&0xFF;
        			int g = (argb>>8)&0xFF;
        			int r = (argb>>16)&0xFF;
        			int a = (argb>>24)&0xFF;
        			target[i][j] = Color.rgb(r,g,b);
        		}
        	}
        }
        catch(IOException e){
        	System.err.println(e);
        	System.exit(9);
        }
		System.out.println("Read target image " + targetImage + " " + maxX + "x" + maxY);
		return target;
	}
	
	public void start(Stage myStage){
		stage = myStage;
		imgName = "monaLisa-200";
		Color[][] target = createColorTab("generatedImages/"+imgName+".jpg");
		int maxX = target.length;
		int maxY = target[0].length;

		GeneticAlgorithm GA = new GeneticAlgorithm(
				100000000, // maxGenerationNumber
				10, //acceptableFitnessThreshold
				target // image cible				
				);		

		Individual bestIndividual = GA.run();	
		createResult(bestIndividual,maxX,maxY,imgName+"-finalResult");
		
	}

}
