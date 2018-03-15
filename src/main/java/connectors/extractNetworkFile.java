package connectors;

import java.io.IOException;
import main.MainFunction;

/**
 * Created by Qijian-Gan on 10/11/2017.
 */
public class extractNetworkFile{

    public static void extractFromAimsun() {
        // This function is used to extract the network files from Aimsun
        try {
            String commandLine = "aimsun.exe -script " +
                    MainFunction.cBlock.AimsunFolder + "\\" + MainFunction.cBlock.AimsunNetExtractionScriptName + " " +
                    MainFunction.cBlock.AimsunFolder + "\\" + MainFunction.cBlock.AimsunFileName + " " +
                    MainFunction.cBlock.JunctionYes + " " + MainFunction.cBlock.SectionYes + " " +
                    MainFunction.cBlock.DetectorYes + " " + MainFunction.cBlock.SignalYes + " " + MainFunction.cBlock.AimsunFolder;
            Process p = Runtime.getRuntime().exec(commandLine);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
