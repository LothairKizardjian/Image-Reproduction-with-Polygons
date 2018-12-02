package imageProcessing.Clustering;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import imageProcessing.ConvexPolygon;
import imageProcessing.GrahamScan.GrahamScan;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Clustering {
	
	private ArrayList<PixelGroup> clusters;
	private Image imageToReadFrom;
	private WritableImage imageToWriteTo;
	private int maxClusterNumber;
	private String targetName;
	public static PixelGroup targetImage;
	public static ArrayList<Pixel> pixelsRemaining;
	
	public Clustering(Image i1, WritableImage i2,String tname,int clusterNumber) {
		imageToReadFrom = i1;
		imageToWriteTo  = i2;
		maxClusterNumber = clusterNumber;
		targetName = tname;
		clusters = new ArrayList<PixelGroup>();
		pixelsRemaining = new ArrayList<Pixel>();
		targetImage = this.getAllPixels();
	}
	
	public boolean add(PixelGroup cluster) {
		if(clusters.size() < maxClusterNumber) {
			return clusters.add(cluster);
		}
		return false;
	}
	
	public boolean remove(PixelGroup cluster) {
		return clusters.remove(cluster);
	}
	
	public int getPixelNumber() {
		int size = 0;
		for(PixelGroup p : clusters) {
			size += p.getGroup().size();
		}
		return size;
	}
	
	/**
	 * Merges both the PixelGroups into a new one. Deleting from clusters those
	 * 2 groups and adding the new one. 
	 * @param cluster1
	 * @param cluster2
	 */
	public void merge(PixelGroup cluster1, PixelGroup cluster2) {
		ArrayList<Pixel> newGroup = new ArrayList<Pixel>();
		
		for(Pixel p : cluster1.getGroup()) {
			newGroup.add(new Pixel(p));
		}
		for(Pixel p : cluster2.getGroup()) {
			newGroup.add(new Pixel(p));
		}
		
		PixelGroup merged = new PixelGroup(newGroup);
		
		clusters.remove(cluster1);
		clusters.remove(cluster2);
		clusters.add(merged);
	}
	
	/**
	 * @return a new HashSet of Pixel containing all the pixels of the imageToReadFrom
	 */
	public PixelGroup getAllPixels(){
		ArrayList<Pixel> allImagePixels = new ArrayList<Pixel>();
		for(int i=0; i<imageToReadFrom.getWidth(); i++) {
			for(int j=0; j<imageToReadFrom.getHeight(); j++) {
				allImagePixels.add(new Pixel(i,j,imageToReadFrom,imageToWriteTo));
			}
		}
		return new PixelGroup(allImagePixels);
	}
	
	public boolean containsPix(Pixel p) {
		for(PixelGroup pg : clusters) {
			if(pg.containsPix(p)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param p
	 * @return a new HashSet of pixels containing the neighbors of p that
	 * doesn't belong to any of the PixelGroup of the cluster
	 */
	public ArrayList<Pixel> getFreeNeighbors(PixelGroup p,ArrayList<Pixel> pixelsRemaining){
		ArrayList<Pixel> freeNeighbors = new ArrayList<Pixel>();
		ArrayList<Pixel> pNeighbors = p.getAllNeighbors();
		for(Pixel pix : pNeighbors) {
			for(Pixel pixel : pixelsRemaining) {
				if(pix.equals(pixel)) {
					freeNeighbors.add(pix);
				}
			}
		}
		return freeNeighbors;
	}
	
	/**
	 * 
	 * @param p
	 * @param dist
	 * @return true if the distance between p and all the others points of the cluster is acceptable
	 */
	public boolean canAddPixel(Pixel p,double dist) {
		if(clusters.size() == 0) {
			return true;
		}else {
			for(PixelGroup pg : clusters) {
				for(Pixel pix : pg.getGroup()) {
					if(p.distanceBetween(pix) < dist) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Initialize the cluster with maxClusterNumber clusters of pixel of size 1
	 * @param maxClusterNumber
	 * @return the number of clusters created (should be maxClusterNumber)
	 */
	public int initializeCluster(int maxClusterNumber) {
		Random gen = new Random();
		int added = 0;
		int maxX = (int) imageToReadFrom.getWidth();
		int maxY = (int) imageToReadFrom.getHeight();
		double acceptableDistanceBetweenPixel = 15.2;
		while(added < maxClusterNumber) {
			int x = gen.nextInt(maxX);
			int y = gen.nextInt(maxY);
			Pixel p = targetImage.getPixel(x, y);
			if(canAddPixel(p,acceptableDistanceBetweenPixel)) {
				ArrayList<Pixel> newPixelGroup = new ArrayList<Pixel>();
				newPixelGroup.add(p);
				PixelGroup newCluster = new PixelGroup(newPixelGroup);
				clusters.add(newCluster);
				pixelsRemaining.remove(p);
				added++;
			}
		}
		return added;
	}
	
	/**
	 * Will generate a new cluster starting with random numPoints PixelGroups on the
	 * imageToReadFrom. Those PixelGroups will start with only one pixel and then will
	 * grow, merge together ... etc
	 * @param numPoints
	 */
	public void generateCluster() {
		int pixelNumber = (int) (imageToReadFrom.getWidth() * imageToReadFrom.getHeight());
		for(Pixel p : targetImage.getGroup()) {
			pixelsRemaining.add(p);
		}
		
		/**
		 * Randomly generating numPoints PixelGroups of one Pixel
		 */		
		int pixelAdded = initializeCluster(maxClusterNumber);		
		
		/**
		 * Addind all the pixels that aren't in a group
		 */			
		while(pixelAdded < pixelNumber) {
			for(PixelGroup pg : clusters) {
				ArrayList<Pixel> freeNeighbors = getFreeNeighbors(pg,pixelsRemaining);
				for(Pixel pix : freeNeighbors) {
					if(Math.abs(pix.getColorValue() - pg.getAverageColorValue()) < pg.delta) {
						pg.add(pix);
						pixelsRemaining.remove(pix);
						pixelAdded++;
						pg.setAverageColor();
						pg.setAllColor(pg.getAverageColor());
					}
				}write();
			}
		}	
		write();
	}
	
	public void write() {
		for(PixelGroup pg : clusters) {
			for(Pixel pix : pg.getGroup()) {
				pix.imageToWriteTosetColor();
			}
		}
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(imageToWriteTo, null); 
		try {
			ImageIO.write(renderedImage, "png", new File("generatedImages/"+targetName+"Clustered"+".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<ConvexPolygon> generateConvexPolygons(){
		ArrayList<ConvexPolygon> cp = new ArrayList<ConvexPolygon>();
		for(PixelGroup pg : clusters) {
			int[] xlist = new int[pg.getGroup().size()];
			int[] ylist = new int[pg.getGroup().size()];
			int i = 0;
			for(Pixel p : pg.getGroup()) {
				xlist[i] = p.getX();
				ylist[i] = p.getY();
				i++;
			}
			ConvexPolygon poly = new ConvexPolygon(GrahamScan.getConvexHull(xlist,ylist),pg.getAverageColor());
			cp.add(poly);
		}
		return cp;		
	}
}
