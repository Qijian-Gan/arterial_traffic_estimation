package config;

import java.util.Scanner;

/**
 * Created by Qijian-Gan on 10/6/2017.
 */
public class getTaskID {
    public static int getTaskIDFromScreen(){
        int taskID=0;
        // Selection of type of tasks
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please choose one of the following tasks (Be sure to update the configuration file FIRST!):\n");
        System.out.print("1:  Extract the Aimsun network\n"); // Extract the network files
        System.out.print("2:  Read the Aimsun network files\n"); // Read the network files
        System.out.print("3:  Read and reconstruct the Aimsun network files\n"); // Read and reconstruct the network files
        System.out.print("4:  Load Aimsun simulation data to database\n"); // Load Aimsun simulation results
        System.out.print("5:  Arterial traffic estimation\n"); // Arterial traffic estimation
        System.out.print("6:  Arterial traffic initialization\n"); // Arterial traffic initialization
        System.out.print("Please enter your selection (number):");
        taskID =Integer.parseInt(scanner.next());
        return taskID;
    }
}
