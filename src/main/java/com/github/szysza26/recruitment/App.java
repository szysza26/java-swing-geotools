package com.github.szysza26.recruitment;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.geotools.map.Layer;

/**
 * Main class represent window
 */
public class App extends JFrame {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Time in ms for interval auto refresh data from server
	 */
	private static final int refreshInterval = 30000;

	private Map map;
	private Toolbar toolbar;
	private Infobar infobar;
	private Timer timer;

	/**
	 * Point of start application, with create instance of App class represent window
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new App();
			}
		});
	}

	/**
	 * Initialize Toolbar, Map, Infobar and Timer for auto refresh data from server
	 */
	App() {
		super("Map");
		
		setSize(800, 600);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) ((screenSize.getWidth() - getWidth()) / 2), (int) ((screenSize.getHeight() - getHeight()) / 2));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);

		setLayout(new BorderLayout());

		map = new Map();
		add(map, BorderLayout.CENTER);

		toolbar = new Toolbar(this);
		add(toolbar, BorderLayout.NORTH);

		infobar = new Infobar();
		add(infobar, BorderLayout.SOUTH);

		setVisible(true);

		enableAutoRefresh();
	}
	
	/**
	 * This method is for starts fetch data from server with showed information for user about progress
	 * On success will update layer in map
	 * On failure will show dialog with information about error
	 */
	private void update() {
		infobar.showProgress();
		try {
			Layer layer = PointReceiver.fetchPoints();
			map.updateLayer(layer);
		} catch (PointFetchException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		infobar.hideProgress();
	}

	/**
	 * This method enable auto refresh data from server every specific time
	 */
	void enableAutoRefresh() {
		disableAutoRefresh();
		timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				update();
			}
		}, 0, refreshInterval);
	}

	/**
	 * This method disable auto refresh data from server
	 */
	void disableAutoRefresh() {
		if (timer == null)
			return;

		timer.cancel();
		timer = null;
	}
	
	/**
	 * This method force refresh data from server
	 */
	void reload() {
		new Thread(() -> update()).start();
	}

	Map getMap() {
		return map;
	}

}
