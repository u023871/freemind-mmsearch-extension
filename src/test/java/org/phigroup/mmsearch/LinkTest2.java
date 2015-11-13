package org.phigroup.mmsearch;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JApplet;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/** * * @author Ian Schneider */
public class LinkTest2 extends JApplet {

	/** Creates a new instance of ControlApplet */
	public LinkTest2() {
	}

	protected Link[] createLinks() {
		try {
			return new Link[] { new Link("Google", "http://www.google.com"),
					new Link("Java", "http://java.sun.com") };
		} catch (MalformedURLException murle) {
			throw new RuntimeException("you typed in a bad URL", murle);
		}
	}

	public void init() {
		setLayout(new BorderLayout());
		JScrollPane scroller = new JScrollPane();
		final JTree tree = new JTree(buildRoot());
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.getSelectionModel().addTreeSelectionListener(
				new TreeSelectionListener() {
					public void valueChanged(TreeSelectionEvent e) {
						TreePath path = tree.getSelectionPath();
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
								.getLastPathComponent();
						Link l = (Link) node.getUserObject();
						showStatus("Opening page : " + l.display + " ( "
								+ l.url + " )");
						getAppletContext().showDocument(l.url, l.display);
					}
				});
		scroller.setViewportView(tree);
		add(scroller, BorderLayout.CENTER);
	}

	private TreeNode buildRoot() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		Link[] links = createLinks();
		for (int i = 0, ii = links.length; i < ii; i++) {
			root.add(new DefaultMutableTreeNode(links[i]));
		}
		return root;
	}

	static class Link {
		String display;
		URL url;

		public Link(String display, String url) throws MalformedURLException {
			this.display = display;
			this.url = new URL(url);
		}

		public String toString() {
			return display;
		}
	}
}