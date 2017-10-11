import java.util.*;
import java.util.Random;
import java.io.*;

public class Simulator {
 private int numProc;
 private int algorithm;
 private Random rng;
 
 private ArrayList<Process> proc;
 private BufferedWriter out;


 public Simulator(int alg, int num, BufferedWriter outt) {
  System.out.println("Sim Constructor: Algorithm: "+alg+" - Processes: "+num);
  // Initiate required variables variables
  this.algorithm = alg; this.numProc = num; this.out = outt;
  
  rng = new Random(); // Random number generator used to instantiate arrivaleTime, burstTime and deadlines of a process
  
  proc = new ArrayList<Process>(numProc); // array holding all generated processes
  for(int i=0; i< numProc; i++) proc.add(new Process(i,rng,10,25,1)); // Create an array of process to run simulations on
  
  displayProcessInfo(); // create data.txt with info of all process used
  
  int rrQuantum = 4; // quantum time slices used to run the round robin algorithm, default set to 4 unit time
  runAlg(alg,rrQuantum);
 }
 
 /*
  * Prints all arrival time, burst time, and deadlines of all processes to data.txt
  */
 private void displayProcessInfo() {
  try{ BufferedWriter data = new BufferedWriter(new FileWriter(new File("data.txt"))); 
    for(int i = 0;i<numProc;i++) {
      data.write("Id: "+proc.get(i).getId()+" - Arrival Time: "+proc.get(i).getArrivalTime()+" - Burst Time: "+proc.get(i).getBurstTime()+" - Deadline: "+proc.get(i).getDeadline());data.newLine();
    }
    data.flush(); data.close();
  } catch(IOException e) {}
 }

 /*
  *  Function used to decide which algorithm to simulate
  */
 private void runAlg(int alg, int rrQuantum) {
  switch(alg) {
   case 1: 
     fcfs();
     break;
   case 2: 
     rr(rrQuantum);
     break;
   case 3: 
     sjf();
     break;
   case 4: 
     srtn();
     break;
   case 5: 
     runAll(rrQuantum);
     break;
   default: 
     System.out.println("Simulator - Switch Case Error");
     break; // error
  }
 }

 // simulate all scheduling algorithms
 private void runAll(int rrQuantum) {
   rr(rrQuantum);
   try{out.newLine();out.newLine();} catch(IOException e){}
   fcfs();
   try{out.newLine();out.newLine();} catch(IOException e){}
   sjf();
   try{out.newLine();out.newLine();} catch(IOException e){}
   srtn();
 }

 // Round Robin
 public void rr(int q) {
   try{out.write("Round Robin Algorithm Begins");out.newLine();} catch(IOException e){}
   // Generate time quantum, time counter, and arrived counter
   int currentTime = 0, arrivedIndex = -1; //quantum = rng.nextInt(8)+1; // Range from 1 - 8
   int quantum = q;
   
   sortArrivalTime();
   arrivedIndex = checkArrivals(currentTime, arrivedIndex);
   // each process will run their time slice is up, then if a new proccess has arrived then we will re evaluate
   while(continueRun()) {
     int executeTime = 0;
     boolean run = false;
     for(int i=0; i<arrivedIndex;i++) {
       //System.out.println("CurTime: "+currentTime+"Process: "+i+" TimeLeft: "+proc.get(i).getTimeLeft());
       int exTime = executeQuantum(quantum,i,currentTime);
       currentTime += exTime;
       run = exTime>0;
     }
     if (!run) {currentTime++;}
     arrivedIndex = checkArrivals(currentTime,arrivedIndex);
   }
   
   // calculates wait time and turn around time for the round robin algorithm
   for(int i = 0; i < numProc; i++) {
     proc.get(i).setWaitTime(proc.get(i).getCompletionTime()-proc.get(i).getBurstTime());
     proc.get(i).setTurnAroundTime(proc.get(i).getCompletionTime()-proc.get(i).getArrivalTime());
   }
   
   try{out.write("--- Round Robin Algorithm ENDS ---");out.newLine();} catch(IOException e){}
   printStats(); // print the cpu utilization of this algorithm
 }
 
 // helper method used in conjuction with round robin
 private boolean continueRun() {
   boolean flag = false;
   for(int i=0; i < numProc; i++) {if(proc.get(i).getTimeLeft()>0) flag = true;}
   return flag;
 }
 
 // Checks if a new process has arrived
 private int checkArrivals(int curTime, int in) {
   int index = in<0 ? 0 : in;
   for(int i=index; i<numProc;i++) {
     if(curTime >= proc.get(i).getArrivalTime()) index++;
   }
   return index;
 }

 // Execute the process for a given time slice, returning the amount of time which it executed for
 private int executeQuantum(int quantum, int procId, int time) {
   int executeTime = 0, tl = proc.get(procId).getTimeLeft();
   if(proc.get(procId).getArrivalTime() == proc.get(procId).getTimeLeft()) { proc.get(procId).setResponseTime(time); }
   if(tl == 0) return 0;
   
   if(quantum >= tl) {executeTime = tl;proc.get(procId).setTimeLeft(0);}
   else {executeTime = quantum;proc.get(procId).setTimeLeft(tl - quantum);}
   
   try{out.write("Process "+proc.get(procId).getId()+": Executed for "+executeTime); out.newLine();} catch(IOException e) {}
   
   // print execution
   return executeTime;
 }
 
