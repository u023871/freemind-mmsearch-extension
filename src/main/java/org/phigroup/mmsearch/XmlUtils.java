package org.phigroup.mmsearch;
/*
 * Created on 22.03.2005
 */
import java.util.ArrayList;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Abramovich_E
 */
public class XmlUtils {

    public static Node[] getElements(Node parent, String name) {
        ArrayList<Node> result = new ArrayList<>();
        NodeList nodelist = parent.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE
             && node.getNodeName().equals(name))
                result.add(node);
        }
        Node[] arr = new Node[result.size()];
        result.toArray(arr);
        return arr;
    }

    public static Node getElement(Node parent, String name) {
        NodeList nodelist = parent.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE
             && node.getNodeName().equals(name))
                return node;
        }
        return null;
    }

    public static String getText(Node parent) {
        NodeList list = parent.getChildNodes();
        for (int i=0; i<list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType()==Node.TEXT_NODE) {
                return node.getNodeValue();
            }
        }
        return null;
    }

    public static String getAttribute(Node parent, String name) {
        NamedNodeMap list = parent.getAttributes();
        for (int i=0; i<list.getLength(); i++) {
            Attr attr = (Attr) list.item(i);
            if (attr.getName().equals(name)) {
                return attr.getValue();
            }
        }
        return null;
    }

}
