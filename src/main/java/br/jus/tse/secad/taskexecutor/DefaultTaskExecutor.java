package br.jus.tse.secad.taskexecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import br.jus.tse.secad.taskexecutor.util.PropertiesUtil;

/**
 * Conceitos: Task: Tarefa a ser executada. Tarefas são produzidas para serem consumidas. Threads:
 * Aqueles que executam/consomem as tasks.
 * 
 * @author luciano.bohnert
 */
public class DefaultTaskExecutor extends AbstractTaskExecutor {

	private static Logger log = Logger.getLogger(DefaultTaskExecutor.class);

	/**
	 * Tamanho inicial do pool de threads. Não pode ser menor do que 1!!!
	 */
	private int initialThreadsSize = 0;

	private BlockingQueue<Runnable> tasksQueue = new LinkedBlockingQueue<Runnable>(PropertiesUtil.getQueueSize());

	private ThreadPoolExecutor threadPool;
	/**
	 * Indica a quantidade de tarefas que serão executadas
	 */
	private long tasksCount;

	private TaskFactory runnableFactory;
	
	public DefaultTaskExecutor() {
	}
	
	public DefaultTaskExecutor(TaskFactory factory){
		this.runnableFactory = factory;
	}
	
	public void setRunnableFactory(TaskFactory runnableFactory) {
		this.runnableFactory = runnableFactory;
	}

	public void start() {
		super.start();
		log.info("Creating Thread Pool. Size[" + initialThreadsSize + "]");
		threadPool = new br.jus.tse.secad.taskexecutor.concurrent.CustomizedThreadPoolExecutor(initialThreadsSize,
				initialThreadsSize == 0 ? 1 : initialThreadsSize, 1, TimeUnit.SECONDS, tasksQueue,
				new ThreadPoolExecutor.CallerRunsPolicy());

		this.tasksCount = runnableFactory.getSize();

		new Thread("Producer") {
			public void run() {
				Task next = null;
				long taskProduced = 0;
				while ((next = runnableFactory.next()) != null) {
					threadPool.submit(new TaskDecorator(next));
					taskProduced++;
				}
				if (DefaultTaskExecutor.this.tasksCount == 0)
					DefaultTaskExecutor.this.tasksCount = taskProduced;
			};
		}.start();
	}

	public int getThreadCount() {
		if (threadPool != null)
			return threadPool.getActiveCount();
		else
			return 0;
	}

	public void setThreadCount(int value) {
		if (threadPool != null) {
			threadPool.setCorePoolSize(value);
			threadPool.setMaximumPoolSize(value);
		} else {
			this.initialThreadsSize = value;
		}
	}

	public long getTasksCount() {
		return tasksCount;
	}

	public int getQueueCapacity() {
		if (threadPool != null) {
			return threadPool.getQueue().size() + threadPool.getQueue().remainingCapacity();
		} else {
			return 0;
		}
	}

	public int getQueueSize() {
		if (threadPool != null) {
			return threadPool.getQueue().size();
		} else {
			return 0;
		}
	}
}
