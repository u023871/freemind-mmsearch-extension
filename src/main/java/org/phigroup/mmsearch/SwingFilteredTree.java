package org.phigroup.mmsearch;
/*
 * Created on 16.03.2005
 */

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * @author Abramovich_E
 */
public class SwingFilteredTree {

    JTree tree;
    JPanel treePanel;
    JTextField treeText;
    JButton treeButton;
    JLabel treeStatus;

    String searchCriteria;
    MMNode unfiltered;

    public void initTree(MMNode resultRoot) {
        unfiltered = resultRoot;
        DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(resultRoot);
        wrapTree(treeRoot);
        tree = new JTree(treeRoot);
        JScrollPane scrollTree = new JScrollPane(tree);
        scrollTree.setPreferredSize(new Dimension(800,600));

        //
        // search field panel
        //
        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Search criteria: "));
        treeText = new JTextField(30);
        filterPanel.add(treeText);
        treeButton = new JButton("Apply");
        filterPanel.add(treeButton);

        //
        // status panel
        //
        //JPanel statusPanel = new JPanel();
        treeStatus = new JLabel();
        treeStatus.setPreferredSize(new Dimension(400,20));
        treeStatus.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
        JPanel downPanel = createPanel(filterPanel, treeStatus);
        treePanel = createPanel(scrollTree, downPanel);

        treeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyFilter();
            }
        });

        treeText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyFilter();
            }
        });

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent evt) {
                TreePath[] paths = evt.getPaths();
                treeStatus.setText("");
                for (int i = 0; i < paths.length; i++) {
                    if (evt.isAddedPath(i)) {
                        DefaultMutableTreeNode mut = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
                        if (mut.getUserObject() instanceof MMNode) {
                            MMNode dir = (MMNode) mut.getUserObject();

                            int count = countLeaves(dir);
                            treeStatus.setText("  "
                                    + countLeavesStr(count)
                            );

                            // LI: add link opening on tree selection
                            if (Desktop.isDesktopSupported()) {
        						Desktop desktop = Desktop.getDesktop();
        						try {

        							if(dir.mmFilePath != null) {

        								desktop.open(dir.mmFilePath);
        							}
        						} catch (IOException ex) {
        							ex.printStackTrace();
        						}
        					}

                        }
                    }
                }
            }
        });
    }

    String countLeavesStr(int count) {
        if (count%10==1) return count+" leaf";
        return count +" leaves";
    }

    int countSubnodes(MMNode dir) {
        int count = 0;
        for (int i=0; i<dir.subNodes.length; i++)
            count += countSubnodes(dir.subNodes[i])+1;
        return count;
    }

    int countLeaves(MMNode dir) {
        int count = dir.subNodes.length==0? 1:0;
        for (int i=0; i<dir.subNodes.length; i++)
            count += countLeaves(dir.subNodes[i]);
        return count;
    }

    JPanel createPanel(JComponent comp1, JComponent comp2) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(comp1, BorderLayout.CENTER);
        panel.add(comp2, BorderLayout.SOUTH);
        return panel;
    }

    void wrapTree(DefaultMutableTreeNode mut1) {
        MMNode dir1 = (MMNode) mut1.getUserObject();
        DefaultMutableTreeNode mut2;
        for (int i=0; i<dir1.subNodes.length; i++) {
            MMNode dir2 = dir1.subNodes[i];
            mut2 = new DefaultMutableTreeNode(dir2);
            mut1.add(mut2);
            wrapTree(mut2);
        }
    }

    void applyFilter() {
        try {
            searchCriteria = treeText.getText();
            searchCriteria = searchCriteria.toLowerCase();
            print("Apply search criteria "+searchCriteria);

            long start = System.currentTimeMillis();
            MMNode dir = deepClone(unfiltered);
            filterMM(dir);
            print("Cloning time: "+TimeDiff.timeFrom(start));

            DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(dir);
            wrapTree(treeRoot);
            tree.setModel(new DefaultTreeModel(treeRoot));
        } catch (CloneNotSupportedException e) {
        }
    }

    private void print(String s) {
        System.out.println(s);
    }

    MMNode deepClone(MMNode dir) throws CloneNotSupportedException {
        MMNode clo = (MMNode) dir.clone();
        clo.subNodes = new MMNode[dir.subNodes.length] ;
        for (int i=0; i<dir.subNodes.length; i++)
            clo.subNodes[i] = deepClone(dir.subNodes[i]);
        return clo;
    }

    void filterMM(MMNode dir) {
        checkFilter(dir);
        removeUnfiltered(dir);
    }

    boolean satisfyFilter(String s) {

    	boolean b = false;

    	if(s != null && s.length() > 0) {

        	s = s.toLowerCase();
        	b = s.indexOf(searchCriteria) >= 0;
    	}

        return b;
    }

    public void checkFilter(MMNode dir) {
        if (satisfyFilter(dir.name)) {
            dir.filtered = true;
        }
        for (int i=0; i<dir.subNodes.length; i++) {
            MMNode dir2 = dir.subNodes[i];
            checkFilter(dir2);
            if (dir2.filtered) dir.filtered = true;
        }
    }

    void removeUnfiltered(MMNode dir) {
        List<MMNode> result = new LinkedList<>();
        for (int i=0; i<dir.subNodes.length; i++) {
            MMNode dir2 = dir.subNodes[i];
            if (dir2.filtered) {
                removeUnfiltered(dir2);
                result.add(dir2);
            }
        }
        dir.subNodes = (MMNode[]) result.toArray(new MMNode[result.size()]);
    }

    public JPanel getContentPanel() {
        return treePanel;
    }


}
