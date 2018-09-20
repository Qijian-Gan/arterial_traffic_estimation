package estimation;

public class RateToNumber {

    /**
     *
     * @param Rate String
     * @return int: 0,1,2
     */
    public static int AtStopbar(String Rate){
        // This function is used to convert rate to number for stopbar detectors
        if(Rate.equals("Uncongested"))
            return 1;
        else if(Rate.equals("Congested/Queue Spillback"))
            return 2;
        else{
            System.out.println("Unknown Rate For Stopbar Detector!");
            return 0;
        }
    }

    /**
     *
     * @param Rate String
     * @return int: 0,1,2,3
     */
    public static int AtAdvance(String Rate){
        // This function is used to convert rate to number for advance detectors
        if(Rate.equals("Uncongested"))
            return 1;
        else if(Rate.equals("Congested"))
            return 2;
        else if(Rate.equals("Queue Spillback"))
            return 3;
        else{
            System.out.println("Unknown Rate For Advance Detector!");
            return 0;
        }
    }

}
