package imageProcessing.Clustering;

import java.util.ArrayList;
import javafx.scene.paint.Color;

public class PixelGroup {
	
	private ArrayList<Pixel> group;
	private Color averageColor;
	double delta = 10;
	
	public PixelGroup() {
		group = new ArrayList<Pixel>();
	}
	
	public PixelGroup(ArrayList<Pixel> g) {
		group = new ArrayList<Pixel>(g);
		setAverageColor();
		setAllColor(getAverageColor());	
	}
	
	public void add(Pixel p) {
		if(p.isFree()) {
			p.setFree(false);
			group.add(p);
		}
	}
	
	public boolean remove(Pixel p) {
		p.setFree(true);
		return group.remove(p);
	}
	
	public ArrayList<Pixel> getGroup(){
		return group;
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
	 * 
	 * @return a new HashSet of Pixels containing all the neighbors of
	 * this group according to the imageToReadFrom
	 */
	public ArrayList<Pixel> getAllNeighbors(){
		ArrayList<Pixel> neighbors = new ArrayList<Pixel>();
		for(Pixel p : group) {
			for(Pixel p2 : p.getNeighbors()) {
				if(!containsPix(p2)) {
					if(!neighbors.contains(p2)) {
						neighbors.add(p2);
					}
				}
			}
		}
		return neighbors;
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
}
