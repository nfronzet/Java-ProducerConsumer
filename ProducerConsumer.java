import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class ProducerConsumer {
	
	private static Semaphore mutex = new Semaphore(1);
	private static Semaphore empty = new Semaphore(5);
	private static Semaphore full = new Semaphore(0);
	private static Queue<Integer> buffer = new ArrayDeque<>(5);
	
	private static Random generator = new Random();
	
	static class Producer extends Thread {
		
		@Override
		public void run() {
			
			for (int i = 0; i < 100; i++) {
				try {
					Thread.sleep(generator.nextInt(500));
					mutex.acquire();
					if (full.availablePermits() < 5) {
						full.release();
						empty.acquire();
	
						int val = Math.abs(generator.nextInt());
						buffer.add(val);
						System.out.println("Producer produced " + Integer.toString(val));
					}
				} catch(InterruptedException e) {
					e.printStackTrace();
				} finally {
					mutex.release();
				}
			}
		}
	}
	
	static class Consumer extends Thread {
		
		@Override
		public void run() {
			
			for (int j = 0; j < 100; j++) {
				try {
					Thread.sleep(generator.nextInt(500));
					mutex.acquire();
					if (empty.availablePermits() < 5) {
						empty.release();
						full.acquire();
					
						int val = buffer.remove();
						System.out.println("\tConsumer consumed " + Integer.toString(val));
					}
				} catch(InterruptedException e) {
					e.printStackTrace();
				} finally {
					mutex.release();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		int sleepTime = 20;
		int nProducers = 5;
		int nConsumers = 1;
		if (args.length != 3) {
			System.out.println("Invalid argument format, using default values");
		} else {
			try {
				sleepTime = Integer.parseInt(args[0]);
				nProducers = Integer.parseInt(args[1]);
				nConsumers = Integer.parseInt(args[2]);
				if (sleepTime < 0 || nProducers < 0 || nConsumers < 0)
					throw new NumberFormatException();
				System.out.println("Using arguments from command line");
			} catch (NumberFormatException n) {
				System.out.println("Invalid arguments, using default values");
				sleepTime = 20;
				nProducers = 5;
				nConsumers = 1;
			}
		}
		System.out.println("Sleep time = " + sleepTime + "\nProducer threads = " + nProducers + "\nConsumer threads = " + nConsumers);
		ArrayList<Thread> producers = new ArrayList<>();
		ArrayList<Thread> consumers = new ArrayList<>();
		for (int i = 0; i < nProducers || i < nConsumers; i++) {
			if (i < nProducers)
				producers.add(new Thread(new Producer()));
			if (i < nConsumers)
				consumers.add(new Thread(new Consumer()));
		}
		for (int i = 0; i < nProducers || i < nConsumers; i++) {
			if (i < nProducers)
				producers.get(i).start();
			if (i < nConsumers)
				consumers.get(i).start();
		}
		try {
			Thread.sleep(1000*sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
}
