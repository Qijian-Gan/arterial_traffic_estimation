package util;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class util {

    /**
     *
     * @param DateNum YYYYMMDD
     * @return DateString MM/DD/YYYY
     */
    public static String convertDateNumToDateString(int DateNum){
        //This function is used to convert data string to integer with slash

        int Year=Math.floorDiv(DateNum,10000);
        int Month=Math.floorDiv(Math.floorMod(DateNum,10000),100);
        int Day=Math.floorMod(DateNum,100);

        String DateString;
        if(Month<10){
            DateString="0"+Month+"/";
        }else{
            DateString=Month+"/";
        }
        if(Day<10){
            DateString=DateString+"0"+Day+"/";
        }else{
            DateString=DateString+Day+"/";
        }
        DateString=DateString+Year;
        return DateString;
    }
    /**
     *
     * @param DateStr: MM/DD/YYYY
     * @return Date: YYYYMMDD
     */
    public static int convertDateStringToIntegerWithSlash(String DateStr){
        //This function is used to convert data string to integer with slash

        String [] tmpString=DateStr.split("/");
        int Date=Integer.parseInt(tmpString[2])*10000+Integer.parseInt(tmpString[0])*100+Integer.parseInt(tmpString[1]);
        return Date;
    }

    /**
     *
     * @param TimeStr HH:MM:SS AM/PM
     * @return TotSeconds: seconds (Integer)
     */
    public static int convertTimeStringToSecondsWithColon(String TimeStr){
        // This function is used to convert time string to seconds (integer) with colon
        String [] tmpString=TimeStr.split(" ");
        String [] TimeIn12HourStr=(tmpString[0].trim()).split(":");
        String AMOrPM=tmpString[1];
        int Hour=Integer.parseInt(TimeIn12HourStr[0].trim());
        if(Hour==12){// First, check the case of 12:??:??
            Hour=0;
        }
        if(AMOrPM.equals("PM")){
            Hour=Hour+12;
        }
        int Minute=Integer.parseInt(TimeIn12HourStr[1].trim());
        int Second=Integer.parseInt(TimeIn12HourStr[2].trim());

        int TotSeconds=Hour*3600+Minute*60+Second;
        return TotSeconds;
    }

    /**
     *
     * @param Input double[]
     * @return double
     */
    public static double MeanWithoutZero(double [] Input){
        // This function returns the mean for non-zero values

        double Output=0;
        double NumNonZero=0;
        for(int i=0;i<Input.length;i++){
            if(Input[i]!=0){
                NumNonZero=NumNonZero+1;
                Output=Output+Input[i];
            }
        }
        if(NumNonZero>0){
            Output=Output/NumNonZero;
        }
        return Output;
    }

    //************************************************************************************
    // Insert into database
    //************************************************************************************
    /**
     *
     * @param ps SQL statement
     * @param string List<String> SQL strings
     * @param definedSize Defined size in each batch
     * @return true/false
     */
    public static boolean insertSQLBatch(Statement ps, List<String> string, int definedSize){
        // This function is used to insert SQL batch
        // definedSize: Can not set to high. Or else we will lose a lot of data
        int curSize=0;
        List<String> tmpString= new ArrayList<String>();
        try {
            for (int i = 0; i < string.size(); i++) {
                ps.addBatch(string.get(i));
                tmpString.add(string.get(i));
                curSize=curSize+1;
                if(curSize==definedSize || i==string.size()-1){
                    try {
                        ps.executeBatch();
                    }catch (SQLException e){
                        ps.clearBatch();
                        insertLineByLine(ps, tmpString);
                    }
                    curSize=0;
                    tmpString=new ArrayList<String>();
                    ps.clearBatch();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param ps SQL statement
     * @param string List<String> SQL strings
     * @return true
     */
    public static boolean insertLineByLine(Statement ps, List<String> string){
        // This function is used to insert line by line

        for (int i=0;i<string.size();i++){
            try{
                ps.execute(string.get(i));
            } catch (SQLException e) {
                System.out.println("Fail to insert: "+e.getMessage());
            }
        }
        return true;
    }

}
