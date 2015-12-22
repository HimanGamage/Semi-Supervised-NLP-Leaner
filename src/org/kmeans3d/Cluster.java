package org.kmeans3d;


import java.util.*;

public class Cluster {

	private final List<Point> points;
	private Point centroid;
	private Point closestPoint;
	private LinkedHashMap<Integer, String> relationMapping ;
	public LinkedHashMap<Integer, String> getRelationMapping() {
		return relationMapping;
	}

	public void setRelationMapping(LinkedHashMap<Integer, String> relationMapping) {
		this.relationMapping = relationMapping;
	}

	public Cluster(Point firstPoint) {
		points = new ArrayList<Point>();
		centroid = firstPoint;
	}
	
	public Point getCentroid(){
		return centroid;
	}
	
	public void updateCentroid(){
		double newx = 0d, newy = 0d, newz = 0d;
		
		double minDistance;
		for (Point point : points){
			newx += point.x; newy += point.y; newz += point.z;
		}
		centroid = new Point(newx / points.size(), newy / points.size(), newz / points.size());
		minDistance=getSquareOfDistance(centroid, points.get(0));
		closestPoint=points.get(0);
		for (Point point : points) {
			double d=getSquareOfDistance(centroid, point);
			
			if(minDistance>d){
				minDistance=d;
				closestPoint=point;
			}
		}
	}
	
	public List<Point> getPoints() {
		return points;
	}
	public Point getClosestPoint() {
		return closestPoint;
	}

	public void setClosestPoint(Point closestPoint) {
		this.closestPoint = closestPoint;
	}

	public Double getSquareOfDistance(Point centeroid,Point point){
		return  (centeroid.x - point.x) * (centeroid.x - point.x)
				+ (centeroid.y - point.y) *  (centeroid.y - point.y) 
				+ (centeroid.z - point.z) *  (centeroid.z - point.z);
	}
	public String toString(){
		StringBuilder builder = new StringBuilder("This cluster contains the following points:\n");
		for (Point point : points)
			builder.append(point.toString()+" Distance :"+getSquareOfDistance(centroid, point) + ",\n");
		return builder.deleteCharAt(builder.length() - 2).toString();	
	}
}
