package imageProcessing.Clustering;

import java.util.ArrayList;
import java.util.Iterator;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * 
 * @author Lothair
 * 
 *	The goal of this class is to not modify 
 *	the image it reads from so it can only 
 *	write into the WritableImage imageToWriteTo
 *
 */
public class Pixel{

	private int x,y;
	private int argb;
	private Color color;
	private boolean free;
	private double delta = 0.0;
	private PixelGroup pixelGroup;
	private Image imageToReadFrom;
	private WritableImage imageToWriteTo;
	private ArrayList<Pixel> freeNeighbors;
	private ArrayList<Pixel> attachedNeighbors;
	
	public Pixel(int x, int y, Image i1, WritableImage i2,PixelGroup pg) {
		this.setX(x);
		this.setY(y);
		this.free = true;
		this.imageToReadFrom = i1;
		this.imageToWriteTo  = i2;
		this.color = i1.getPixelReader().getColor(x,y);
		this.setPixelGroup(pg);
		this.freeNeighbors = new ArrayList<Pixel>();
		this.attachedNeighbors = new ArrayList<Pixel>();
	}
	
	public Pixel(Pixel p) {
		this.setX(p.getX());
		this.setY(p.getY());
		this.color = p.color;
		this.free = p.free;
		this.imageToReadFrom = p.imageToReadFrom;
		this.imageToWriteTo  = p.imageToWriteTo;
		this.setPixelGroup(p.getPixelGroup());
		this.setFreeNeighbors(p.getFreeNeighbors());
	}
	
	public void updateNeighbors() {
		Iterator<Pixel> it = getFreeNeighbors().iterator();
		while(it.hasNext()) {
			Pixel p = it.next();
			if(!p.isFree()) {
				getAttachedNeighbors().add(p);
				it.remove();
			}
		}
	}
	
	public PixelGroup getPixelGroup() {
		return pixelGroup;
	}
	
	public Color getColor() {
		return color;
	}
	
	public boolean isFree() {
		return free;
	}
	
	public void setFree(boolean b) {
		this.free = b;
	}
	
	public double getDelta() {
		return delta;
	}
	
	public void setDelta(double d) {
		delta = d;
	}
	
	public double getColorValue() {
		double res = 0.0;
		res += color.getRed() + color.getGreen() + color.getBlue();
		return res;
	}
	
	public void setColor(Color c) {
		color = c;
	}
	
	public void imageToWriteToSetArgb() {
		imageToWriteTo.getPixelWriter().setArgb(getX(), getY(), argb);
	}

	public void imageToWriteTosetColor() {
		imageToWriteTo.getPixelWriter().setColor(getX(),getY(),color);
	}
	
	public int imageToReadFromGetArgb() {
		return imageToReadFrom.getPixelReader().getArgb(getX(),getY());
	}

	public Color imageToReadFromGetColor() {
		return imageToReadFrom.getPixelReader().getColor(getX(),getY());
	}
	
	/**
	 * 
	 * @return a new HashSet of Pixel containing this pixel's neighbors
	 */
	public void initNeighbors(){
		int maxX = (int) (imageToReadFrom.getWidth()-1);
		int maxY = (int) (imageToReadFrom.getHeight()-1);
		
		for(int i=-1; i<=1; i++) {
			for(int j=-1; j<=1; j++) {
				if(i!=0 || j!=0) {
					Pixel p;
					
					if(getX() == 0){
						if(getY() == 0) {
							if(i>=0 && j>=0) {
								p = getPixelGroup().getCluster().getTargetImage().getPixel(getX()+i, getY()+j);
								if(p.isFree()) {
									freeNeighbors.add(p);
								}else {
									getAttachedNeighbors().add(p);
								}
							}
						}else if(getY() == maxY){
							if(i>=0 && j<=0) {
								p = getPixelGroup().getCluster().getTargetImage().getPixel(getX()+i, getY()+j);
								if(p.isFree()) {
									freeNeighbors.add(p);
								}else {
									getAttachedNeighbors().add(p);
								}
							}
						}else {							
							if(i>= 0) {
								p = getPixelGroup().getCluster().getTargetImage().getPixel(getX()+i, getY()+j);
								if(p.isFree()) {
									freeNeighbors.add(p);
								}else {
									getAttachedNeighbors().add(p);
								}	
							}
						}
					}else if(getX() == maxX){
						if(getY() == 0) {
							if(i<=0 && j>=0) {
								p = getPixelGroup().getCluster().getTargetImage().getPixel(getX()+i, getY()+j);
								if(p.isFree()) {
									freeNeighbors.add(p);
								}else {
									getAttachedNeighbors().add(p);
								}									
							}							
						}else if(getY() == maxY){
							if(i<=0 && j<=0) {
								p = getPixelGroup().getCluster().getTargetImage().getPixel(getX()+i, getY()+j);
								if(p.isFree()) {
									freeNeighbors.add(p);
								}else {
									getAttachedNeighbors().add(p);
								}	
							}
						}else {
							if(i<=0) {
								p = getPixelGroup().getCluster().getTargetImage().getPixel(getX()+i, getY()+j);
								if(p.isFree()) {
									freeNeighbors.add(p);
								}else {
									getAttachedNeighbors().add(p);
								}			
							}
						}
					}else {
						if(getY() == 0) {
							if(j>=0) {
								p = getPixelGroup().getCluster().getTargetImage().getPixel(getX()+i, getY()+j);
								if(p.isFree()) {
									freeNeighbors.add(p);
								}else {
									getAttachedNeighbors().add(p);
								}								
							}
						}else if(getY() == maxY){
							if(j<=0) {
								p = getPixelGroup().getCluster().getTargetImage().getPixel(getX()+i, getY()+j);
								if(p.isFree()) {
									freeNeighbors.add(p);
								}else {
									getAttachedNeighbors().add(p);
								}			
							}
						}else {
							p = getPixelGroup().getCluster().getTargetImage().getPixel(getX()+i, getY()+j);
							if(p.isFree()) {
								freeNeighbors.add(p);
							}else {
								getAttachedNeighbors().add(p);
							}	
						}
					}
				}
			}
		}
	}
	
	public double distanceBetween(Pixel p) {
		return Math.sqrt(Math.pow(p.getX() - getX(),2)+Math.pow(p.getY()-getY(),2));
	}
	
	public boolean equalsPix(Pixel p) {
		return((getX() == p.getX() && getY() == p.getY()));
	}
	
	public String toString() {
		return "(x = "+getX()+"; y = "+getY()+")";
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setPixelGroup(PixelGroup pixelGroup) {
		this.pixelGroup = pixelGroup;
	}

	public ArrayList<Pixel> getFreeNeighbors() {
		return freeNeighbors;
	}

	public void setFreeNeighbors(ArrayList<Pixel> freeNeighbors) {
		this.freeNeighbors = freeNeighbors;
	}

	public ArrayList<Pixel> getAttachedNeighbors() {
		return attachedNeighbors;
	}

	public void setAttachedNeighbors(ArrayList<Pixel> attachedNeighbors) {
		this.attachedNeighbors = attachedNeighbors;
	}
}