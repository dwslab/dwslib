package de.dwslab.dwslib.framework;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * This class provides a framework to process a given set of inputs in parallel
 * using a set of threads. The methods {@link Processor#beforeProcess()} and
 * {@link Processor#afterProcess()} can be overwriten to execute any procedures
 * before and after the tasks are processed in parallel. This methods are run in
 * single thread.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public abstract class Processor<E> {

	/**
	 * The number of parallel processes which are used.
	 */
	private int threads;
	/**
	 * The logger.
	 */
	protected static Logger log;
	/**
	 * List of objects which are going to be processed in parallel.
	 */
	private List<E> objectToProcess;
	/**
	 * Number of times a failed object from the
	 * {@link Processor#objectToProcess} is requeued.
	 */
	private int numberOfMaxFails = 3;

	/**
	 * Constructor for a given number of parallel threads.
	 * 
	 * @param threads
	 *            , number of parallel threads which are used. If smaller 1,
	 *            number of cores is used.
	 */
	public Processor(int threads) {
		if (threads < 1) {
			System.out
					.println("Number of threads will be set to number of avaible processors.");
			this.threads = Runtime.getRuntime().availableProcessors();
		} else {
			this.threads = threads;
		}
		try {
			log = Logger.getLogger(getClass().getEnclosingClass()
					.getSimpleName());
		} catch (NullPointerException ne) {
			log = Logger.getLogger("Processor.java");
			log.log(Level.WARNING, "Could not obtain class name");
		}
	}

	/**
	 * Keeps track of the process and the current status of the
	 * {@link ThreadPoolExecutor}
	 * 
	 * @param executor
	 *            the {@link ThreadPoolExecutor} including all threads to be
	 *            processed
	 * @param startTime
	 *            The time the executor was started.
	 * @return The difference between the total number of threads and the number
	 *         of finished threads.
	 */
	private long printState(ThreadPoolExecutor executor, long startTime) {
		long total = executor.getTaskCount();
		
		long finished = executor.getCompletedTaskCount();
		long runtime = (System.currentTimeMillis() - startTime);
		long perItem = (long) (((float) runtime) / finished);
		long left = (long) (((float) runtime / finished) * (total - finished));

		if ((((float) runtime) / finished) == Float.POSITIVE_INFINITY) {
			perItem = -1;
			left = -1;
		}

		log.log(Level.INFO,
				new Date()
						+ " Runtime: "
						+ DurationFormatUtils.formatDuration(runtime,
								"HH:mm:ss.S")
						+ " --> Total: "
						+ total
						+ ", Done: "
						+ finished
						+ ", "
						+ (perItem == -1 ? "..." : DurationFormatUtils
								.formatDuration(perItem, "HH:mm:ss.S"))
						+ " / item"
						+ ", Finished in: "
						+ (left == -1 ? "..." : DurationFormatUtils
								.formatDuration(left, "HH:mm:ss.S")));

		return total - finished;
	}

	/**
	 * Implement this function to fill the task queue with objects of type E.
	 * 
	 * @return
	 */
	protected abstract List<E> fillListToProcess();

	/**
	 * Overwrite this function whenever you need the {@link Processor#process()}
	 * to execute any method before starting processing its tasks.
	 */
	protected void beforeProcess() {
	}

	/**
	 * Overwrite this function whenever you need the {@link Processor#process()}
	 * to execute any method after all tasks are processed.
	 */
	protected void afterProcess() {
	}

	/**
	 * Executes the whole process.
	 */
	public void process() {
		long startTime = new Date().getTime();
		log.log(Level.INFO, new Date() + " " + "Starting.");

		objectToProcess = fillListToProcess();

		beforeProcess();

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(threads);

		long l = System.currentTimeMillis();
		for (E object : objectToProcess) {
			executor.submit(new Worker<E>(object, this, executor));

			if (System.currentTimeMillis() - l > 5000) {
				printState(executor, startTime);
				l = System.currentTimeMillis();
			}
		}

		long stillTodo = printState(executor, startTime);

		// reset start time so avg time per item is not affected by set-up time
		startTime = new Date().getTime();
		while (stillTodo != 0) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			stillTodo = printState(executor, startTime);
		}

		executor.shutdown();

		afterProcess();

		log.log(Level.INFO, new Date() + " " + "Done.");
	}

	/**
	 * Implementation of the Worker, which will run in parallel within the
	 * {@link Processor}.
	 * 
	 * @author Robert Meusel (robert@dwslab.de)
	 * 
	 * @param <E>
	 *            The given object type.
	 */
	@SuppressWarnings("hiding")
	private class Worker<E> implements Runnable {
		private E object;
		private Processor<E> p;
		private ThreadPoolExecutor executor;
		private int numberOfFails = 0;

		public Worker(E object, Processor<E> p, ThreadPoolExecutor executor) {
			this.object = object;
			this.p = p;
			this.executor = executor;
		}

		/**
		 * Run.
		 */
		public void run() {
			try {
				p.process(object);
			} catch (Exception e) {
				log.log(Level.WARNING, new Date()
						+ " Worker-task failed for the " + numberOfFails + ": "
						+ e.getMessage());
				e.printStackTrace();
				if (numberOfFails++ < numberOfMaxFails) {
					requeue();
				}
			}
		}

		/**
		 * Requeue if smaller current number of fails is smaller
		 * {@link Processor#numberOfMaxFails}
		 */
		private void requeue() {
			log.log(Level.INFO,
					new Date() + " requeue worker for " + object.toString());
			executor.submit(this);
		}

	}

	/**
	 * Method which needs to be implement and contains the code to process one
	 * object E
	 * 
	 * @param object
	 *            the object which needs to be processed.
	 * @throws Exception
	 *             any possible exception which could be thrown.
	 */
	protected abstract void process(E object) throws Exception;

}
