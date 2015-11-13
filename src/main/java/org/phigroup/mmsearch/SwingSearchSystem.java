package org.phigroup.mmsearch;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Search MindMaps.
 * Swing interface provided.
 * @author E.Abramovich
 */
public class SwingSearchSystem {

    Properties properties;

    /**
     * Load current program state from property file.
     */
    void loadProperties() {
        properties = new Properties();
        try {
            FileInputStream in = new FileInputStream("SearchSystem.properties");
            properties.load(in);
            in.close();
        } catch (IOException e) {
        }
    }

    /**
     * Save current program state into property file.
     */
    void saveProperties() throws IOException {
        FileOutputStream out = new FileOutputStream("SearchSystem.properties");
        properties.store(out, "saving search system properties");
        out.close();
    }

    String searchCriteria;
    String startingPath;
    int maxCount = 0;

    void setSearchCriteria() throws IOException {
        searchCriteria = JOptionPane.showInputDialog(null, "Search criteria", searchCriteria);
        if (searchCriteria==null) System.exit(0);
        properties.setProperty("searchCriteria", searchCriteria);
        saveProperties();
        print("Search criteria: "+searchCriteria);
    }

    boolean startsWithDriveLetter(String path) {
        if (path==null || path.length()<2) return false;
        char drive = Character.toLowerCase(path.charAt(0));
        if (drive<'a' || drive>'z') return false;
        return path.charAt(1)==':';
    }

    /**
     * Set name of directory with mindmaps interactively.
     */
    void setStartingPath() throws IOException {
        startingPath = JOptionPane.showInputDialog(null,
                        "Starting path", startingPath);
        if (startingPath==null) System.exit(0);
        startingPath = startingPath.trim();
        if (startingPath.length()==0) startingPath = ".";
        startingPath = startingPath.replace('/','\\');
        if (!startsWithDriveLetter(startingPath) && !startingPath.startsWith("\\") && !startingPath.startsWith(".")) startingPath = '\\'+startingPath;
        if (startingPath.endsWith("\\")) startingPath = startingPath.substring(0,startingPath.length()-1);
        properties.setProperty("startingPath", startingPath);
        saveProperties();
        print("Starting path: "+startingPath);
    }

    private void print(String s) {
        System.out.println(s);
    }

    Node getElement(Node cur) {
        return XmlUtils.getElement(cur, "node");
    }

    Node[] getElements(Node cur) {
        return XmlUtils.getElements(cur, "node");
    }

    String getMMText(Node cur) {
        return XmlUtils.getAttribute(cur, "TEXT");
    }

    public MMNode scanMM(Node curRec) {
        MMNode curMM = new MMNode();
        curMM.name = getMMText(curRec);
        List<MMNode> subNodes = new ArrayList<>();
        Node[] recs = getElements(curRec);
        for (int i=0; i<recs.length; i++) {
            Node rec = recs[i];
            MMNode mm = scanMM(rec);
            subNodes.add(mm);
        }
        curMM.subNodes = (MMNode[]) subNodes.toArray(new MMNode[subNodes.size()]);
        return curMM;
    }

    /**
     * wrap file into node
     * @param fname
     * @param root
     * @return
     */
    MMNode wrapInFileNode(String fname, MMNode root) {
        MMNode froot = new MMNode();
        froot.subNodes = new MMNode[1];
        froot.subNodes[0] = root;
        froot.name = "["+fname+"]";

        String absolutePath = properties.get("startingPath") + File.separator + fname;

        System.out.println(absolutePath);
        froot.mmFilePath = new File(absolutePath);

        return froot;
    }

    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
        SwingSearchSystem ss = new SwingSearchSystem();
        ss.run();
    }

    List<String> getMindMapNames() {
        ArrayList<String> fnames = new ArrayList<>();
        String[] filelist = new File(startingPath).list();
        for (int i=0; i<filelist.length; i++) {
            if (!filelist[i].endsWith(".mm")) continue;
            fnames.add(filelist[i]);
            maxCount--;
            if (maxCount==0) break;
        }
        return fnames;
    }

    MMNode readMindMaps(List<String> fnames) throws SAXException, IOException, ParserConfigurationException {
        JLabel status = new JLabel();
        status.setPreferredSize(new Dimension(400,20));
        JFrame statusFrame = frame(status);

        long start = System.currentTimeMillis();
        List<MMNode> roots = new LinkedList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        for (Iterator<String> iter = fnames.iterator(); iter.hasNext();) {
            String fname = (String) iter.next();
            status.setText("File "+fname);
            System.out.print(".");
            Document doc = factory.newDocumentBuilder().parse(new File(startingPath, fname));
            MMNode root = scanMM(getElement(XmlUtils.getElement(doc, "map")));
            roots.add(wrapInFileNode(fname, root));
        }
        System.out.println();
        statusFrame.dispose();

        MMNode unfiltered = new MMNode();
        unfiltered.name = "Search mindmaps in "+startingPath;
        unfiltered.subNodes = (MMNode[]) roots.toArray(new MMNode[roots.size()]);
        print("Elapsed time: "+TimeDiff.timeFrom(start));

        return unfiltered;
    }

    public void run() throws SAXException, IOException, ParserConfigurationException {
        loadProperties();
        searchCriteria = properties.getProperty("searchCriteria");
        startingPath = properties.getProperty("startingPath");
        setStartingPath();

        if (maxCount>0)
            print("max count: "+maxCount);

        List<String> fnames = getMindMapNames();
        print(fnames.size()+" mindmaps found");

        MMNode unfiltered = readMindMaps(fnames);

        SwingFilteredTree ft = new SwingFilteredTree();
        ft.initTree(unfiltered);
        frame(ft.getContentPanel());
    }

    JFrame frame;

    private JFrame frame(JComponent comp) {
        frame = new JFrame();
        frame.setTitle("Search mindmaps");
        frame.getContentPane().add( comp, "Center" );
        frame.addWindowListener(new WindowListener() {

            public void windowActivated(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }

            public void windowDeactivated(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowOpened(WindowEvent e) {
            }

        });
    	frame.pack();
    	frame.setVisible(true);
    	return frame;
    }

}
