import java.util.Random;
import java.util.concurrent.Semaphore;

public class Lab3
{
	// Configuration
    final static int PORT0 = 0;
	final static int PORT1 = 1;
	final static int MAXLOAD = 5;

	public static void main(String args[]) 
	{
		final int NUM_CARS = 10;
		int i;

		Ferry fer = new Ferry(PORT0,10);

		Auto [] automobile = new Auto[NUM_CARS];
		for (i=0; i< 7; i++) automobile[i] = new Auto(i,PORT0,fer);
		for ( ; i<NUM_CARS ; i++) automobile[i] = new Auto(i,PORT1,fer);

		Ambulance ambulance = new Ambulance(PORT0,fer);

			/* Start the threads */
 		fer.start();   // Start the ferry thread.
		for (i=0; i<NUM_CARS; i++) automobile[i].start();  // Start automobile threads
		ambulance.start();  // Start the ambulance thread.

		try {fer.join();} catch(InterruptedException e) { }; // Wait until ferry terminates.
		System.out.println("Ferry stopped.");
		// Stop other threads.
		for (i=0; i<NUM_CARS; i++) automobile[i].interrupt(); // Let's stop the auto threads.
		ambulance.interrupt(); // Stop the ambulance thread.
	}
}


class Auto extends Thread 
{
	private int id_auto;
	private int port;
	private Ferry ferry;

	public Auto(int id_auto, int port, Ferry ferry) 
	{
		this.id_auto = id_auto;
		this.port = port;
		this.ferry = ferry;
	}

	public void run() 
	{
		Semaphore semBoard; // Semaphore to board

		while (true) 
		{
			// Delay
			try 
			{
				sleep((int) (300 * Math.random()));
			} 
			catch (Exception e)
			{
				break;
			}
			System.out.println("Auto " + id_auto + " arrives at port " + port);
			
			if (port == Lab3.PORT0)
				semBoard = ferry.semBoardPort0;
			else
				semBoard = ferry.semBoardPort1;

			// Board
			try {
				semBoard.acquire();
			} 
			catch (InterruptedException e)
			{
				break;
			}
			System.out.println("Auto " + id_auto + " boards on the ferry at port " + port);
			ferry.addLoad(); // increment the ferry load
			if (ferry.getLoad() == Lab3.MAXLOAD)
				ferry.semDepart.release(); // Advise the ferry to leave
			else
				semBoard.release(); // signal the next auto

			// Arrive at the next port
			port = 1 - port;
			if (port == Lab3.PORT0)
				semBoard = ferry.semBoardPort0;
			else
				semBoard = ferry.semBoardPort1;

			//Disembark
			try {
				ferry.semDisembark.acquire();
			} 
			catch (InterruptedException e) 
			{
				break;
			}
			System.out.println("Auto " + id_auto	+ " disembarks from ferry at port " + port);
			ferry.reduceLoad(); // Reduce load
			if (ferry.getLoad() == 0)
				semBoard.release(); // signal to cars to board
			else
				ferry.semDisembark.release(); // signal next auto to disembark
			
			if (isInterrupted())
				break;
		}
		System.out.println("Auto " + id_auto + " terminated");
	}
}

class Ambulance extends Thread
{
	
	private int port;
	private Ferry ferry;
	
	public Ambulance(int port, Ferry ferry)
	{
		this.port = port;
		this.ferry = ferry;
	}
	
	public void run()
	{
		Semaphore semBoard;
		while(true)
		{
			
			try
			{
				sleep((int) (1000*Math.random()));
			}
			catch(Exception e)
			{
				break;
			}
			System.out.println("Ambulance arrives at port "+ port);
			
			if(port == Lab3.PORT0)
				semBoard = ferry.semBoardPort0;
			else
				semBoard = ferry.semBoardPort1;
			
			
			try
			{
				semBoard.acquire();
			}
			catch(InterruptedException e)
			{
				break;
			}
			System.out.println("Ambulance boards the ferry at port "+port);
			ferry.addLoad();
			ferry.semDepart.release(); 
			
			//Arrive at the next port
			port = 1 - port;
			if(port == Lab3.PORT0)
				semBoard = ferry.semBoardPort0;
			else
				semBoard = ferry.semBoardPort1;
			
			//Disembark
			try
			{
				ferry.semDisembark.acquire();
			} 
			catch (InterruptedException e) 
			{
				break; 
			}
			System.out.println("Ambulance disembarks te ferry at port " + port);
			ferry.reduceLoad();
			
			if(ferry.getLoad() == 0) 
				semBoard.release();  
			else 
				ferry.semDisembark.release();  
			
			
			if(isInterrupted())
				break;
		}
		System.out.println("Ambulance terminates");
	}

}



class Ferry extends Thread 
{
	private int port = 0;		//Start at port 0
	private int load = 0; 	//Load is zero
	private int numCrossings;	//number of crossings to execute
	
	//Semaphores
	public Semaphore semBoardPort0;
	public Semaphore semBoardPort1;
	public Semaphore semDisembark;
	public Semaphore semDepart;
	
	public Ferry(int port, int numberOfTours)
	{
		this.port = port;
		numCrossings = numberOfTours;
		semBoardPort0 = new Semaphore(0,true);
		semBoardPort1 = new Semaphore(0,true);
		semDisembark = new Semaphore(0,true);
		semDepart = new Semaphore(0,true);
	}
	
	public void run()
	{
		int i;
		System.out.println("Start at port " + " with a load of  " + load + " vehicles");
		semBoardPort0.release(); //Cars can board
		
		//numCrossing in a day
		for(i=0; i<numCrossings; i++)
		{
			semDepart.acquireUninterruptibly();  //Wait for the signal for departure
			//Crossing
			System.out.println("Departure from port "+ port + " with a load of "+load + " vehicles");
			System.out.println("Crossing " + i + " with load of "+load+ " vehicles");
			port = 1 - port;
			
			//Travel time
			try
			{
				sleep((int) (100*Math.random()));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			//Arrive at port
			System.out.println("Arrive at port "+port+" with a load of "+load+" vehicles");
			
			//Disembark
			semDisembark.release();
		}
	}
	
	//Methods to manipulate the load of the ferry
	public int getLoad()
	{
		return load;
	}
	public void addLoad()
	{
		load = load + 1;
	}
	public void reduceLoad()
	{
		load = load - 1;
	}
}