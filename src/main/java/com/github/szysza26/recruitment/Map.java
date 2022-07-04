package com.github.szysza26.recruitment;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseAdapter;
import org.geotools.swing.event.MapMouseEvent;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Filter;

/**
 * This class represent map displayed in window
 */
public class Map extends JMapPane {

	private static final long serialVersionUID = 1L;

	private PopupFactory pf;
	private JLabel popupLabel;
	private Popup popup;

	Map() {
		super(new MapContent());

		pf = new PopupFactory();
		popupLabel = new JLabel();

		initHoverPopup();
	}

	/**
	 * This method clear all old layers from map and put new layer
	 * @param layer
	 */
	void updateLayer(Layer layer) {
		MapContent mapContent = getMapContent();

		for (Layer l : mapContent.layers()) {
			mapContent.removeLayer(l);
		}

		mapContent.addLayer(layer);
	}

	/**
	 * This method add mouse move event listener to map
	 * On hover mouse on point (tolerance 10px) will be displayed tooltip with name of point
	 */
	void initHoverPopup() {
		addMouseListener(new MapMouseAdapter() {
			@Override
			public void onMouseMoved(MapMouseEvent ev) {
				if (getMapContent().layers().size() == 0)
					return;

				MapContent mapContent = getMapContent();
				Layer layer = mapContent.layers().get(0);
				ReferencedEnvelope envelope = ev.getEnvelopeByPixels(10);

				String geometryPropertyName = layer.getFeatureSource().getSchema().getGeometryDescriptor()
						.getLocalName();
				FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
				Filter filter = ff.bbox(ff.property(geometryPropertyName), envelope);
				SimpleFeatureCollection collection;
				try {
					collection = (SimpleFeatureCollection) mapContent.layers().get(0).getFeatureSource()
							.getFeatures(filter);
				} catch (IOException e) {
					System.out.println("popup error");
					e.printStackTrace();
					return;
				}

				if (collection.size() > 0 && popup == null) {
					String text = (String) collection.features().next().getAttribute("name");
					popupLabel.setText(text);
					popup = pf.getPopup((Component) ev.getSource(), popupLabel, ev.getXOnScreen() + 10,
							ev.getYOnScreen() - 25);
					popup.show();
				} else if (collection.size() == 0 && popup != null) {
					popup.hide();
					popup = null;
				}
			}
		});
	}
}
