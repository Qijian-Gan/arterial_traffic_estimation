package estimation;

import commonClass.forEstimation.AggregatedTrafficStates;
import commonClass.forEstimation.DetectorTypeByMovement;
import commonClass.forEstimation.TrafficStateByDetectorType;
import commonClass.forGeneralNetwork.detector.DetectorMovementProperty;
import commonClass.parameters.Parameters;

import java.util.List;

public class aggregateTrafficStates {

    /**
     *
     * @param detectorMovementProperties List<DetectorMovementProperty> class
     * @param detectorTypeByMovement DetectorTypeByMovement class
     * @param trafficStateByDetectorGroups List<TrafficStateByDetectorType> class
     * @param DetectorGroup String
     * @param parameters Parameters
     * @return AggregatedTrafficStates class
     */
    public static AggregatedTrafficStates CheckAllMovements(
            List<DetectorMovementProperty> detectorMovementProperties,DetectorTypeByMovement detectorTypeByMovement,
            List<TrafficStateByDetectorType> trafficStateByDetectorGroups,String DetectorGroup,Parameters parameters){
        // This is the function to check aggregated state for traffic movements

        // Initialization of aggregated states for three different movements:[Left turn, Through, Right turn]
        // Params: Rates[], Occs[], Lanes[], ThresholdLow[], ThresholdHigh[]
        AggregatedTrafficStates aggregatedTrafficStates=new AggregatedTrafficStates(new double[]{0,0,0},
                new double[]{0,0,0},new double[]{0,0,0},new double[]{0,0,0},new double[]{0,0,0});
        if(detectorMovementProperties.size()>0) {
            int NumType = detectorMovementProperties.size();
            // Check left-turn movement
            double [] RateOCCLanesLeft=CheckIndividualMovement(NumType, detectorTypeByMovement.getLeft()
                    ,detectorMovementProperties, trafficStateByDetectorGroups, parameters, "Left",DetectorGroup);
            // Check through movement
            double [] RateOCCLanesThrough=CheckIndividualMovement(NumType, detectorTypeByMovement.getThrough()
                    ,detectorMovementProperties, trafficStateByDetectorGroups, parameters, "Through",DetectorGroup);
            // Check right-turn movement
            double [] RateOCCLanesRight=CheckIndividualMovement(NumType, detectorTypeByMovement.getRight()
                    ,detectorMovementProperties, trafficStateByDetectorGroups, parameters, "Right",DetectorGroup);

            double [] Rates=new double[]{RateOCCLanesLeft[0],RateOCCLanesThrough[0],RateOCCLanesRight[0]};
            double [] Occs=new double[]{RateOCCLanesLeft[1],RateOCCLanesThrough[1],RateOCCLanesRight[1]};
            double [] Lanes=new double[]{RateOCCLanesLeft[2],RateOCCLanesThrough[2],RateOCCLanesRight[2]};
            double [] ThresholdLow=new double[]{RateOCCLanesLeft[3],RateOCCLanesThrough[3],RateOCCLanesRight[3]};
            double [] ThresholdHigh=new double[]{RateOCCLanesLeft[4],RateOCCLanesThrough[4],RateOCCLanesRight[4]};
            aggregatedTrafficStates=new AggregatedTrafficStates(Rates,Occs,Lanes,ThresholdLow,ThresholdHigh);
        }
        return aggregatedTrafficStates;
    }


    /**
     *
     * @param NumType Number of detector types
     * @param PossibleMovement String []
     * @param detectorMovementProperties List<DetectorMovementProperty> class
     * @param trafficStateByDetectorGroups List<TrafficStateByDetectorType> class
     * @param parameters Parameters
     * @param Movement String
     * @param DetectorGroup String
     * @return double[]{Rate,Occ,TotLanes,ThresholdLow,ThresholdHigh}
     */
    public static double [] CheckIndividualMovement(int NumType, String [] PossibleMovement
            , List<DetectorMovementProperty> detectorMovementProperties, List<TrafficStateByDetectorType> trafficStateByDetectorGroups
            , Parameters parameters, String Movement, String DetectorGroup){
        // This is the function to check aggregated state for individual traffic movement

        double Rate=0;
        double Occ=0;
        double TotLanes=0;
        double ThresholdLow=0;
        double ThresholdHigh=0;
        int NumMatchedDetectorMovement=0;
        for(int i=0;i<NumType;i++){ // Loop for each detector type
            for(int j=0;j<PossibleMovement.length;j++){ // Loop for each possible detector type
                if(PossibleMovement[j].equals(detectorMovementProperties.get(i).getMovement())){
                    // Find the corresponding movement
                    if(!trafficStateByDetectorGroups.get(i).getRate().equals("Unkonwn")){
                        // Get the proportions
                        double Proportion=parameterOperations.FindTrafficProportion(detectorMovementProperties.get(i).getMovement(),
                                parameters,Movement);

                        if(DetectorGroup.equals("Exclusive Left Turn")||DetectorGroup.equals("Exclusive Right Turn")
                                ||DetectorGroup.equals("General Stopbar Detectors")) {
                            Rate = Rate + RateToNumber.AtStopbar(trafficStateByDetectorGroups.get(i).getRate()) *
                                    trafficStateByDetectorGroups.get(i).getTotLanes() * Proportion;
                        }else if(DetectorGroup.equals("Advance Detectors")){
                            Rate = Rate + RateToNumber.AtAdvance(trafficStateByDetectorGroups.get(i).getRate()) *
                                    trafficStateByDetectorGroups.get(i).getTotLanes() * Proportion;
                        }else{
                            System.out.println("Wrong input of detector group!");
                            System.exit(-1);
                        }

                        Occ=Occ+trafficStateByDetectorGroups.get(i).getAvgOcc()*trafficStateByDetectorGroups.get(i).getTotLanes()
                                *Proportion;
                        TotLanes=TotLanes+trafficStateByDetectorGroups.get(i).getTotLanes()*Proportion;
                        NumMatchedDetectorMovement=NumMatchedDetectorMovement+1;
                        ThresholdLow=ThresholdLow+trafficStateByDetectorGroups.get(i).getThresholds()[0];
                        ThresholdHigh=ThresholdHigh+trafficStateByDetectorGroups.get(i).getThresholds()[1];
                    }
                    break;
                }
            }
        }
        if(TotLanes>0){
            Rate=Rate/TotLanes;
            Occ=Occ/TotLanes;
            ThresholdLow=ThresholdLow/NumMatchedDetectorMovement;
            ThresholdHigh=ThresholdHigh/NumMatchedDetectorMovement;
        }
        return new double[]{Rate,Occ,TotLanes,ThresholdLow,ThresholdHigh};
    }

}
