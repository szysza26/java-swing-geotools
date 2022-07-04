package com.github.szysza26.recruitment;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geotools.data.DataUtilities;
import org.geotools.data.collection.CollectionFeatureSource;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class PointReceiver {
	/**
	 * URL to remote servlet
	 */
	private final static String URL = "http://daily.digpro.se/bios/servlet/bios.servlets.web.RecruitmentTestServlet";

	/**
	 * This method fetch points data from remote servlet and then parse and convert it
	 * 
	 * Response from servlet must have header with information about charset, otherwise set default UTF_8
	 * Comment lines starting with "#" are ignored
	 * Expected data points format as "x, y, name"
	 * 
	 * @return Layer object form geotools package with saved points
	 * @throws PointFetchException
	 */
	public static Layer fetchPoints() throws PointFetchException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).build();
		HttpResponse<Stream<String>> response;
		try {
			response = client.send(request, BodyHandlers.ofLines());
		} catch (IOException | InterruptedException e) {
			throw new PointFetchException("connection with server fail");
		}

		SimpleFeatureType TYPE;
		try {
			TYPE = DataUtilities.createType("point", "location:Point,name:String");
		} catch (SchemaException e) {
			throw new PointFetchException("fail in create type");
		}

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

		List<SimpleFeature> features = response.body().filter(line -> !line.startsWith("#")).map(line -> {
			String[] result = line.split(", ");
			Point point = geometryFactory
					.createPoint(new Coordinate(Double.parseDouble(result[0]), Double.parseDouble(result[1])));
			featureBuilder.add(point);
			featureBuilder.add(result[2]);
			return featureBuilder.buildFeature(null);
		}).collect(Collectors.toCollection(ArrayList::new));

		ListFeatureCollection collection = new ListFeatureCollection(TYPE, features);
		Style style = SLD.createSimpleStyle(new CollectionFeatureSource(collection).getSchema());
		Layer layer = new FeatureLayer(collection, style);

		return layer;
	}
}
