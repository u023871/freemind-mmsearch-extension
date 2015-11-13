package org.phigroup.mmsearch;
/*
 * Created on 16.03.2005
 */

import java.io.*;
import java.util.*;

/**
 * @author Abramovich_E
 */
public class StrUtils {
    /**
     * Save string to file.
     */
    public static void saveStr(String fname, String text)
      throws IOException
    {
      PrintWriter out = new PrintWriter(new FileOutputStream(fname));
      out.print(text);
      out.close();
    }

    /**
     * Send data from one stream to another.
     * @see com.ibm.utl.Stream2Stream
     */
    public static void stream2stream(InputStream in, OutputStream out)
               throws IOException
    {
      int count = 0;
      final int BUF_SIZE = 5120;  // 5k
      byte[] buffer = new byte[BUF_SIZE];
      while ((count = in.read(buffer, 0, BUF_SIZE)) > 0)
        out.write(buffer, 0, count);
      out.flush();
    }

    /**
     * Load string from file.
     */
    public static String loadStr(String fname)
      throws FileNotFoundException, IOException
    {
      FileInputStream in = new FileInputStream(fname);
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      stream2stream(in, bytes);
      return bytes.toString();
    }

    /**
     * Replace a piece of "token" string
     * from "p1" to "p2" positions with "ins" string.
     */
    public static String replaceStr(String token, int p1, int p2, String ins) {
      StringBuffer sb = new StringBuffer(token.length()+p2-p1+1+ins.length());
      sb.append(token.substring(0,p1));
      sb.append(ins);
      sb.append(token.substring(p2));
      return sb.toString();
    }

    /**
     * Append string to file.
     */
    public static void appendStr(String fname, String s) {
      try {
        RandomAccessFile logout = new RandomAccessFile(fname, "rw");
        logout.seek(logout.length());
        logout.write(s.getBytes());
        logout.write('\n');
        logout.close();
      } catch (IOException ex) {
      }
    }

    /**
     * Replace all occurrences of "tag" string
     * with "gat" string in given string "s".
     */
    public static String replaceAll(String s, String tag, String gat){
      // scan string to determine how many replacements should be done
      int k = 0;
      int p = 0;
      while ((p = s.indexOf(tag, p))!=-1) {
        k++;
        p += tag.length();
      }
      // make actual replacements
      StringBuffer sb = new StringBuffer(s.length()+k*(gat.length()-tag.length()));
      p = 0;
      int t = 0;
      while ((p = s.indexOf(tag, p))!=-1) {
        sb.append(s.substring(t,p));
        sb.append(gat);
        p += tag.length();
        t = p;
      }
      sb.append(s.substring(t));
      return sb.toString();
    }

    /** Capitalize the first letter of string */
    public static String caps(String s) {
      if (s == null || s.length() == 0)
        return s;
      StringBuffer b = new StringBuffer(s);
      b.setCharAt(0, Character.toUpperCase(b.charAt(0)));
      return b.toString();
    }

    /** String of spaces */
    public static String space(int len) {
        StringBuffer sb = new StringBuffer(len);
        for (int i=0; i<len; i++)
            sb.append(' ');
        return sb.toString();
    }

    /** Align string to left with some character */
    public static String alignLeft(String str, char ch, int len) {
        StringBuffer buf = new StringBuffer(Math.max(len, str.length()));
        buf.append(str);
        for (int i=str.length(); i<len; i++) buf.append(ch);
        return buf.toString();
    }

    /** Align string to right with some character */
    public static String alignRight(String str, char ch, int len) {
        StringBuffer buf = new StringBuffer(Math.max(len, str.length()));
        for (int i=str.length(); i<len; i++) buf.append(ch);
        buf.append(str);
        return buf.toString();
    }

    /** Align string to left with spaces */
    public static String alignLeft(String str, int len) {
        return alignLeft(str, ' ', len);
    }

    /** Align string to right with spaces */
    public static String alignRight(String str, int len) {
        return alignRight(str, ' ', len);
    }

    /** Return file contents as a list of strings. */
    public static List readList(String fname) throws IOException {
        List list = new LinkedList();
        BufferedReader in = new BufferedReader(new FileReader(fname));
        while (in.ready()) {
            String s = in.readLine();
            if (s==null) break;
            list.add(s);
        }
        in.close();
        return list;
    }

    /**
     * Indent input string as an array of lines,
     * divided with "\n" or "\n\r".
     * @param s    input string
     * @param TAB  string to insert before each line
     */
    public static String indentStr(String s, String TAB) {
      // count lines to allocate StringBuffer properly
      int count = 1;
      int k = 0; // position to process
      while (true) {
        k = s.indexOf('\n', k);
        if (k == -1) break;
        count++;
        k++;
      }

      // indent lines
      StringBuffer b = new StringBuffer(s.length() + count * TAB.length());
      b.append(TAB);
      k = 0;
      while (true) {
        int p = s.indexOf('\n', k);
        if (p == -1)
          break;
        p++;
        if (p == s.length())
          break;
        if (s.charAt(p)=='\r') {
          p++;
          if (p==s.length()) break;
        }
        b.append(s.substring(k, p) + TAB);
        k = p;
      }
      b.append(s.substring(k));
      return b.toString();
    }

}
