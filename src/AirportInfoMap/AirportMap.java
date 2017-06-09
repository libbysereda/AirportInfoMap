package airportInfoMap;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 * @author Liubov Sereda
 * Date June 08, 2017
 * 
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	private String cityFile = "city-data.json";
	PGraphics pg;
	RectButton button;
	
	public void setup() {
		// setting up PAppler
		size(900, 700, OPENGL);
		
		button = new RectButton(this, 100, 40);
		button.setCoordinates(10, 10);
		button.setSize(90, 30, 7);
		button.setBaseColor(100, 20, 30);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 0, 0, 900, 900);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		
		// load image for the marker
		// Icon credit: made by http://www.freepik.com loaded from http://www.flaticon.com
		PImage icon = loadImage("icon.png");
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature, icon);
			String airportCode = m.getProperty("code").toString();
			String city = m.getProperty("city").toString();
			
			// https://stackoverflow.com/a/2608682
			city = city.replaceAll("^\"|\"$", "");
			
			for (Feature c : cities) {
				String name = c.getProperty("name").toString();
				if(city.equals(name)) {
					airportList.add(m);
					
					// put airport in hashmap with OpenFlights unique id for key
					airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
				}
			}
			
			/*
			if(!airportCode.equals("\"\"")) 
			{
				m.setRadius(5);
				airportList.add(m);
				
				// https://stackoverflow.com/a/2608682
				airportCode = airportCode.replaceAll("^\"|\"$", "");
				
			
				// put airport in hashmap with OpenFlights unique id for key
				airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
			}
			*/
			
		
		}
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			//routeList.add(sl);
		}
		
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		//map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	
	public void draw() {

		background(0);
		map.draw();
		button.display();
		//drawButtons();
		
		
	}
	
	// helper method for drawing buttons on the map
	private void drawButtons() {
		
		int xbase = 10;
		int ybase = 10;
		
		// left button
		fill(125, 181, 245);
		noStroke();
		rect(xbase, ybase, 100, 30, 7);
		
		textAlign(CENTER, CENTER);
		textSize(10);
		fill(70);
		text("Show all airports", xbase+100/2, ybase+30/2);
		
		// right button
		fill(125, 181, 245);
		noStroke();
		rect(xbase+110, ybase, 100, 30, 7);
		
		textAlign(LEFT, CENTER);
		textSize(10);
		fill(70);
		text("Show all routes", xbase+125, ybase+15);
	}
	
	// helper method for buttons animation
	public void mouseClicked() {
		if (mouseX > 10 && mouseX < 100 && mouseY > 10 && mouseY > 20) {
			fill(0);
		}
	}

}