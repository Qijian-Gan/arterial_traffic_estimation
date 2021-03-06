package dataProvider;

import main.MainFunction;;
import commonClass.simulationData.*;
import commonClass.simulationData.Turning.*;
import commonClass.simulationData.Lane.*;
import commonClass.simulationData.Section.*;
import commonClass.simulationData.ControlTurn.*;
import commonClass.simulationData.ControlSignal.*;
import commonClass.simulationData.ControlPhase.*;
import commonClass.simulationData.Centroid.*;
import commonClass.simulationData.SimVehicle.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
/**
 * Created by Qijian-Gan on 5/30/2018.
 */

public class getSimulationData {
    // This is the simulation data provider

    //*********************************************************************
    // Get simulation statistics
    //*********************************************************************
    /**
     *
     * @param conSqlite Connection to the sqlite database
     * @param conMySQL Connection to the MySQL database
     * @param InputTime Input time (double, seconds)
     * @param TurnYes Get turn information?
     * @param LaneYes Get lane information?
     * @param SectionYes Get section information?
     * @param ControlTurnYes Get control turn information?
     * @param ControlSignalYes Get control signal information?
     * @param ControlPhaseYes Get control phase information?
     * @param CentroidYes Get centroid information?
     * @return SimulationStatistics class
     */
    public static SimulationStatistics GetSimulationStatistics(Connection conSqlite, Connection conMySQL, double InputTime
            ,boolean TurnYes, boolean LaneYes, boolean SectionYes,boolean ControlTurnYes, boolean ControlSignalYes
            , boolean ControlPhaseYes, boolean CentroidYes){
        // This function is used to get simulation statistics

        List<TurningStatisticsByObjectID> turningStatisticsByObjectIDList=new ArrayList<TurningStatisticsByObjectID>(); // Turning information
        List<LaneStatisticsByObjectID> laneStatisticsByObjectIDList=new ArrayList<LaneStatisticsByObjectID>(); // Lane information
        List<SectionStatisticsByObjectID> sectionStatisticsByObjectIDList=new ArrayList<SectionStatisticsByObjectID>(); // Section information
        List<ControlTurnStatisticsByObjectID> controlTurnStatisticsByObjectIDList=new ArrayList<ControlTurnStatisticsByObjectID>(); // Control turn information
        List<ControlSignalStatisticsByObjectID> controlSignalStatisticsByObjectIDList=new ArrayList<ControlSignalStatisticsByObjectID>(); // Control signal information
        List<ControlPhaseStatisticsByObjectID> controlPhaseStatisticsByObjectIDList=new ArrayList<ControlPhaseStatisticsByObjectID>(); // Control phase information

        List<CentroidStatistics> centroidStatisticsList=new ArrayList<CentroidStatistics>();
        if(TurnYes){// Get turn information
            turningStatisticsByObjectIDList=GetTurningStatistics(conSqlite, InputTime);
        }
        if(LaneYes){// Get lane information
            laneStatisticsByObjectIDList=GetLaneStatistics(conSqlite, InputTime);
        }
        if(SectionYes){// Get section information
            sectionStatisticsByObjectIDList=GetSectionStatistics(conSqlite, InputTime);
        }
        if(ControlTurnYes){// Get control turn information
            controlTurnStatisticsByObjectIDList=GetControlTurnStatistics(conSqlite, InputTime);
        }
        if(ControlSignalYes){// Get control signal information
            controlSignalStatisticsByObjectIDList=GetControlSignalStatistics(conSqlite, InputTime);
        }
        if(ControlPhaseYes){// Get control phase information
            controlPhaseStatisticsByObjectIDList=GetControlPhaseStatistics(conSqlite, InputTime);
        }
        if(CentroidYes){ // Get the centroid information
            centroidStatisticsList=GetCentroidStatistics(conMySQL,InputTime);
        }
        List<SimVehInfBySection> simVehInfBySectionList=GetVehicleTrajectories(conMySQL, InputTime);
        return (new SimulationStatistics(turningStatisticsByObjectIDList,laneStatisticsByObjectIDList,sectionStatisticsByObjectIDList,
                controlTurnStatisticsByObjectIDList,controlSignalStatisticsByObjectIDList,controlPhaseStatisticsByObjectIDList,
                centroidStatisticsList,simVehInfBySectionList));
    }

