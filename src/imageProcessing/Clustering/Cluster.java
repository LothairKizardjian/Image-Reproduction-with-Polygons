package imageProcessing.Clustering;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import imageProcessing.ConvexPolygon;
import imageProcessing.GrahamScan.GrahamScan;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Cluster {
	
	private ArrayList<PixelGroup> clusters;
	private Image imageToReadFrom;
	private WritableImage imageToWriteTo;
	private int maxClusterNumber;
	private String targetName;
	private PixelGroup targetImage;
	
	public Cluster(Image i1, WritableImage i2,String tname,int clusterNumber) {
		imageToReadFrom = i1;
		imageToWriteTo  = i2;
		maxClusterNumber = clusterNumber;
		targetName = tname;
		clusters = new ArrayList<PixelGroup>();
		targetImage = getAllPixels();
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
	 * @return a new HashSet of Pixel containing all the pixels of the imageToReadFrom
	 */
	public PixelGroup getAllPixels(){
		ArrayList<Pixel> allImagePixels = new ArrayList<Pixel>();
		PixelGroup pg = new PixelGroup(this,"image");
		for(int i=0; i<imageToReadFrom.getWidth(); i++) {
			for(int j=0; j<imageToReadFrom.getHeight(); j++) {
				allImagePixels.add(new Pixel(i,j,imageToReadFrom,imageToWriteTo,pg));
			}
		}
		pg.setGroup(allImagePixels);
		return pg;
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
		double acceptableDistanceBetweenPixel = maxX/maxClusterNumber*maxY/maxClusterNumber;
		while(added < maxClusterNumber) {
			int x = gen.nextInt(maxX);
			int y = gen.nextInt(maxY);
			Pixel p = getTargetImage().getPixel(x, y);
			if(canAddPixel(p,acceptableDistanceBetweenPixel)) {
				PixelGroup newCluster = new PixelGroup(this,""+added);
				newCluster.add(p);
				newCluster.initNeighbors();
				newCluster.setAverageColor();
				newCluster.setAllColor(newCluster.getAverageColor());
				clusters.add(newCluster);
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
		//int pixelNumber = (int) (imageToReadFrom.getWidth() * imageToReadFrom.getHeight());
		
		/*
		 * Randomly generating numPoints PixelGroups of one Pixel
		 */		
		initializeCluster(maxClusterNumber);
		System.out.println("Initialisation finished");
		/*
		 * clustering
		 */
		//ExecutorService executorService = Executors.newFixedThreadPool(maxClusterNumber);
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for(PixelGroup pg : clusters) {
			try {
			     threads.add(new Thread(pg));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(Thread t : threads) {
			t.start();
		}
		for(Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			if(pg.getGroup().size() > 3) {
				int[] xlist = new int[pg.getGroup().size()];
				int[] ylist = new int[pg.getGroup().size()];
				int i = 0;
				for(Pixel p : pg.getGroup()) {
					xlist[i] = p.getX();
					ylist[i] = p.getY();
					i++;
				}
				List<java.awt.Point> pointList = GrahamScan.getConvexHull(xlist,ylist);
				if(pointList != null) {
					ConvexPolygon poly = new ConvexPolygon(GrahamScan.getConvexHull(xlist,ylist),pg.getAverageColor());
					cp.add(poly);
				}
			}
		}
		return cp;		
	}

	public PixelGroup getTargetImage() {
		return targetImage;
	}

	public void setTargetImage(PixelGroup targetImage) {
		this.targetImage = targetImage;
	}
}
