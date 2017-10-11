import java.util.Scanner;
import java.io.*;

public class OSLab05 {
 public static void main(String []args) {
   BufferedWriter out = null;
  try{ out = new BufferedWriter(new FileWriter(new File("output.txt"))); }
  catch(IOException e) {}
   
  Simulator sim;
  System.out.println("Hi! Welcome to CPU Scheduling Simulator. Please give me required parameters.\n Scheduling algorithm - (1) FCFS (2) RR (3) SJF (4) SRTN (5) All\n");

  Scanner sc = new Scanner(System.in);
  int alg=0,numProc=5;
  
  // Obtains the algorithm from the user
  while(alg==0) {
   String in = sc.nextLine();
   if(isValid(in)>0) {alg=isValid(in);}
  }

  System.out.println("Number of Processes in the system:");
  
  String in = "null";
  
  // obtains the number of processes for the simulation
  while(!isInt(in)) { in = sc.nextLine(); }
  
  numProc = Integer.parseInt(in);
  System.out.println("\nWait.... Generating Schedules...\n");
  // run the simulation
  sim = new Simulator(alg,numProc,out);
  System.out.println("DONE. Please check the output file output.txt for all the results.");
  // flush and close buffer
  try{out.flush(); out.close();} catch(IOException e) {}
 }

 private static int isValid(String s) {
  try{ Integer.parseInt(s); } catch(Exception e) {return 0;}
  int v = Integer.parseInt(s);
  if(v > 0 && v <6) {return v;}
  return 0;
 }

 private static boolean isInt(String s) {
  try{ Integer.parseInt(s); } catch(Exception e) {return false;}
  return true;
 }
}