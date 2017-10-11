import java.util.Random;

public class Process {
 // Object variables
 private int arrivalTime, burstTime, deadline, length, id, waitTime, resTime, turnATime, cTime;
 private int timeLeft;
 // Constructor
 public Process(int id, Random rng, int arrivalRange, int burst, int end) {
  arrivalTime = rng.nextInt(arrivalRange);
  burstTime = timeLeft = rng.nextInt(burst)+1; // Assuming we cannot have a burst time of 0
  deadline = rng.nextInt(end);
  length = deadline - arrivalTime;
  this.id = id;
 }

 // Getter and Setter methods
 public int getArrivalTime() { return arrivalTime; }
 public int getBurstTime() { return burstTime; }
 public int getDeadline() { return deadline; }
 public int getId() { return id; }
 
 // Wait Time
 public int getWaitTime() { return waitTime; }
 public void setWaitTime(int w) { waitTime = w; }
 
 // Reponse Time
 public int getResponseTime() { return resTime; }
 public void setResponseTime(int r) { resTime = r; }
 
 // Turn Around Time
 public int getTurnAroundTime() { return turnATime; }
 public void setTurnAroundTime(int t) { turnATime = t; }
 
 // Completion Time
 public int getCompletionTime() { return cTime; }
 public void setCompletionTime(int t) { cTime = t; }

 // Time left to execute - used in premptive algorithms (ie RR + SRJN)
 public int getTimeLeft() { return timeLeft; }
 public void setTimeLeft(int t) { timeLeft = t; }
}