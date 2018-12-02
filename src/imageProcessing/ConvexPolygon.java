package imageProcessing;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import imageProcessing.GrahamScan.GrahamScan;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;



public class ConvexPolygon extends Polygon {
		
		static final int maxNumPoints=100;
		static Random gen = new Random();
		static int max_X,max_Y;
		int colors[] = new int[3];
		double opacity;
		NumberFormat nf = new DecimalFormat("##.00");
		
		
		// randomly generates a convex polygon
		public ConvexPolygon(int numPoints,boolean isConvex,String format){
			super();
			if(isConvex) {
				genRandomConvexPolygone(numPoints);
			}else {
				genRandomPolygon(numPoints);
			}
			int r = 0;
			int g = 0;
			int b = 0;
			if(format == "color") {
				r = gen.nextInt(256);
				g = gen.nextInt(256);
				b = gen.nextInt(256); 
				
			}else if(format == "black") {
				r = 0;
				g = 0;
				b = 0;
			}else if(format == "white") {
				r = 255;
				g = 255; 
				b = 255;
			}
			colors[0] = r;
			colors[1] = g;
			colors[2] = b;
			this.setFill(Color.rgb(r, g, b));
			this.opacity = 0.0;
			this.setOpacity(opacity);
		}
		
		public ConvexPolygon(List<java.awt.Point> pointList,Color c) {
			for(int i=0; i<pointList.size(); i++) {
					addPoint(pointList.get(i).getX(),pointList.get(i).getY());
			}
			this.opacity = 1;
			colors[0] = (int) (c.getRed()*255);
			colors[1] = (int) (c.getGreen()*255);
			colors[2] = (int) (c.getBlue()*255);
			commitChanges();
		}
		
		public ConvexPolygon(){
			super();
		}
		
		public ConvexPolygon(ConvexPolygon p) {
			this.getPoints().clear();
			for(double point : p.getPoints()) {
				this.getPoints().add(point);
			}
			for(int i=0; i<colors.length; i++) {
				colors[i] = p.colors[i];
			}
			opacity = p.opacity;
			this.setFill(Color.rgb(colors[0], colors[1], colors[2]));
			this.setOpacity(opacity);
		}
		
		public void commitChanges() {
			this.setFill(Color.rgb(colors[0], colors[1], colors[2]));
			this.setOpacity(opacity);			
		}
				
		public String toString(){
			String res = super.toString();
			res += " " + this.getFill() + " opacity " + this.getOpacity();
			return res;
		}
		
		public int[][] generatePoints(int numPoints){
			int[][] points = new int[2][numPoints];
			Random gen = new Random();
			for(int j=0; j<points[0].length; j++) {
				int x = gen.nextInt(GeneticAlgorithm.target.length);
				int y = gen.nextInt(GeneticAlgorithm.target[0].length);
				points[0][j] = x;
				points[1][j] = y;
			}			
			return points;
		}
			
		public void addPoint(double x, double y){
			getPoints().add(x);
			getPoints().add(y);
		}
		
		/**
		 * generates an array of coordinate that represents a polygon
		 * @param numPoints
		 */
		public void genRandomPolygon(int numPoints) {
			int[][] points = generatePoints(numPoints);
			for(int i=0;i<points[0].length;i++) {
				addPoint(points[0][i],points[1][i]);
			}
		}
		
		/**
		 * generates an array of coordinate that represents a convex polygon
		 * @param numPoints
		 */
		public void genRandomConvexPolygone(int numPoints) {
			int[][] points = generatePoints(numPoints);
			List<java.awt.Point> convexHull = GrahamScan.getConvexHull(points[0],points[1]);
			for(int i=0; i<convexHull.size()-1; i++) {
				/*
				 * the last point of the convexHull is the same as the first
				 */
				addPoint(convexHull.get(i).getX(),convexHull.get(i).getY());
			}
		}
		
		/**
		 * @return true if all the points of the polygon are collinear
		 */
		public boolean areAllCollinear() {
			int[] vecX = new int[this.getPoints().size()/2];
			int[] vecY = new int[this.getPoints().size()/2];
			List<java.awt.Point> points = new ArrayList<java.awt.Point>();
			for(int i = 0; i<vecX.length; i++) {
				points.add(new java.awt.Point(vecX[i],vecY[i]));
			}
			return GrahamScan.areAllCollinear(points);
		}
		
