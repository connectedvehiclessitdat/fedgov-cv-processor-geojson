package gov.usdot.cv.geojson.processor;

import gov.usdot.cv.common.database.mongodb.geospatial.Coordinates;
import gov.usdot.cv.common.database.mongodb.geospatial.Geometry;
import gov.usdot.cv.common.database.mongodb.geospatial.Point;

import org.apache.log4j.Logger;

import com.deleidos.rtws.core.framework.EnrichmentDefinition;
import com.deleidos.rtws.core.framework.processor.AbstractEnrichmentProcessor;
import com.deleidos.rtws.core.framework.processor.EnrichmentAction;
import com.deleidos.rtws.core.framework.processor.ParameterList;

@EnrichmentDefinition(
	description = "Add geojson object to the advisory situation data.", 
	type = "cv_geojson", 
	properties = {}
)
public class GeoJsonProcessor extends AbstractEnrichmentProcessor {
	private static final Logger logger = Logger.getLogger(GeoJsonProcessor.class);
	private static final String POLYGON_GEOMETRY_TYPE = "Polygon";
	private static final String POINT_GEOMETRY_TYPE = "Point";
	
	@Override
	public String getType() {
		return "cv_geojson";
	}

	/**
	 * When configuring this enrichment, the order of the parameters must be northwest latitude, 
	 * northwest longitude, southeast latitude, and southeast longitude.
	 */
	@Override
	public Object buildEnrichedElement(EnrichmentAction action, ParameterList parameters) {
		if (parameters.toArray() == null || 
			(parameters.toArray().length != 2 &&
			parameters.toArray().length != 4)) {
			logger.warn("Parameters is empty, not of length 2, and not of length 4.");
			return null;
		}
		
		if (parameters.toArray().length == 2) {
			Double lat = parameters.get(0, Double.class);
			Double lon = parameters.get(1, Double.class);
			
			Geometry geometry = 
					buildGeometry(POINT_GEOMETRY_TYPE, buildPoint(lat, lon));
			
			return geometry.toJSONObject();
		} else {
			Double nwLat = parameters.get(0, Double.class);
			Double nwLon = parameters.get(1, Double.class);
			Double seLat = parameters.get(2, Double.class);
			Double seLon = parameters.get(3, Double.class);
		
			Point nwCorner = buildPoint(nwLat, nwLon);
			Point neCorner = buildPoint(nwLat, seLon);
			Point seCorner = buildPoint(seLat, seLon);
			Point swCorner = buildPoint(seLat, nwLon);
		
			Geometry geometry = buildGeometry(POLYGON_GEOMETRY_TYPE, 
					buildCoordinates(nwCorner, neCorner, seCorner, swCorner));
		
			return geometry.toJSONObject();
		}
	}
	
	private Point buildPoint(Double lat, Double lon) {
		Point.Builder builder = new Point.Builder();
		builder.setLat(lat).setLon(lon);
		return builder.build();
	}
	
	private Coordinates buildCoordinates(
			Point nwCorner, 
			Point neCorner, 
			Point seCorner, 
			Point swCorner) {
		Coordinates.Builder builder = new Coordinates.Builder();
		// Note: geojson requires that all geometry shape start and end at the same point
		builder.addPoint(nwCorner).addPoint(neCorner).addPoint(seCorner).addPoint(swCorner).addPoint(nwCorner);
		return builder.build();
	}
	
	private Geometry buildGeometry(String type, Coordinates coordinates) {
		Geometry.Builder builder = new Geometry.Builder();
		builder.setType(type).setCoordinates(coordinates);
		return builder.build();
	}
	
	private Geometry buildGeometry(String type, Point point) {
		Geometry.Builder builder = new Geometry.Builder();
		builder.setType(type).setPoint(point);
		return builder.build();
	}
	
}