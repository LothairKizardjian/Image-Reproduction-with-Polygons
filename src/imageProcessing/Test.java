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
	
	public static void main(String[] args){
		launch(args);
	}
	
	public void start(Stage myStage){
		String targetImage = "monaLisa-100.jpg";
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
		
		// Création d'une population
		Population pop = new Population(target);
		
		ArrayList<Group> g = new ArrayList<Group>();
		ArrayList<RenderedImage> images = new ArrayList<RenderedImage>();
		
		Group bestImage = new Group();

		for(Individual i : pop.getPopulation()) {
			System.out.println("Polygon number : "+i.getGenome().size());
			Group gr = new Group();
			WritableImage wimg = new WritableImage(maxX,maxY);
			for(ConvexPolygon cp : i.getGenome()) {
				gr.getChildren().add(cp);
			}
			gr.snapshot(null,wimg);
			g.add(gr);
			if(pop.getBestIndividual() == i) {
				bestImage = gr;
			}
			RenderedImage renderedImage = SwingFXUtils.fromFXImage(wimg, null); 
			images.add(renderedImage);
		}

		int cpt = 0;
		for(RenderedImage rdImage : images) {
			try {
				ImageIO.write(rdImage, "png", new File("generatedImages/test"+cpt+".png"));
				cpt++;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		System.out.println("Best fitness : "+pop.getBestIndividual().getFitness());
		//affichage de l'image dans l'interface graphique
		Scene scene = new Scene(bestImage,maxX, maxY);
		myStage.setScene(scene);
		myStage.show();
		
		
	}

}