		/**
		 * The polygon is convex if his point list contains exactly the same points of his convex envelop
		 * @return true if the polygon is convex
		 */
		public boolean isConvex() {
			//TODO
			return false;
		}
		
		/**
		 * Will make this polygon become convex if he's not
		 */
		public void convexify() {
			int[] vecX = new int[this.getPoints().size()/2];
			int[] vecY = new int[this.getPoints().size()/2];
			
			for(int i = 0; i<this.getPoints().size(); i++) {
				if(i%2 == 0) {
					vecX[i/2] = this.getPoints().get(i).intValue();
				}else {
					vecY[i/2] = this.getPoints().get(i).intValue();
				}
			}
			List<java.awt.Point> convexHull = GrahamScan.getConvexHull(vecX,vecY);
			if(convexHull != null) {
			getPoints().clear();
				for(int i=0; i<convexHull.size()-1; i++) {
					/*
					 * the last point of the convexHull is the same as the first
					 */
					addPoint(convexHull.get(i).getX(),convexHull.get(i).getY());
				}
			}
		}	

	    /**
	     * @return true if we can add a new vertex to the given polygon
	     */
	    public boolean canAddVertex() {
	    	return getPoints().size()/2 < ConvexPolygon.maxNumPoints;
	    }  
	    
	    /**
	     * Add a new random vertex to the given polygon
	     */
	    public void addRandomVertex() {
	    	if(canAddVertex()) {
	    		Random gen = new Random();
	    		int x,y;   		
	    		do {
	    			x = gen.nextInt(GeneticAlgorithm.target.length);
	    			y = gen.nextInt(GeneticAlgorithm.target[0].length); 	
	    		}while(contains(x, y));
	    		addPoint(x, y);
	    	}
	    }
	    
	    /**
	     * @return true if the polygon can have one of it's vertex removed
	     */
	    public boolean canRemoveVertex() {
	    	return getPoints().size() > 6;
	    }
	    
	    /**
	     * Removes a random vertex from the polygon
	     */
	    public void removeRandomVertex() {
	    	if(canRemoveVertex()) {
	    		Random gen = new Random();
	    		int x;
	    		do {
	    			x = gen.nextInt(getPoints().size());
	    		}while(x%2 != 0);
	    		getPoints().remove(x);
	    		getPoints().remove(x);
	    	}
	    }
	    
	    /**
	     * Checks if the value x can change by delta
	     * it must be between 0 and max
	     * @param max
	     * @param x
	     * @param delta
	     * @return
	     */
	    public boolean canChangeValue(double max, double x, double delta) {
	    	if(x+delta < 0 || x+delta > max) {
	    		return false;
	    	}
	    	return true;
	    }
	    
	    /**
	     * Change the value of a vertex of the polygon by a percentage (delta) 
	     * of its value but will remain an int.
	     * @param max
	     * @param v
	     * @param delta
	     */
	    public void changeVertex(double max, int v, double delta) {
	    	double valV = getPoints().get(v);
	    	double valW = v%2==0?getPoints().get(v+1):getPoints().get(v-1);
	    	double deltaV = (int) (valV * delta);
	    	if(!contains(valV+delta,valW)) {
		    	if(canChangeValue(max,v,deltaV)) {
		    		getPoints().set(v, valV+deltaV);
		    	}else {
		    		getPoints().set(v, valV-deltaV);
		    	}
	    	}
	    }	
	    
	    public boolean canTranslate(int shiftX,int shiftY) {
	    	for(int i=0; i<getPoints().size()-1; i++) {
    			int maxX = GeneticAlgorithm.target.length;
    			int maxY = GeneticAlgorithm.target[0].length;
    			Double valX = 0.0,valY = 0.0;
	    		if(i%2 ==0) {
	    			valX = getPoints().get(i);
	    			valY = getPoints().get(i+1);
	    		}
	    		if(!canChangeValue(maxX,valX,shiftX) || !canChangeValue(maxY,valY,shiftY)){
	    			return false;
	    		}
	    	}
	    	return true;
	    }
	    
