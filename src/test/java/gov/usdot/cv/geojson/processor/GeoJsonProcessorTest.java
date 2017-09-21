package gov.usdot.cv.geojson.processor;

import static org.junit.Assert.*;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.deleidos.rtws.core.framework.processor.EnrichmentAction;
import com.deleidos.rtws.core.framework.processor.EnrichmentFieldMappingCache;
import com.deleidos.rtws.core.framework.processor.EnrichmentModelMappingCache;

public class GeoJsonProcessorTest {
	
	@Test
	public void testAddPolygonGeoJsonDefSuccess() {
		GeoJsonProcessor processor = new GeoJsonProcessor();
		
		JSONObject advSitData = buildAdvSitData();
		
		EnrichmentAction geojsonAction = new EnrichmentAction();
		geojsonAction.setType("cv_geojson");
		geojsonAction.setField("region");
		geojsonAction.setParameters(new String[]{"nwPos.lat", "nwPos.lon", "sePos.lat", "sePos.lon"});
		geojsonAction.setRemoveParameters(false);
		
		EnrichmentFieldMappingCache fields = new EnrichmentFieldMappingCache();
		fields.setModelName("test");
		fields.setModelVersion("1.0");
		fields.setFields(new EnrichmentAction[]{geojsonAction});
		
		EnrichmentModelMappingCache models = new EnrichmentModelMappingCache();
		models.setModels(new EnrichmentFieldMappingCache[]{fields});
		
		processor.setEnrichments(models);
		
		processor.initialize();
		processor.process(advSitData);
		
		assertEquals(true, advSitData.has("region"));
		assertEquals("Polygon", advSitData.getJSONObject("region").getString("type"));
		assertEquals(1, advSitData.getJSONObject("region").getJSONArray("coordinates").size());
		assertEquals(5, advSitData.getJSONObject("region").getJSONArray("coordinates").getJSONArray(0).size());
		
		processor.dispose();
	}
	
	@Test
	public void testAddPointGeoJsonDefSuccess() {
		GeoJsonProcessor processor = new GeoJsonProcessor();
		
		JSONObject vsdm = buildVehicleSitData();
		
		EnrichmentAction geojsonAction = new EnrichmentAction();
		geojsonAction.setType("cv_geojson");
		geojsonAction.setField("region");
		geojsonAction.setParameters(new String[]{"lat", "long"});
		geojsonAction.setRemoveParameters(false);
		
		EnrichmentFieldMappingCache fields = new EnrichmentFieldMappingCache();
		fields.setModelName("test");
		fields.setModelVersion("1.0");
		fields.setFields(new EnrichmentAction[]{geojsonAction});
		
		EnrichmentModelMappingCache models = new EnrichmentModelMappingCache();
		models.setModels(new EnrichmentFieldMappingCache[]{fields});
		
		processor.setEnrichments(models);
		
		processor.initialize();
		processor.process(vsdm);
		
		assertEquals(true, vsdm.has("region"));
		assertEquals("Point", vsdm.getJSONObject("region").getString("type"));
		assertEquals(2, vsdm.getJSONObject("region").getJSONArray("coordinates").size());
		assertTrue(-83.0458529 == vsdm.getJSONObject("region").getJSONArray("coordinates").getDouble(0));
		assertTrue(42.3279744 == vsdm.getJSONObject("region").getJSONArray("coordinates").getDouble(1));
		
		processor.dispose();
	}
	
	@Test
	public void testMissingParameters() {
		GeoJsonProcessor processor = new GeoJsonProcessor();
		
		JSONObject advSitData = buildAdvSitData();
		
		EnrichmentAction geojsonAction = new EnrichmentAction();
		geojsonAction.setType("cv_geojson");
		geojsonAction.setField("geo");
		geojsonAction.setParameters(new String[]{"nwPos.lon", "sePos.lat", "sePos.lon"});
		geojsonAction.setRemoveParameters(false);
		
		EnrichmentFieldMappingCache fields = new EnrichmentFieldMappingCache();
		fields.setModelName("test");
		fields.setModelVersion("1.0");
		fields.setFields(new EnrichmentAction[]{geojsonAction});
		
		EnrichmentModelMappingCache models = new EnrichmentModelMappingCache();
		models.setModels(new EnrichmentFieldMappingCache[]{fields});
		
		processor.setEnrichments(models);
		
		processor.initialize();
		processor.process(advSitData);
		
		assertEquals(false, advSitData.has("geo"));
		
		processor.dispose();
	}
	
	@Test
	public void testMissingNWPosObject() {
		GeoJsonProcessor processor = new GeoJsonProcessor();
		
		JSONObject advSitData = buildAdvSitData();
		advSitData.remove("nwPos");
		
		EnrichmentAction geojsonAction = new EnrichmentAction();
		geojsonAction.setType("cv_geojson");
		geojsonAction.setField("geo");
		geojsonAction.setParameters(new String[]{"nwPos.lat", "nwPos.lon", "sePos.lat", "sePos.lon"});
		geojsonAction.setRemoveParameters(false);
		
		EnrichmentFieldMappingCache fields = new EnrichmentFieldMappingCache();
		fields.setModelName("test");
		fields.setModelVersion("1.0");
		fields.setFields(new EnrichmentAction[]{geojsonAction});
		
		EnrichmentModelMappingCache models = new EnrichmentModelMappingCache();
		models.setModels(new EnrichmentFieldMappingCache[]{fields});
		
		processor.setEnrichments(models);
		
		processor.initialize();
		processor.process(advSitData);
		
		assertEquals(false, advSitData.has("geo"));
		
		processor.dispose();
	}
	
	private static JSONObject buildAdvSitData() {
		JSONObject data = new JSONObject();
		
		JSONObject header = new JSONObject();
		header.put("modelName", "test");
		header.put("modelVersion", "1.0");
		data.put("standardHeader", header);
		
		data.put("receiptId", "b334e47b-2f2b-46e7-ad1c-2ff069e038c1");
		data.put("dialogId", 156);
		data.put("sequenceId", 5);
		data.put("requestId", 428673774);
		
		JSONObject nwPos = new JSONObject();
		nwPos.put("lat", 43.0);
		nwPos.put("lon", -85.0);
		data.put("nwPos", nwPos);
		
		JSONObject sePos = new JSONObject();
		sePos.put("lat", 41.0);
		sePos.put("lon", -82.0);
		data.put("sePos", sePos);
		
		return data;
	}
	
	private static JSONObject buildVehicleSitData() {
		JSONObject data = new JSONObject();
		
		JSONObject header = new JSONObject();
		header.put("modelName", "test");
		header.put("modelVersion", "1.0");
		data.put("standardHeader", header);
		
		data.put("receiptId", "b334e47b-2f2b-46e7-ad1c-2ff069e038c1");
		data.put("dialogId", 154);
		data.put("sequenceId", 5);
		data.put("requestId", 428673774);
		data.put("lat", 42.3279744);
		data.put("long", -83.0458529);

		return data;
	}
}