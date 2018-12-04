package imageProcessing.Clustering;

import java.util.ArrayList;

import javafx.concurrent.Task;
import javafx.scene.paint.Color;

public class PixelGroup extends Task<Integer> {
	
	private ArrayList<Pixel> group;
	private Color averageColor;
	private Cluster cluster;
	private String name;
	
	public PixelGroup(Cluster c,String n) {
		group = new ArrayList<Pixel>();
		cluster = c;
		name = n;
	}
	
	public PixelGroup(ArrayList<Pixel> g,Cluster c) {
		this.cluster = c;		
		group = new ArrayList<Pixel>(g);
		for(Pixel p : group) {
			p.setFree(false);
		}
		initNeighbors();
		setAverageColor();
		setAllColor(getAverageColor());	
	}
	
	public void add(Pixel p) {
		synchronized(cluster) {
			if(p.isFree()) {
				p.setFree(false);
				p.setPixelGroup(this);
				p.initNeighbors();
				group.add(p);
				for(Pixel pix : p.getAttachedNeighbors()) {
					pix.updateNeighbors();
				}
				setAverageColor();
				setAllColor(getAverageColor());	
			}
		}
	}
	
	public ArrayList<Pixel> getFreeNeighbors(){
		ArrayList<Pixel> neighborhood = new ArrayList<Pixel>();
		for(Pixel p : group) {
			neighborhood.addAll(p.getFreeNeighbors());
		}		
		return neighborhood;
	}
	
	public boolean remove(Pixel p) {
		p.setFree(true);
		return group.remove(p);
	}
	
	public Pixel getPixel(int x,int y) {
		for(Pixel p : group) {
			if(p.getX() == x && p.getY() == y) {
				return p;
			}
		}
		return null;
	}
	
	public boolean containsPix(Pixel p) {
		for(Pixel pix : group) {
			if(pix.equalsPix(p)) {
				return true;
			}
		}
		return false;
	}
	
	public void initNeighbors() {
		for(Pixel p : group) {
			p.initNeighbors();
		}
	}
	
	/**
	 * 
	 * @param group2
	 * @return a new HashSet of Pixels that contains all the pixels
	 * wich are not contained in this PixelGroup
	 */
	public ArrayList<Pixel> notContainedPixels(ArrayList<Pixel> group2){
		if(group.containsAll(group2)) {
			return null;
		}else {
			ArrayList<Pixel> notContained = new ArrayList<Pixel>();
			for(Pixel p : group2) {
				if(!group.contains(p)) {
					notContained.add(p);
				}
			}
			return notContained;
		}
	} 
	
	/**
	 *	grows the pixel group according to the image (should be threaded) 
	 */
	@Override
	
	protected Integer call() throws Exception {
		ArrayList<Pixel> freeNeighbors = getFreeNeighbors();
		Integer pixelAdded = 0;
		while(freeNeighbors.size() > 0) {
			for(Pixel p : freeNeighbors) {	
				int added = 0;
				if(Math.abs(p.getColorValue()-getAverageColorValue()) < p.getDelta()) {
					//System.out.println("added");
					add(p);
					pixelAdded++;
					added++;
				}
				if(added == 0) {
					p.setDelta(p.getDelta()+0.00001);
				}else {
					if(p.getDelta() > 0) {
						p.setDelta(0);							
					}
				}	
			}
			freeNeighbors = getFreeNeighbors();
			cluster.write();
		}
		System.out.println(getName()+" -> finished");
		return pixelAdded;
	}	

	public void setAverageColor() {
		if(group.size() != 0) {
			double r = 0.0;
			double g = 0.0;
			double b = 0.0;
			double o = 0.0;
			for(Pixel p : group) {
				Color c = p.imageToReadFromGetColor();
				r += c.getRed();
				g += c.getGreen();
				b += c.getBlue();
				o += c.getOpacity();
			}
			r/=group.size();
			g/=group.size();
			b/=group.size();
			o/=group.size();
			averageColor = new Color(r,g,b,o);
		}
	}
	
	public Color getAverageColor() {
		return averageColor;
	}
	
	public double getAverageColorValue() {
		double res = 0.0;
		res += averageColor.getRed() + averageColor.getGreen() + averageColor.getBlue();
		return res;
	}
	/**
	 * Sets the Color of each pixel of this group to the given color
	 * @param c
	 */
	public void setAllColor(Color c) {
		for(Pixel p : group) {
			p.setColor(c);
		}
	}	
	
	public String toString() {
		String s = "";
		for(Pixel p : group) {
			s+=p.toString()+"\n";
		}
		return s;
	}
	
	public ArrayList<Pixel> getGroup(){
		return group;
	}
	
	public void setGroup(ArrayList<Pixel> g) {
		group = g;
	}

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	
	public String getName() {
		return name;
	}
}