 // First come first served
 public void fcfs() {
  try{out.write("FCFS Algorithm Begins");out.newLine();} catch(IOException e){}
  sortArrivalTime(); //Sort on arrival time, smallest to greatest
  
  // Exectue burst Times in order of arrival time
  nonPremptiveExecute();
  try{out.write("--- FCFS END ---");out.newLine();out.newLine();} catch(IOException e){}
  printStats();
 }
 
 /*
  * Helper function used in FCFS algorithm
  * To sort all processes on their arrival time
  */
 private void sortArrivalTime() {
    Collections.sort(proc, new Comparator<Process>(){
      public int compare(Process p1, Process p2) {
        return ((Integer)p1.getArrivalTime()).compareTo((Integer)p2.getArrivalTime());
      }});
 } // end sort

 /*
  * Shortest Job First
  * Ignoring arrival time
  */
 public void sjf() {
   try{out.write("SJF Algorithm Begins");out.newLine();} catch(IOException e){}
   sortBurstLength();
   
  // execute process until done
   nonPremptiveExecute();
   try{out.write("--- SJF END ---");out.newLine();out.newLine();} catch(IOException e){}
   printStats();
 }
 
  /*
  * Helper function used in SJF algorithm
  * To sort all processes on their length
  */
 private void sortBurstLength() {
   Collections.sort(proc, new Comparator<Process>(){
      public int compare(Process p1, Process p2) {
        return ((Integer)p1.getBurstTime()).compareTo((Integer)p2.getBurstTime());
      }});
 } // end sort

 /*
  * Shortest Remaining Time Next
  */
 public void srtn() {
   // 
   try{out.write("SRTN Algorithm Begins");out.newLine();} catch(IOException e){}
   
   try{out.write("--- SRTN Algorithm ENDS ---");out.newLine();} catch(IOException e){}
   
   // if(proc.get(procId).getArrivalTime() == proc.get(procId).getTimeLeft()) { proc.get(i).setResponseTime(time); }
   for(int i = 0; i < numProc; i++) {
     proc.get(i).setWaitTime(proc.get(i).getCompletionTime()-proc.get(i).getBurstTime());
     proc.get(i).setTurnAroundTime(proc.get(i).getCompletionTime()-proc.get(i).getArrivalTime());
   }
   
   // Print stats of running algorithm
   printStats();
 }
 
 
 /*
  * Helper function used in the FCFS and SJF algorithm becasue they aren't premptive
  */
 private void nonPremptiveExecute() {
   int time = 0, time2 = 0;
   for(int i = 0; i < numProc; i++) {
     time2 += proc.get(i).getBurstTime();
     
     // writes execution information to buffer
     try{out.write("Process "+proc.get(i).getId()+": Executes for "+proc.get(i).getBurstTime()+"unit time (From time "+time+" to "+time2+")");out.newLine();} catch(IOException e) {}

     // Calculates the wait time, response time, and turn around time for the non premptive algorithms
     proc.get(i).setWaitTime(time);
     time += proc.get(i).getBurstTime();
     proc.get(i).setResponseTime(time);
     proc.get(i).setTurnAroundTime(time-proc.get(i).getArrivalTime());
   }
 }
 
 // return average wait time of the running algorithm
 public double getAvgWait() {
   int time=0;
   for(int i = 0; i < numProc; i++) { time += proc.get(i).getWaitTime(); }
   return time/numProc;
 }
 
 // return average turn around time of the running algorithm
 public double getAvgTurnAround() {
   int time=0;
   for(int i = 0; i < numProc; i++) { time += proc.get(i).getTurnAroundTime(); }
   return time/numProc;
 }
 
 // return average response time of the running algorithm
 public double getAvgResponseTime() {
   int time=0;
   for(int i = 0; i < numProc; i++) { time += proc.get(i).getResponseTime(); }
   return time/numProc;
 }
 
 /*
  * Writes the CPU utilization of the runnning algorithm to buffer
  */
 public void printStats() {
   try{
     out.write("--- CPU Utilization ---");out.newLine();
     out.write("Waiting Time  - Response Time  - Turn Around Time");out.newLine();
     for(int i=0;i<numProc;i++) out.write("Id: "+proc.get(i).getId()+" - "+proc.get(i).getWaitTime()+" - "+proc.get(i).getResponseTime()+" - "+proc.get(i).getTurnAroundTime());out.newLine();
     out.write("Average Waiting Time: "+getAvgWait()+" - Average Response Time: "+getAvgResponseTime()+" - Average Turn Around Time"+getAvgTurnAround());out.newLine();
     out.write("--- END of CPU Utilization ---");out.newLine();
   } catch(IOException e) {}
 }
}