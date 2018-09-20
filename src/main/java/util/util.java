package util;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class util {

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
