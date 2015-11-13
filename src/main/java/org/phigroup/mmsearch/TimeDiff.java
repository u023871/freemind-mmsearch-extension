package org.phigroup.mmsearch;
/*
 * Created on 16.03.2005
 */

/**
 * @author Abramovich_E
 */
public class TimeDiff {

    /**
     * Represents time interval as a string.<br>
     * Indicates how many years/months/weeks/../seconds have passed.<br>
     * Precision can be indicated. For example, if "kmax=2",
     * results can look like<br>
     * "2 hours 20 mins" or "10 mins 30 secs"<br>
     * i.e. only 2 time units are used.
     *
     * @param diff   time interval in milliseconds.
     * @param kmax   presision. Number of time units to show.
     * @param round  type of round-up. "True" means standard round-up,
     *               "false" -- "floor" truncation.
     */
    public static String timeInterval(long diff, int kmax, boolean round) {
      boolean negative = false;
      if (diff<0) {
        negative = true;
        diff = -diff;
      }
      int[] tlen = {1000, 60, 60, 24, 7};
      float[] flen = {364.25f/12, 364.25f};
      String[] tnames = {"sec","min","hour","day","week","month","year"};
      int[] term = new int[5];
      int ms = (int)(diff%tlen[0]);
      diff /= tlen[0];
      int sec = (int)(diff%tlen[1]);
      diff /= tlen[1];
      int min = (int)(diff%tlen[2]);
      diff /= tlen[2];
      term[0] = (int)(diff%tlen[3]);  // hours
      diff /= tlen[3];        // now we have days in "diff"
      term[4] = (int)(diff/flen[1]);  // years
      diff -= (int)(term[4]*flen[1]);
      term[3] = (int)(diff/flen[0]);  // months
      diff -= (int)(term[3]*flen[0]);
      term[2] = (int)(diff/tlen[4]);  // weeks
      diff %= tlen[4];
      term[1] = (int)diff;      // days
      // calculate roundups
      int[] roundup = new int[7];
      for (int i=0; i<roundup.length; i++)
        roundup[i] = 0;
      if (round) {
        // this code applies standard round-up instead of "floor" truncation.
        if (2*ms>=tlen[0]) roundup[0]++;
        if (2*(sec+roundup[0])>=tlen[1]) roundup[1]++;
        if (2*(min+roundup[1])>=tlen[2]) roundup[2]++;
        if (2*(term[0]+roundup[2])>=tlen[3]) roundup[3]++;
        if (2*(term[1]+roundup[3])>=tlen[4]) roundup[4]++;
        if (2*(term[2]+roundup[4])>=flen[0]/7) roundup[5]++;
        if (2*(term[3]+roundup[5])>=12) roundup[6]++;
      }
      // build string representation
      String q = "";
      if (negative) q = "-";
      int k = 0;
      for (int i=4; i>=0; i--) {
        if (k==(kmax-1))
          term[i] += roundup[i+2];
        if (term[i]!=0 && k<kmax) {
          q += term[i]+" "+tnames[i+2]+(term[i]==1?"":"s")+" ";
          k++;
        }
      }
      if (min!=0 && k<kmax) {
        q += min+" "+tnames[1]+" ";
        k++;
      }
      if (k<kmax) {
        q += sec;
        k++;
        if (k<kmax) {
          q += "."+StrUtils.alignLeft(Integer.toString(ms),'0',3);
        }
        q += " "+tnames[0];
      }
      return q;
    }

    public static String timeDiff(long diff) {
        return timeInterval(diff, 2, true);
    }

    public static String timeFrom(long moment) {
        return timeDiff(System.currentTimeMillis()-moment);
    }

}