	    /**
	     * Translate the polygons by a random (x,y) value
	     * @param shiftX
	     * @param shiftY
	     */
	    public void translate() {
	    	Random gen = new Random();
			int maxX = GeneticAlgorithm.target.length;
			int maxY = GeneticAlgorithm.target[0].length;
	    	int shiftX,shiftY;
	    	do {
	    		shiftX = gen.nextInt(maxX);
	    		if(gen.nextBoolean()) {
	    			shiftX = -shiftX;
	    		}
	    		shiftY = gen.nextInt(maxY);
	    		if(gen.nextBoolean()) {
	    			shiftY = -shiftY;
	    		}	    		
	    	}while(!canTranslate(shiftX,shiftY));
	    	for(int i=0; i<getPoints().size()-1; i++) {
	    		if(i%2 ==0) {
	    			getPoints().set(i,getPoints().get(i)+shiftX);
	    			getPoints().set(i+1,getPoints().get(i+1)+shiftY);
	    		}
	    	}
	    }
	    
	    /**
	     * Checks if the color C can change by delta amount (i.e it's 0 < c+delta > 255)
	     * @param c
	     * @param delta
	     * @return
	     */
	    public boolean canChangeColor(int c, int delta) {
	    	if(c+delta < 0 || c+delta > 255) {
	    		return false;
	    	}
	    	return true;
	    }
	    
	    /**
	     * Change the color c of the polygon by a percentage (delta)
	     * If the color c is the Red value : c = 144 for example and delta = 0.1
	     * then the new value of c is 144 + 144*0.1 = 144 + 14.4 = 144 + 14 = 158
	     * @param c
	     * @param delta
	     */
	    public void changeColor(int c, double delta) {
	    	int colorVal = colors[c];
	    	int deltaColor = (int) (colorVal * delta);
	    	if(canChangeColor(colorVal,deltaColor)) {
	    		colors[c] += delta;
	    	}else if(canChangeColor(colorVal,-deltaColor)) {
	    		colors[c] -= delta;
	    	}
	    }
	    
	    /**
	     * Checks if the opacity can change by delta amount
	     * @param opacity
	     * @param delta
	     * @return
	     */
	    public boolean canChangeOpacity(double opacity, double delta) {
	    	if(opacity + delta < 0 || opacity + delta > 1) {
	    		// an opacity of 0 is stupid
	    		return false;
	    	}
	    	return true;
	    }
	    
	    /**
	     * Change the opacity of the polygon by a percentage (delta)
	     * If the opacity is 0.84 and delta is 0.1 then the new value
	     * of the opacity is 0.84 + 0.84 * 0.1 = 0.84 + 0.084 = 0.924
	     * @param delta
	     */
	    public void changeOpacity(double delta) {
	    	double deltaOp = opacity * delta;
	    	if(canChangeOpacity(opacity,deltaOp)) {
	    		opacity += deltaOp;
	    	}else {
	    		opacity -= deltaOp;
	    	}
	    }
	    
	    /**
	     * 
	     * @return the area of this polygon
	     * 
	     * DOESNT WORK 
	     
	    public double getArea() {
	    	int[] X = new int[getPoints().size()/2];
	    	int[] Y = new int[getPoints().size()/2];	    	
	    	int numPoints = getPoints().size()/2;
	    	for(int i = 0; i<this.getPoints().size(); i++) {
				if(i%2 == 0) {
					X[i/2] = this.getPoints().get(i).intValue();
				}else {
					Y[i/2] = this.getPoints().get(i).intValue();
				}
			}
	    		
	    	double area = 0;         // Accumulates area in the loop
	    	int j = numPoints-1;  // The last vertex is the 'previous' one to the first

	    	  for (int i=0; i<numPoints; i++)
	    	    { area = area +  (X[j]+X[i]) * (Y[j]-Y[i]); 
	    	      j = i;  //j is previous vertex to i
	    	    }
	    	  return area/2;
	    }
	    */
		
		public class Point {
			int x,y;
			Random gen = new Random();

			// generate a random point
			public Point(){
				x= gen.nextInt(ConvexPolygon.max_X);
				y= gen.nextInt(ConvexPolygon.max_Y);
			}
			
			public Point(int x, int y){
				this.x=x;
				this.y=y;
			}
			
			public Point(Point p) {
				this.x = p.x;
				this.y = p.y;
			}
			
			public int getX(){return x;}
			public int getY(){return y;}
			public void translate(int vx,int vy){
				x += vx;
				y += vy;
			}
			
			public boolean equals(Object o){
				if (o==null)
					return false;
				else if (o == this)
					return true;
				else if (o instanceof Point)
					return ((Point) o).x== this.x && ((Point) o).y== this.y;
				else
					return false;
			}
			
			public String toString(){
				//NumberFormat nf = new DecimalFormat("#.00");
				return "(" + x + "," + y+")"; // + nf.format(Math.atan2(y, x))+")";
			}
		}
}
