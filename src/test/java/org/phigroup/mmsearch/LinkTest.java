package org.phigroup.mmsearch;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

public class LinkTest extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 402465605512436095L;

	public LinkTest() {
		JPanel p = new JPanel();

		JLabel link = new JLabel("Click here");
		link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		link.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 0) {
					if (Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						try {
							URI uri = new URI("http://github.com");
							desktop.browse(uri);
						} catch (IOException ex) {
							ex.printStackTrace();
						} catch (URISyntaxException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
		p.add(link);
		getContentPane().add(BorderLayout.NORTH, p);
	}

	public static void main(String[] args) {
		LinkTest linkTest = new LinkTest();
		linkTest.setSize(640, 100);
		linkTest.setVisible(true);
	}
}