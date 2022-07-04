package com.github.szysza26.recruitment;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class represent bar with status of progress fetch data from server
 */
public class Infobar extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel progress;

	Infobar() {
		super();
		
		progress = new JLabel("loading...");
		add(progress);
	}
	
	void showProgress(){
		progress.setVisible(true);
	}
	
	void hideProgress() {
		progress.setVisible(false);
	}
}
