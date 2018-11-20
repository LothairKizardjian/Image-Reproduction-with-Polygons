package imageProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class Individual{
    private List<ConvexPolygon> genome;
    private double fitness;
    
    public Individual(ArrayList<ConvexPolygon> genome,Color[][] target) {
    	this.genome = new ArrayList<ConvexPolygon>(genome);
    	setFitness(target);
    }

    public Individual(int n,Color[][] target){
        
        genome=new ArrayList<ConvexPolygon>();
        Random r = new Random();
        int random = 3 + r.nextInt(ConvexPolygon.maxEdges);
        for(int i=0; i<n; i++) {
        	genome.add(new ConvexPolygon(random));
        }
        setFitness(target);
    }
    
    public List<ConvexPolygon> getGenome() {
        return genome;
    }
    
    public void setGenome(List<ConvexPolygon> genome) {
    	this.genome = new ArrayList<ConvexPolygon>(genome);
    }
   
    public double getFitness() {
    	return this.fitness;
    }
    
    public void setFitness(Color[][] target){
		Group image = new Group();
		for(ConvexPolygon p : this.genome){
		    image.getChildren().add(p);
		}
	
		int x = target.length;
		int y = target[0].length;
		
	    WritableImage wimg = new WritableImage(x,y);
		image.snapshot(null,wimg);
		PixelReader pr = wimg.getPixelReader();
		double res = 0;
		for(int i=0; i<x; i++){
		    for(int j=0; j<y; j++){
			Color c = pr.getColor(i,j);
			res+= Math.pow(c.getBlue()-target[i][j].getBlue(),2)
			    + Math.pow(c.getRed()-target[i][j].getRed(),2)
			    + Math.pow(c.getGreen()-target[i][j].getGreen(),2);
		    }
		}	
		this.fitness = res;
    }
    

    
    public void mutation() {
    	Random rand = new Random();
        int random = 1+rand.nextInt(100);
        int random2= 1+rand.nextInt(genome.size());
        
        //On choisi au hasard le gene (ici un polygone) qui subira une mutation
        ConvexPolygon chosen = genome.get(random2);
        
        //On effectue ensuite une mutation qui a 1 chance sur 3 d'avoir des effets différents        
        if(random <= 33) {
            // mutation sur la couleur d'un polygone : on lui en attribue une nouvelle au hasard
    		Random gen = new Random();
			int r = gen.nextInt(256);
			int g = gen.nextInt(256);
			int b = gen.nextInt(256); 
			chosen.setFill(Color.rgb(r, g, b));
        }else if(33 < random && random <= 66){
        	// mutation sur ajout ou retrait d'un sommet d'un polygone mais sa couleur reste identique
        	// mais la forme peut radicalement être modifiée
        	int random3 = rand.nextInt(2);
        	if(random3 == 1) {
        		//ajout d'un sommet 
        		chosen.genRandomConvexPolygone(chosen.verteces+1);
        	}else {
        		//retrait d'un sommet
        		chosen.genRandomConvexPolygone(chosen.verteces-1);
        	}        	
        }else {
        	// mutation sur la suppression ou l'ajout d'un polygone (ssi il n'en a pas déjà 50)
        	int random4 = rand.nextInt(2);   
        	if(random4 == 1) {
        		//retrait du polygone
        		genome.remove(chosen);
        	}else {
        		//ajout d'un nouvea polygone aléatoire si genome.size() < 50, sinon rien ne se passe
        		if(genome.size()<50) {
        			int random5 = 3 + rand.nextInt(ConvexPolygon.maxEdges);
        			genome.add(new ConvexPolygon(random5));
        		}
        	}
        	
        }
    }
}