    //*********************************************************************
    // Get Sim_Inf
    //*********************************************************************
    /**
     *
     * @param con Database connection
     * @return SimInfFromSqlite class
     */
    public static SimInfFromSqlite GetSimInfFromSqlite(Connection con){
        // This function is used to get simulation information from the sqlite database

        // Check the SIM_INFO table
        List<Double> fromTimeList=new ArrayList<Double>();
        List<Integer> objectIDList=new ArrayList<Integer>();
        List<Long> execDateTimeList=new ArrayList<Long>();
        try {
            Statement ps=con.createStatement();
            ResultSet resultset=ps.executeQuery("select did,from_time, exec_date from SIM_INFO");
            while(resultset.next()){ // Always get the last one.
                objectIDList.add(resultset.getInt("did"));
                fromTimeList.add(resultset.getDouble("from_time"));
                String execDateTimeStr=resultset.getString("exec_date");
                execDateTimeList.add(ConvertDateTimeStringToInteger(execDateTimeStr));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new SimInfFromSqlite(fromTimeList,objectIDList,execDateTimeList);
    }

    /**
     *
     * @param DateTimeStr String
     * @return long DateTime
     */
    public static long ConvertDateTimeStringToInteger(String DateTimeStr){
        // This function is used to convert current date & time into an integer

        String[] strings=DateTimeStr.split("T"); // In mysqlite database, Date and Time are separated by "T"
        String[] DateStr=strings[0].split("-");
        long Year=Long.parseLong(DateStr[0].trim());
        long Month=Long.parseLong(DateStr[1].trim());
        long Day=Long.parseLong(DateStr[2].trim());

        String[] TimeStr=strings[1].split(":");
        long Hour=Long.parseLong(TimeStr[0].trim());
        long Minute=Long.parseLong(TimeStr[1].trim());
        long Second=Long.parseLong(TimeStr[2].trim());

        long DateTime=(((Year*10000+Month*100+Day)*100+Hour)*100+Minute)*100+Second;
        return DateTime;
    }

    //*********************************************************************
    // Get the microscopic turning information
    //*********************************************************************
    /**
     *
     * @param con Database connection
     * @param InputTime Input time (double, seconds)
     * @return List<TurningStatisticsByObjectID> class
     */
    public static List<TurningStatisticsByObjectID> GetTurningStatistics(Connection con, double InputTime){
        // This function is used to get the turning statistics with the given InputTime
        // If the InputTime is not of integer number of 300 seconds (5 minutes), get the floor of it (the statistics in the
        // past 5 minutes)

        // Check the SIM_INFO table
        SimInfFromSqlite simInfFromSqlite=GetSimInfFromSqlite(con);
        List<Double> fromTimeList=simInfFromSqlite.getFromTimeList();
        List<Integer> objectIDList=simInfFromSqlite.getObjectIDList();
        List<Long> execDateTimeList=simInfFromSqlite.getExecDateTimeList();

        // Check the MITURN table
        // Parameters:
        // did: object ID
        // oid (integer): Turn ID
        // ent (integer): Time interval from 1 to N. 0 is the aggregation of all the intervals
        // flow (double): mean flow (veh/h)
        // speed (double): mean speed (km/h): need to change it to mph
        List<TurningStatisticsByObjectID> turningStatisticsByObjectIDList=new ArrayList<TurningStatisticsByObjectID>();
        double OutputInterval=5*60; // Aimsun output interval is 5 minutes
        for(int i=0;i<objectIDList.size();i++){
            int ObjectID=objectIDList.get(i);
            double FromTime=fromTimeList.get(i);
            long ExecDateTime=execDateTimeList.get(i);
            if(InputTime>FromTime) {
                int Interval=(int) (Math.ceil(InputTime-FromTime)/OutputInterval);
                if(Interval>=1) {
                    try {
                        Statement ps = con.createStatement();
                        ResultSet resultset = ps.executeQuery("select oid,flow,speed from MITURN where did="+ObjectID+
                                " and ent="+Interval+";");
                        List<TurningStatistics> turningStatisticsList=new ArrayList<TurningStatistics>();
                        while (resultset.next()) {
                            int TurnID=resultset.getInt("oid");
                            double TurnFlow=resultset.getDouble("flow");
                            double TurnSpeed=resultset.getDouble("speed")*0.621371; // km/hr to mph
                            turningStatisticsList.add(new TurningStatistics(TurnID,TurnFlow,TurnSpeed));
                        }
                        if(turningStatisticsList.size()!=0){
                            turningStatisticsByObjectIDList.add(new TurningStatisticsByObjectID(
                                    InputTime,ObjectID,FromTime,Interval,ExecDateTime,turningStatisticsList));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return turningStatisticsByObjectIDList;
    }

    //*********************************************************************
    // Get the microscopic lane information
    //*********************************************************************
    /**
     *
     * @param con Database connection
     * @param InputTime Input time (double, seconds)
     * @return List<LaneStatisticsByObjectID> class
     */
    public static List<LaneStatisticsByObjectID> GetLaneStatistics(Connection con, double InputTime){
        // This function is used to get lane statistics
        // If the InputTime is not of integer number of 300 seconds (5 minutes), get the floor of it (the statistics in the
        // past 5 minutes)

        // Check the SIM_INFO table
        SimInfFromSqlite simInfFromSqlite=GetSimInfFromSqlite(con);
        List<Double> fromTimeList=simInfFromSqlite.getFromTimeList();
        List<Integer> objectIDList=simInfFromSqlite.getObjectIDList();
        List<Long> execDateTimeList=simInfFromSqlite.getExecDateTimeList();

        // Check the MILANE table
        // did: replication ID
        // oid: section id
        // sid: vehicle type: 0 for all vehicles, other values for the type of vehicles in Aimsun
        // ent: time interval
        // lane: lane ID (from 1 to number of lanes) probably labeled from left to right
        // input_flow: mean flow (veh/hr) in the lane (can be -1 when no values)
        // input_flow_D: standard derivation (veh/hr) (can be -1 when no values)
        // speed: mean speed (km/hr) (can be -1 when no values)
        // speed_D : standard derivation (km/hr) (can be -1 when no values)
        List<LaneStatisticsByObjectID> laneStatisticsByObjectIDList=new ArrayList<LaneStatisticsByObjectID>();
        double OutputInterval=5*60; // Aimsun output interval is 5 minutes
        for(int i=0;i<objectIDList.size();i++){
            int ObjectID=objectIDList.get(i);
            double FromTime=fromTimeList.get(i);
            long  ExecDateTime=execDateTimeList.get(i);
            if(InputTime>FromTime) {
                int Interval=(int) (Math.ceil(InputTime-FromTime)/OutputInterval);
                if(Interval>=1) {
                    try {
                        Statement ps = con.createStatement();
                        ResultSet resultset = ps.executeQuery("select oid,lane,sid,input_flow,input_flow_D,speed,speed_D" +
                                " from MILANE where did="+ObjectID+" and ent="+Interval+" and sid=0;"); // Currently only check "All Vehicles"
                        List<LaneStatistics> laneStatisticsList=new ArrayList<LaneStatistics>();
                        while (resultset.next()) {
                            int SectionID=resultset.getInt("oid");
                            int LaneID=resultset.getInt("lane");
                            int VehicleType=resultset.getInt("sid");
                            double LaneFlow=resultset.getDouble("input_flow");
                            double LaneFlowStd=resultset.getDouble("input_flow_D");
                            double LaneSpeed=resultset.getDouble("speed");
                            if(LaneSpeed>0){
                                LaneSpeed=LaneSpeed*0.621371; // km/hr to mph
                            }
                            double LaneSpeedStd=resultset.getDouble("speed_D");
                            if(LaneSpeedStd>0){
                                LaneSpeedStd=LaneSpeedStd*0.621371; // km/hr to mph
                            }
                            laneStatisticsList.add(new LaneStatistics(SectionID,LaneID,VehicleType,LaneFlow,LaneFlowStd,LaneSpeed,LaneSpeedStd));
                        }
                        if(laneStatisticsList.size()!=0){
                            laneStatisticsByObjectIDList.add(new LaneStatisticsByObjectID(InputTime,ObjectID,FromTime,Interval,ExecDateTime,laneStatisticsList));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return laneStatisticsByObjectIDList;
    }

    //*********************************************************************
    // Get the microscopic section information
    //*********************************************************************
    /**
     *
     * @param con Database connection
     * @param InputTime Input time (double, seconds)
     * @return List<SectionStatisticsByObjectID> class
     */
    public static List<SectionStatisticsByObjectID> GetSectionStatistics(Connection con, double InputTime){
        // This function is used to get section statistics
        // If the InputTime is not of integer number of 300 seconds (5 minutes), get the floor of it (the statistics in the
        // past 5 minutes)

        // Check the SIM_INFO table
        SimInfFromSqlite simInfFromSqlite=GetSimInfFromSqlite(con);
        List<Double> fromTimeList=simInfFromSqlite.getFromTimeList();
        List<Integer> objectIDList=simInfFromSqlite.getObjectIDList();
        List<Long> execDateTimeList=simInfFromSqlite.getExecDateTimeList();

        // Check the MISECT table
        // did: replication ID
        // oid: section id
        // sid: vehicle type: 0 for all vehicles, other values for the type of vehicles in Aimsun
        // ent: time interval
        // input_flow: mean flow (veh/hr) in the lane (can be -1 when no values)
        // input_flow_D: standard derivation (veh/hr) (can be -1 when no values)
        // speed: mean speed (km/hr) (can be -1 when no values)
        // speed_D : standard derivation (km/hr) (can be -1 when no values)
        List<SectionStatisticsByObjectID> sectionStatisticsByObjectIDArrayList=new ArrayList<SectionStatisticsByObjectID>();
        double OutputInterval=5*60; // Aimsun output interval is 5 minutes
        for(int i=0;i<objectIDList.size();i++){
            int ObjectID=objectIDList.get(i);
            double FromTime=fromTimeList.get(i);
            long ExecDateTime=execDateTimeList.get(i);
            if(InputTime>FromTime) {
                int Interval=(int) (Math.ceil(InputTime-FromTime)/OutputInterval);
                if(Interval>=1) {
                    try {
                        Statement ps = con.createStatement();
                        ResultSet resultset = ps.executeQuery("select oid,sid,input_flow,input_flow_D,speed,speed_D" +
                                " from MISECT where did="+ObjectID+" and ent="+Interval+" and sid=0;"); // Currently only check "All Vehicles"
                        List<SectionStatistics> sectionStatisticsList=new ArrayList<SectionStatistics>();
                        while (resultset.next()) {
                            int SectionID=resultset.getInt("oid");
                            int VehicleType=resultset.getInt("sid");
                            double SectionFlow=resultset.getDouble("input_flow");
                            double SectionFlowStd=resultset.getDouble("input_flow_D");
                            double SectionSpeed=resultset.getDouble("speed");
                            if(SectionSpeed>0){
                                SectionSpeed=SectionSpeed*0.621371; // km/hr to mph
                            }
                            double SectionSpeedStd=resultset.getDouble("speed_D");
                            if(SectionSpeedStd>0){
                                SectionSpeedStd=SectionSpeedStd*0.621371; // km/hr to mph
                            }
                            sectionStatisticsList.add(new SectionStatistics(SectionID,VehicleType,SectionFlow,SectionFlowStd,
                                    SectionSpeed,SectionSpeedStd));
                        }
                        if(sectionStatisticsList.size()!=0){
                            sectionStatisticsByObjectIDArrayList.add(new SectionStatisticsByObjectID(InputTime,ObjectID,
                                    FromTime,Interval,ExecDateTime,sectionStatisticsList));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sectionStatisticsByObjectIDArrayList;
    }

    //*********************************************************************
    // Get the microscopic control turning information
    //*********************************************************************
    /**
     *
     * @param con SQL connection
     * @param InputTime Input time (double, seconds)
     * @return List<ControlTurnStatisticsByObjectID> class
     */
    public static List<ControlTurnStatisticsByObjectID> GetControlTurnStatistics(Connection con, double InputTime){
        // This function is used to get control turn statistics
        // If the InputTime is not of integer number of 300 seconds (5 minutes), get the floor of it (the statistics in the
        // past 5 minutes)

        // Check the SIM_INFO table
        SimInfFromSqlite simInfFromSqlite=GetSimInfFromSqlite(con);
        List<Double> fromTimeList=simInfFromSqlite.getFromTimeList();
        List<Integer> objectIDList=simInfFromSqlite.getObjectIDList();
        List<Long> execDateTimeList=simInfFromSqlite.getExecDateTimeList();

        // Check the MICONTROLTURN table
        // did: replication ID
        // oid: turn id
        // sid: vehicle type: 0 for all vehicles, other values for the type of vehicles in Aimsun
        // ent: time interval
        // state: state index
        //  0 (red), 1 (green), 2 (yellow), 3 (flashing green), 4 (flashing red), 5 (flashing yellow), 6 (off),
        // 7(flashing yellow behaving as green), 8 (yellow behaving as green) and 9 (flashing red behaving as green).
        // active_time: active time in seconds
        // active_time_percentage: percentage of active time
        List<ControlTurnStatisticsByObjectID> controlTurnStatisticsByObjectIDArrayList=new ArrayList<ControlTurnStatisticsByObjectID>();
        double OutputInterval=5*60; // Aimsun output interval is 5 minutes
        for(int i=0;i<objectIDList.size();i++){
            int ObjectID=objectIDList.get(i);
            double FromTime=fromTimeList.get(i);
            long ExecDateTime=execDateTimeList.get(i);
            if(InputTime>FromTime) {
                int Interval=(int) (Math.ceil(InputTime-FromTime)/OutputInterval);
                if(Interval>=1) {
                    try {
                        Statement ps = con.createStatement();
                        ResultSet resultset = ps.executeQuery("select oid,sid,state,active_time,active_time_percentage" +
                                " from MICONTROLTURN where did="+ObjectID+" and ent="+Interval+" and sid=0;"); // Currently only check "All Vehicles"
                        List<ControlTurnStatistics> controlTurnStatisticsList=new ArrayList<ControlTurnStatistics>();
                        while (resultset.next()) {
                            int TurnID=resultset.getInt("oid");
                            int VehicleType=resultset.getInt("sid");
                            double State=resultset.getDouble("state");
                            double ActiveTime=resultset.getDouble("active_time");
                            double ActiveTimePercentage=resultset.getDouble("active_time_percentage");
                            controlTurnStatisticsList.add(new ControlTurnStatistics(TurnID,VehicleType,State,ActiveTime,ActiveTimePercentage));
                        }
                        if(controlTurnStatisticsList.size()!=0){
                            controlTurnStatisticsByObjectIDArrayList.add(new ControlTurnStatisticsByObjectID(
                                    InputTime,ObjectID,FromTime,Interval,ExecDateTime,controlTurnStatisticsList));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return controlTurnStatisticsByObjectIDArrayList;
    }

    //*********************************************************************
    // Get the microscopic control signal information
    //*********************************************************************
    /**
     *
     * @param con
     * @param InputTime
     * @return List<ControlSignalStatisticsByObjectID> class
     */
    public static List<ControlSignalStatisticsByObjectID> GetControlSignalStatistics(Connection con, double InputTime){
        // This function is used to get control signal statistics
        // If the InputTime is not of integer number of 300 seconds (5 minutes), get the floor of it (the statistics in the
        // past 5 minutes)

        // Check the SIM_INFO table
        SimInfFromSqlite simInfFromSqlite=GetSimInfFromSqlite(con);
        List<Double> fromTimeList=simInfFromSqlite.getFromTimeList();
        List<Integer> objectIDList=simInfFromSqlite.getObjectIDList();
        List<Long> execDateTimeList=simInfFromSqlite.getExecDateTimeList();

        // Check the MICONTROLSIGNAL table
        // did: replication ID
        // oid: node id
        // sg: signal group id
        // sid: vehicle type: 0 for all vehicles, other values for the type of vehicles in Aimsun
        // ent: time interval
        // state: state index
        //  0 (red), 1 (green), 2 (yellow), 3 (flashing green), 4 (flashing red), 5 (flashing yellow), 6 (off),
        // 7(flashing yellow behaving as green), 8 (yellow behaving as green) and 9 (flashing red behaving as green).
        // active_time: active time in seconds
        // active_time_percentage: percentage of active time
        List<ControlSignalStatisticsByObjectID> controlSignalStatisticsByObjectIDArrayList=new ArrayList<ControlSignalStatisticsByObjectID>();
        double OutputInterval=5*60; // Aimsun output interval is 5 minutes
        for(int i=0;i<objectIDList.size();i++){
            int ObjectID=objectIDList.get(i);
            double FromTime=fromTimeList.get(i);
            long ExecDateTime=execDateTimeList.get(i);
            if(InputTime>FromTime) {
                int Interval=(int) (Math.ceil(InputTime-FromTime)/OutputInterval);
                if(Interval>=1) {
                    try {
                        Statement ps = con.createStatement();
                        ResultSet resultset = ps.executeQuery("select oid,sg,sid,state,active_time,active_time_percentage" +
                                " from MICONTROLSIGNAL where did="+ObjectID+" and ent="+Interval+" and sid=0;"); // Currently only check "All Vehicles"
                        List<ControlSignalStatistics> controlSignalStatisticsList=new ArrayList<ControlSignalStatistics>();
                        while (resultset.next()) {
                            int NodeID=resultset.getInt("oid");
                            int SignalGroupID=resultset.getInt("sg");
                            int VehicleType=resultset.getInt("sid");
                            double State=resultset.getDouble("state");
                            double ActiveTime=resultset.getDouble("active_time");
                            double ActiveTimePercentage=resultset.getDouble("active_time_percentage");
                            controlSignalStatisticsList.add(new ControlSignalStatistics(NodeID,SignalGroupID,VehicleType,
                                    State,ActiveTime,ActiveTimePercentage));
                        }
                        if(controlSignalStatisticsList.size()!=0){
                            controlSignalStatisticsByObjectIDArrayList.add(new ControlSignalStatisticsByObjectID(
                                    InputTime,ObjectID,FromTime,Interval,ExecDateTime,controlSignalStatisticsList));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return controlSignalStatisticsByObjectIDArrayList;
    }

    //*********************************************************************
    // Get the microscopic control phase information
    //*********************************************************************
    /**
     *
     * @param con Database connection
     * @param InputTime Input time (double, seconds)
     * @return List<ControlPhaseStatisticsByObjectID> class
     */
    public static List<ControlPhaseStatisticsByObjectID> GetControlPhaseStatistics(Connection con, double InputTime){
        // This function is used to get control phase statistics
        // If the InputTime is not of integer number of 300 seconds (5 minutes), get the floor of it (the statistics in the
        // past 5 minutes)

        // Check the SIM_INFO table
        SimInfFromSqlite simInfFromSqlite=GetSimInfFromSqlite(con);
        List<Double> fromTimeList=simInfFromSqlite.getFromTimeList();
        List<Integer> objectIDList=simInfFromSqlite.getObjectIDList();
        List<Long> execDateTimeList=simInfFromSqlite.getExecDateTimeList();

        // Check the MICONTROLPHASE table
        // did: replication ID
        // oid: control plan ID
        // node_id: node ID
        // phase: phase ID
        // ent: time interval
        // active_time: active time in seconds
        // active_time_percentage: percentage of active time
        List<ControlPhaseStatisticsByObjectID> controlPhaseStatisticsByObjectIDArrayList=new ArrayList<ControlPhaseStatisticsByObjectID>();
        double OutputInterval=5*60; // Aimsun output interval is 5 minutes
        for(int i=0;i<objectIDList.size();i++){
            int ObjectID=objectIDList.get(i);
            double FromTime=fromTimeList.get(i);
            long ExecDateTime=execDateTimeList.get(i);
            if(InputTime>FromTime) {
                int Interval=(int) (Math.ceil(InputTime-FromTime)/OutputInterval);
                if(Interval>=1) {
                    try {
                        Statement ps = con.createStatement();
                        ResultSet resultset = ps.executeQuery("select oid,node_id,phase,active_time,active_time_percentage" +
                                " from CONTROLPHASE where did="+ObjectID+" and ent="+Interval+";");
                        List<ControlPhaseStatistics> controlPhaseStatisticsList=new ArrayList<ControlPhaseStatistics>();
                        while (resultset.next()) {
                            int ControlPlanID=resultset.getInt("oid");
                            int NodeID=resultset.getInt("node_id");
                            int PhaseID=resultset.getInt("phase");
                            double ActiveTime=resultset.getDouble("active_time");
                            double ActiveTimePercentage=resultset.getDouble("active_time_percentage");
                            controlPhaseStatisticsList.add(new ControlPhaseStatistics(ControlPlanID,NodeID,PhaseID,ActiveTime,ActiveTimePercentage));
                        }
                        if(controlPhaseStatisticsList.size()!=0){
                            controlPhaseStatisticsByObjectIDArrayList.add(new ControlPhaseStatisticsByObjectID(
                                    InputTime,ObjectID,FromTime,Interval,ExecDateTime,controlPhaseStatisticsList));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return controlPhaseStatisticsByObjectIDArrayList;
    }

    //*********************************************************************
    // Get the microscopic centroid information
    //*********************************************************************
    /**
     *
     * @param con Database connection
     * @param InputTime Input time (double, seconds)
     * @return List<CentroidStatistics> class
     */
    public static List<CentroidStatistics> GetCentroidStatistics(Connection con, double InputTime){
        // This function is used to get the centroid statistics with given input time

        List<CentroidStatistics> centroidStatisticsList=new ArrayList<CentroidStatistics>();
        double Interval=5*60; // 5 minutes
        double DistToStopbarThreshold=150; // Normally the distance to advance detectors
        double FromTime=Math.max(0,InputTime-Interval);
        double ToTime=InputTime;

        try {
            // Get the simulated vehicle OD information
            Statement ps = con.createStatement();
            String TableName="simulated_vehicle_"+ MainFunction.cBlock.replicationName;
            ResultSet resultset = ps.executeQuery("select SectionID,LaneID,VehicleType,CentroidOrigin,CentroidDestination," +
                    "DistanceToEnd from "+TableName+" where Time>="+FromTime+" and Time<"+ToTime+";");
            List<SimVehODInf> simVehODInfList=new ArrayList<SimVehODInf>();
            while (resultset.next()) {
                int SectionID=resultset.getInt("SectionID");
                int LaneID=resultset.getInt("LaneID");
                int VehicleType=resultset.getInt("VehicleType");
                int CentroidOrigin=resultset.getInt("CentroidOrigin");
                int CentroidDestination=resultset.getInt("CentroidDestination");
                double DistanceToEnd=resultset.getDouble("DistanceToEnd");
                simVehODInfList.add(new SimVehODInf(SectionID,LaneID,VehicleType,CentroidOrigin,CentroidDestination,DistanceToEnd));
            }
            if(simVehODInfList.size()!=0){
                centroidStatisticsList=CalculateCentroidStatisticsFromVehicleODInf(simVehODInfList,InputTime,Interval,DistToStopbarThreshold);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return centroidStatisticsList;
    }

    /**
     *
     * @param simVehODInfList List<SimVehODInf> class
     * @param InputTime Input time (double, seconds)
     * @param Interval (double, seconds)
     * @param DistToStopbarThreshold Threshold of distance to stopbar (double)
     * @return List<CentroidStatistics> class
     */
    public static List<CentroidStatistics> CalculateCentroidStatisticsFromVehicleODInf(List<SimVehODInf> simVehODInfList
            , double InputTime,double Interval,double DistToStopbarThreshold){
        // This function is used to calculate the centroid statistics from the vehicle od information

        List<CentroidStatistics> centroidStatisticsList=new ArrayList<CentroidStatistics>();

        // Get the unique set of section IDs
        HashSet<Integer> UniqueSections=new HashSet<Integer>();
        for(int i=0;i<simVehODInfList.size();i++){
            UniqueSections.add(simVehODInfList.get(i).getSectionID());
        }
        // Index for searching
        int[] SearchIndex= new int[simVehODInfList.size()];
        for(int i=0;i<SearchIndex.length;i++){
            SearchIndex[i]=0;
        }
        // Looping
        Iterator<Integer> iteratorSection=UniqueSections.iterator();
        while(iteratorSection.hasNext()){// Loop for each section
            int SectionID=iteratorSection.next(); // Get the section ID

            // Initialization
            List<int[]> ODList=new ArrayList<int[]>(); // By section
            List<int[]> DownstreamODList=new ArrayList<int[]>(); // By section
            List<ODByLane> ODListByLane=new ArrayList<ODByLane>(); // By section-lane
            List<ODByLane> DownstreamODListByLane=new ArrayList<ODByLane>(); // By section-lane

            for(int i=0;i<simVehODInfList.size();i++){ // Loop for each simulation vehicle
                if(SearchIndex[i]==0 && simVehODInfList.get(i).getSectionID()==SectionID){ // Find the section ID not visited
                    SearchIndex[i]=1; // Set visited
                    ODList.add(new int []{simVehODInfList.get(i).getCentroidOrigin(),simVehODInfList.get(i).getCentroidDestination()
                            ,simVehODInfList.get(i).getVehicleType()});
                    if(simVehODInfList.get(i).getDistanceToEnd()<DistToStopbarThreshold){
                        DownstreamODList.add(new int []{simVehODInfList.get(i).getCentroidOrigin(),simVehODInfList.get(i).getCentroidDestination(),
                                simVehODInfList.get(i).getVehicleType()});
                    }
                    // sorted by lane
                    int LaneID=simVehODInfList.get(i).getLaneID();
                    boolean LaneFound=false;
                    for(int j=0;j<ODListByLane.size();j++){
                        if(ODListByLane.get(j).getLaneID()==LaneID){
                            ODListByLane.get(j).getODList().add(new int []{simVehODInfList.get(i).getCentroidOrigin(),simVehODInfList.get(i).getCentroidDestination(),
                                    simVehODInfList.get(i).getVehicleType()});
                            LaneFound=true;
                            break;
                        }
                    }
                    if(!LaneFound){ // A new lane? append it to the end
                        List<int[]> tmpODList=new ArrayList<int[]>();
                        tmpODList.add(new int []{simVehODInfList.get(i).getCentroidOrigin(),simVehODInfList.get(i).getCentroidDestination(),
                                simVehODInfList.get(i).getVehicleType()});
                        ODListByLane.add(new ODByLane(LaneID,tmpODList));
                    }

                    // Sort by lane and downstream distance threshold
                    if(simVehODInfList.get(i).getDistanceToEnd()<DistToStopbarThreshold){
                        boolean LaneDownstreamFound=false;
                        for(int j=0;j<DownstreamODListByLane.size();j++){
                            if(DownstreamODListByLane.get(j).getLaneID()==LaneID){
                                DownstreamODListByLane.get(j).getODList().add
                                        (new int []{simVehODInfList.get(i).getCentroidOrigin(),simVehODInfList.get(i).getCentroidDestination(),
                                                simVehODInfList.get(i).getVehicleType()});
                                LaneDownstreamFound=true;
                                break;
                            }
                        }
                        if(!LaneDownstreamFound){ // A new lane? append it to the end
                            List<int[]> tmpODList=new ArrayList<int[]>();
                            tmpODList.add(new int []{simVehODInfList.get(i).getCentroidOrigin(),simVehODInfList.get(i).getCentroidDestination(),
                                    simVehODInfList.get(i).getVehicleType()});
                            DownstreamODListByLane.add(new ODByLane(LaneID,tmpODList));
                        }
                    }
                }
            }
            // Finish searching one section, append the result to the end
            centroidStatisticsList.add(new CentroidStatistics(SectionID,InputTime,Interval,DistToStopbarThreshold,ODList,DownstreamODList
                    ,ODListByLane,DownstreamODListByLane));
        }

        return centroidStatisticsList;
    }

    //*********************************************************************
    // Get the microscopic vehicle trajectory data
    //*********************************************************************
    /**
     *
     * @param con Database connection
     * @param InputTime Input time (double, seconds)
     * @return List<SimVehInfBySection> class
     */
    public static List<SimVehInfBySection> GetVehicleTrajectories(Connection con, double InputTime){
        // This function is used to get vehicle trajectories at given InputTime

        List<SimVehInfBySection> simVehInfBySectionList=new ArrayList<SimVehInfBySection>();
        try {
            // Get the simulated vehicle OD information
            Statement ps = con.createStatement();
            String TableName="simulated_vehicle_"+ MainFunction.cBlock.replicationName;

            // Get the closest time interval in the database
            double Interval=2.5*60;
            double FromTime=InputTime-Interval;
            double ToTime=InputTime+Interval;
            String sql="select Time from "+TableName+" where Time>="+FromTime+" and Time<"+ToTime+";";
            ResultSet resultsetTime=ps.executeQuery(sql);
            double MinGap=Interval;
            double SelectTime=InputTime;
            while (resultsetTime.next()) {
                double tmpTime=resultsetTime.getDouble("Time");
                if(Math.abs(tmpTime-InputTime)<MinGap){
                    MinGap=Math.abs(tmpTime-InputTime);
                    SelectTime=tmpTime;
                }
            }

            // Get the corresponding snapshot of the traffic state
            List<AimsunVehInf> aimsunVehInfList=new ArrayList<AimsunVehInf>();
            sql="select * from "+TableName+" where Time="+SelectTime+";";
            ResultSet resultset = ps.executeQuery(sql);
            while (resultset.next()) {
                AimsunVehInf aimsunVehInf=new AimsunVehInf(resultset.getDouble("Time"),resultset.getInt("VehicleID"),
                        resultset.getInt("VehicleType"),resultset.getInt("SectionID"),
                        resultset.getInt("LaneID"),resultset.getDouble("CurrentPosition"),
                        resultset.getDouble("CurrentSpeed"),resultset.getInt("CentroidOrigin"),
                        resultset.getInt("CentroidDestination"),resultset.getDouble("DistanceToEnd"));
                aimsunVehInfList.add(aimsunVehInf);
            }

            // Organize the data by section
            // Get the unique set of section IDs
            HashSet<Integer> UniqueSections=new HashSet<Integer>();
            for(int i=0;i<aimsunVehInfList.size();i++){
                UniqueSections.add(aimsunVehInfList.get(i).getSectionID());
            }
            // Index for searching
            int[] SearchIndex= new int[aimsunVehInfList.size()];
            for(int i=0;i<SearchIndex.length;i++){
                SearchIndex[i]=0;
            }

            // Looping
            Iterator<Integer> iteratorSection=UniqueSections.iterator();
            while(iteratorSection.hasNext()) {// Loop for each section
                int SectionID=iteratorSection.next(); // Get the section ID
                // Initialization
                List<AimsunVehInf> tmpAimsunVehInfList=new ArrayList<AimsunVehInf>();
                for(int i=0;i<aimsunVehInfList.size();i++){// Loop for each vehicle trajectory point
                    if(SearchIndex[i]==0 && aimsunVehInfList.get(i).getSectionID()==SectionID){// Find the section ID
                        SearchIndex[i]=1;
                        tmpAimsunVehInfList.add(aimsunVehInfList.get(i));
                    }
                }
                SimVehInfBySection simVehInfBySection=new SimVehInfBySection(SectionID,InputTime,SelectTime,tmpAimsunVehInfList);
                simVehInfBySectionList.add(simVehInfBySection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return simVehInfBySectionList;
    }

}



