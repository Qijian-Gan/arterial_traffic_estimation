package connectors;

import java.io.IOException;
import main.MainFunction;

/**
 * Created by Qijian-Gan on 10/11/2017.
 */
public class extractNetworkFile{

    /**
     *
     * @param AimsunExe: Name of the aimsun exe file
     */
    public static void extractFromAimsun(String AimsunExe) {
        // This function is used to extract the network files from Aimsun
        try {
            String commandLine = AimsunExe+" -script " +
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
