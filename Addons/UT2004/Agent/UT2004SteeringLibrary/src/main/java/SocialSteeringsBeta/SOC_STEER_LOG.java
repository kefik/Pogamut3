package SocialSteeringsBeta;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;


/**
 *
 * @author Petr
 * DEBUG class
 */
public class SOC_STEER_LOG {

    public static boolean DEBUG = true;
    private static HashMap<String, StringBuilder> log = new HashMap<String, StringBuilder>();
    private static Calendar calendar = new GregorianCalendar();

     public static HashMap<String, Location> logFlags = new HashMap<String,Location>();
   // private static StoryController controller;

    @Deprecated
    private static final String KDefault = "default";
    private static final String KPath = "./log/quarrel/";

    public static final String KParse = "parse";
    public static final String KError = "error";
    public static String KAnimation = "anim";
    public static String KMinibeatChain = "MinibeatChain";
    static String KSync = "sync";

    @Deprecated
    public static void AddLogLine(String s) {

        AddLogLine(s, KDefault);
    }
    /**
     message, file
     * @param message
     * @param file 
     */
    public static synchronized void AddLogLine(String message, String file) {
        if (log.get(file) == null) {
            log.put(file, new StringBuilder(""));

        }
        log.get(file).append("\n\r\n");
        log.get(file).append(message);
    }

    @Deprecated
    public static void AddLogLineWithDate(String mes) {
        AddLogLineWithDate(mes, KDefault);
    }

    /**
     message, file
     * @param mes
     * @param file
     */
    public static synchronized void AddLogLineWithDate(String mes, String file) {
        
        AddLogLine(Date(), file);
        AddLogLine(mes, file);
    }
    
    @Deprecated
    public static void PrintLog() {
        PrintLog(KDefault);
    }
    public static void PrintLog(String file) {
        if (log.get(file) == null) {
            return;
        }
        String ret = log.get(file).toString();
        if (file != null) {
            System.out.print(ret);
        }
    }

    @Deprecated
    public static void DiscardLog() {
        DiscardLog(KDefault);
    }
    public static void DiscardLog(String file) {
        log.remove(file);
    }

    /**
     *
     * @param file
     */
    public static void DumpToFile(String file) {
        if (log.get(file) == null) {
            return;
        }
        String ret = log.get(file).toString();
        String path = KPath + file/* + Date() */+ "SOC.log";
        try {
            // Create file
            File f = new File(path);
            if(!f.exists())
            {
                f.createNewFile();
            }else
            {
                ret = "\n\r\nNEW LOG\n\r\n" + ret;
            }
            FileWriter fstream = new FileWriter(path);
            BufferedWriter out = new BufferedWriter(fstream);

            out.write(ret);
            //Close the output stream
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void DumpAllContent()
    {
        for(String s : log.keySet())
        {
            DumpToFile(s);
        }
    }

    private static String Date()
    {
        calendar = new GregorianCalendar();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);

        return "\nTIME--" + hour + "-" + minute + "-" + second + "-" + millisecond;

    }

    @Deprecated
    public static void DumpToFile() {
        DumpToFile(KDefault);
    }


//    public static void setController(StoryController c)
//    {
//        controller = c;
//    }
    public static void setNewFlag(String name, Location place)
    {
        logFlags.put(name, place);
        //debuging command ktery placne do sceny nejaky token na danou pozici..
        //CommandSpawnActor c = new CommandSpawnActor();
        //c.setId(name);
        //c.setLocation(place);
        //c.setType("GameBotsUE2.GBTextureMarker");
        //nebo
        //c.setType("GameBotsUE2.GBPlaneMarker");
        //controller.getControlServer().getAct().act(c);
      
    }
    public static void removeFlag(String name)
    {
       logFlags.remove(name);
    }
}
