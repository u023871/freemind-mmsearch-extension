package org.phigroup.mmsearch;
import java.io.File;

/**
 * @author Abramovich_E
 */
public class MMNode implements Cloneable {
    public String name;
    public MMNode[] subNodes;
    public boolean filtered;
    public File mmFilePath;

    public String toString() {

//    	String link = "<html><a href='file://" + mmFilePath + "'>" + name + "</a></html>";
    	return name;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
