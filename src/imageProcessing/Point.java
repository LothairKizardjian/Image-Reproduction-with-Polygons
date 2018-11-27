package imageProcessing;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

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
