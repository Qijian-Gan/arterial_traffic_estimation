package main;

import java.io.IOException;

/**
 * Created by Qijian-Gan on 10/11/2017.
 */
public class extractNetworkFile_Main{

    public static void main(String[] args) {
        // This function is used to extract the network files from Aimsun
        try {
            String commandLine = "aimsun.exe -script" + " " + // Calling Aimsun exe
                    "L:\\arterial_detector_analysis_files\\Test\\AimsunStart.py" + " " + // Python script
                    "L:\\arterial_detector_analysis_files\\Test\\I-210-Pilot-(2017-12-12)c.ang" + " " + // Aimsun model
                    1 + " " + // Output junction information
                    1 + " " + // Output section information
                    1 + " " + // Output detector information
                    1 + " " + // Output signal information
                    "L:\\arterial_detector_analysis_files\\Test"; // Output folder
            Process p = Runtime.getRuntime().exec(commandLine);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
