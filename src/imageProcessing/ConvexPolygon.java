package imageProcessing;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;



public class ConvexPolygon extends Polygon {
		
		static final int maxNumPoints=3;
		static Random gen = new Random();
		static int max_X,max_Y;
		int verteces;
		static int maxEdges = 3;
		int colors[] = new int[3];
		double opacity;
		NumberFormat nf = new DecimalFormat("##.00");
		
		
		// randomly generates a polygon
		public ConvexPolygon(int numPoints,String format){
			super();
			verteces = numPoints;
			genRandomConvexPolygone(numPoints);
			int r = 0;
			int g = 0;
			int b = 0;
			if(format == "color") {
				r = gen.nextInt(256);
				g = gen.nextInt(256);
				b = gen.nextInt(256); 
				
			}else if(format == "white") {
				r = 0;
				g = 0;
				b = 0;
			}else if(format == "black") {
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
		
		public ConvexPolygon(){
			super();
		}
		
		public ConvexPolygon(ConvexPolygon p) {
			this.verteces = p.verteces;
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
			
		public void addPoint(double x, double y){
			getPoints().add(x);
			getPoints().add(y);
		}
		
		// http://cglab.ca/~sander/misc/ConvexGeneration/convex.html
		public void genRandomConvexPolygone(int n){
			List<Point> points = new LinkedList<Point>();
			List<Integer> abs = new ArrayList<>();
			List<Integer> ord = new ArrayList<>();
			
			for (int i=0;i<n;i++){
				abs.add(gen.nextInt(max_X));
				ord.add(gen.nextInt(max_Y));
			}
			Collections.sort(abs);
			Collections.sort(ord);
			//System.out.println(abs + "\n" + ord);
			int minX = abs.get(0);
			int maxX = abs.get(n-1);
			int minY = ord.get(0);
			int maxY = ord.get(n-1);
			
			List<Integer> xVec = new ArrayList<>();
			List<Integer> yVec = new ArrayList<>();
			
			int top= minX, bot = minX;
			for (int i=1;i<n-1;i++){
				int x = abs.get(i);
				
				if (gen.nextBoolean()){
					xVec.add(x-top);
					top = x;
				} else{
					xVec.add(bot-x);
					bot = x;
				}
			}
			xVec.add(maxX-top);
			xVec.add(bot-maxX);
			
			int left= minY, right = minY;
			for (int i=1;i<n-1;i++){
				int y = ord.get(i);
				
				if (gen.nextBoolean()){
					yVec.add(y-left);
					left = y;
				} else{
					yVec.add(right-y);
					right = y;
				}
			}
			yVec.add(maxY-left);
			yVec.add(right-maxY);
			
			Collections.shuffle(yVec);
			
			List<Point> lpAux = new ArrayList<>();
			for (int i=0;i<n;i++)
				lpAux.add(new Point(xVec.get(i), yVec.get(i)));
		
			
			// sort in order by angle
			Collections.sort(lpAux, (x,y) ->  Math.atan2(x.getY(), x.getX())  < Math.atan2(y.getY(), y.getX()) ? -1 :
				Math.atan2(x.getY(), x.getX())  == Math.atan2(y.getY(), y.getX()) ? 0 : 1);
				
			int x=0,y=0;
			int minPolX=0, minPolY=0;
			
			for (int i=0;i<n;i++){
				points.add(new Point(x,y));
				x += lpAux.get(i).getX();
				y += lpAux.get(i).getY(); 
				
				if (x < minPolX)
					minPolX=x;
				if (y<minPolY)
					minPolY=y;
			}
				
			int xshift = gen.nextInt(max_X - (maxX-minX)) ;
			int yshift = gen.nextInt(max_Y - (maxY-minY)) ;
			xshift -= minPolX;
			yshift -= minPolY;
			for (int i=0;i<n;i++){
				Point p = points.get(i);
				p.translate(xshift,yshift);
			}
			for (Point p : points)
				addPoint(p.getX(), p.getY());
			
			
		}
		
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
				NumberFormat nf = new DecimalFormat("#.00");
				return "(" + x + "," + y+")"; // + nf.format(Math.atan2(y, x))+")";
			}
		}
}
