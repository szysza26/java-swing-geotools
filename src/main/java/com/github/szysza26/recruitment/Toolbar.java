package com.github.szysza26.recruitment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.geotools.swing.action.NoToolAction;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ResetAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;

/**
 * This class represent toolbar with buttons to some default map actions and buttons for management refresh data from server
 */
public class Toolbar extends JPanel {

	private static final long serialVersionUID = 1L;

	Toolbar(App app) {
		super();

		JButton zoomInButton = new JButton(new ZoomInAction(app.getMap()));
		add(zoomInButton);

		JButton zoomOutButton = new JButton(new ZoomOutAction(app.getMap()));
		add(zoomOutButton);

		JButton panButton = new JButton(new PanAction(app.getMap()));
		add(panButton);

		JButton resetButton = new JButton(new ResetAction(app.getMap()));
		add(resetButton);

		JButton noAction = new JButton(new NoToolAction(app.getMap()));
		add(noAction);

		JButton aboutButton = new JButton("about");
		add(aboutButton);
		aboutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(getParent(),
						"Krzysztof Górzyński \n" + "email: szy162@gmail.com \n" + "phone: +48 884 395 806", "About",
						JOptionPane.PLAIN_MESSAGE);
			}
		});

		JButton reloadButton = new JButton("reload");
		add(reloadButton);
		reloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.reload();
			}
		});
		
		JButton enableButton = new JButton("enable");
		add(enableButton);
		JButton disableButton = new JButton("disable");
		add(disableButton);
		
		enableButton.setEnabled(false);
		
		enableButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.enableAutoRefresh();
				enableButton.setEnabled(false);
				disableButton.setEnabled(true);
			}
		});
		
		disableButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.disableAutoRefresh();
				enableButton.setEnabled(true);
				disableButton.setEnabled(false);
			}
		});
	}
}
