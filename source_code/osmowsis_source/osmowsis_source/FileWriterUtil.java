import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileWriterUtil {

    public static BufferedWriter writer;

    public static void initialize(){
        try{
            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date date = new Date();
            writer = new BufferedWriter(new FileWriter("output_"+ dateFormat.format(date) + ".txt"));
        }
        catch (IOException ex){
            System.out.println(ex.toString());
        }
    }

    public static void write(String s){
        try{
            writer.write(s);
        }
        catch (IOException ex){
            System.out.println(ex.toString());
        }
    }

    public static void writeLine(String s){
        try{
            write(s);
            writer.newLine();
        }
        catch (IOException ex){
            System.out.println(ex.toString());
        }

    }

    public static void closeFile(){
        try{
            writer.close();
        }
        catch (IOException ex){
            System.out.println(ex.toString());
        }

    }
}
