package br.jus.tse.secad.taskexecutor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import br.jus.tse.secad.taskexecutor.swing.BasicControlPanel;
import br.jus.tse.secad.taskexecutor.util.RelativeDateFormat;
import br.jus.tse.secad.taskexecutor.util.TimeUnit1Ponto6;

public abstract class AbstractTaskExecutor implements TaskExecutor {
	private static Logger log = Logger.getLogger(AbstractTaskExecutor.class);
	private static SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static RelativeDateFormat dfDif = new RelativeDateFormat();
	private static final NumberFormat nf = new DecimalFormat("#.#");
	static {
		dfDif.setTimeZone(TimeZone.getTimeZone("GMT"));
		NumberFormat nf = new DecimalFormat("00");
		dfDif.setSecondFormatter(nf);
		dfDif.setMinuteFormatter(nf);
		dfDif.setHourFormatter(nf);
	}
	
	/**
	 * Indica a hora que se iniciou a aplicação, antes de começar o processamento.
	 */
	private Date startTime;
	/**
	 * Indica a hora que se finalizou o processamento.
	 */
	private Date endTime;	
	/**
	 * Indica a hora que iniciou o processamento das tarefas.
	 */
	private Date processingStartTime;
	/**
	 * Guarda o tempo de execução da última tarefa completa.
	 */
	private long lastTaskTime;
	/**
	 * Guarda tempo total de processamento de todas as tarefas
	 */
	private long allTasksTime;
	/**
	 * Armazena a quantidade de tarefas já completadas.
	 */
	private long completedTaskCount;
	/**
	 * Armazena a hora que foi calculado a velocidade instantânea pela última vez.
	 */
	private long lastTimeInstantSpeedCalculated;
	/**
	 * Guarda a Velocidade instantânea do processamento.
	 */
	private double instantSpeed;
	
	/**
	 * Tabela contendo dados de tarefas já iniciadas.
	 */
	private Hashtable<Object, TaskData> initializedTasksTable = new Hashtable<Object, TaskData>();
	/**
	 * Fila utilizada para calculo da velocidade instatânea.
	 */
	private CompletedTasksQueue completedTasksQueue = new CompletedTasksQueue(200);
	
	public synchronized void start() {
		if (startTime == null) {
			log.info("Start");
			startTime = new Date();
		}
	}
	
	public final Date getStartTime() {
		return startTime;
	}

	public final String getStartTimeFormatted() {
		return format(startTime);
	}
	
	public final Date getEndTime() {
		return endTime;
	}
	
	public final String getEndTimeFormatted() {
		return format(endTime);
	}

	public final Date getElapsedTime() {
		if (processingStartTime != null)
			return new Date(System.currentTimeMillis() - processingStartTime.getTime());
		return null;
	}

	public final String getElapsedTimeFormatted() {
		Date date = getElapsedTime();
		if (date != null) {
			return dfDif.format(date);
		} else
			return "00h00m00s";
	}

	public final Date getProcessingStartTime() {
		return processingStartTime;
	}

	public final String getProcessingStartTimeFormatted() {
		return format(processingStartTime);
	}
	
	/**
	 * Retonar o percentual do esforço efetuado.
	 * 
	 * @return o Resultado é Exibido em termos de porcentagem - 0.0 a 100.0.
	 */
	public final double getProgress() {
		if (getTasksCount() > 0)
			return getCompletedTaskCount() * 100.0D / getTasksCount();
		else
			return 0;
	}
	
	/**
	 * Retonar o percentual usado do buffer.
	 * 
	 * @return o Resultado é Exibido em termos de porcentagem - 0.0 a 100.0.
	 */
	public final double getQueueUsage() {
		int gc = getQueueCapacity();
		if (gc != 0)
			return (100.0D * getQueueSize() / gc);
		return 0;
	}
	
	public final long getCompletedTaskCount() {
		return completedTaskCount;
	}

	/**
	 * Retorna a velocidade média do processo em tarefas completadas por unidade de tempo.
	 * 
	 * @param unit
	 *            Se for passado null, assume o default que é em milisegundos.
	 */
	public final double getAverageSpeed(TimeUnit1Ponto6 unit) {
		double returnz = 0.0D;
		if (getElapsedTime() != null) {
			returnz = 1.0D * getCompletedTaskCount() / getElapsedTime().getTime(); // em
																								// milisegundos
			if (unit == TimeUnit1Ponto6.SECONDS)
				returnz *= 1000; // em segundos
			else if (unit == TimeUnit1Ponto6.MINUTES)
				returnz *= 60 * 1000; // em minutos
			else if (unit == TimeUnit1Ponto6.HOURS)
				returnz *= 60 * 60 * 1000; // em horas
		}
		return returnz;
	}

	public final double getAverageSpeed() {
		return getAverageSpeed(null);
	}

	public final Date getEstimatedEndTime() {
		double averageSpeed = getAverageSpeed();
		long taskCount = getTasksCount();
		if (averageSpeed != 0.0D && taskCount > 0) {
			long end = (long) ((taskCount - getCompletedTaskCount()) / averageSpeed);
			end += System.currentTimeMillis();
			return new Date(end);
		}
		return null;
	}
	
	public final String getEstimatedEndTimeFormatted() {
		return format(getEstimatedEndTime());
	}

	public final String getAverageSpeedFormatted(TimeUnit1Ponto6 unit) {
		String returnz = nf.format(getAverageSpeed(unit)) + " task/";
		if (unit == TimeUnit1Ponto6.SECONDS)
			returnz += "sec";
		else if (unit == TimeUnit1Ponto6.MINUTES)
			returnz += "min";
		else if (unit == TimeUnit1Ponto6.HOURS)
			returnz += "hour";
		else
			returnz += "sec";
		return returnz;
	}

	public final double getAverageTaskTime(TimeUnit1Ponto6 unit) {
		long completedTaskCount = getCompletedTaskCount();
		if (completedTaskCount == 0)
			return 0.0D;
		double returnz = 1.0D * allTasksTime / completedTaskCount;
		if (unit == TimeUnit1Ponto6.SECONDS)
			return returnz / 1000.0D; // em segundos
		else if (unit == TimeUnit1Ponto6.MINUTES)
			return returnz / (1000.0D * 60); // em minutos
		else if (unit == TimeUnit1Ponto6.HOURS)
			return returnz / (1000.0D * 60 * 60); // em horas
		return returnz;
	}

	public final String getAverageTaskTimeFormatted(TimeUnit1Ponto6 unit) {
		String returnz = nf.format(getAverageTaskTime(unit));
		if (unit == TimeUnit1Ponto6.SECONDS)
			return returnz + " sec/task";
		else if (unit == TimeUnit1Ponto6.MINUTES)
			return returnz + " min/task";
		else if (unit == TimeUnit1Ponto6.HOURS)
			return returnz + " hours/task";
		else
			return returnz + " milis/task";
	}

	public final double getLastTaskTime(TimeUnit1Ponto6 unit) {
		if (unit == TimeUnit1Ponto6.SECONDS)
			return lastTaskTime / 1000.0; // em segundos
		else if (unit == TimeUnit1Ponto6.MINUTES)
			return lastTaskTime / (1000.0 * 60); // em minutos
		else if (unit == TimeUnit1Ponto6.HOURS)
			return lastTaskTime / (1000.0 * 60 * 60); // em horas
		return lastTaskTime;
	}

	public final String getLastTaskTimeFormatted(TimeUnit1Ponto6 unit) {
		String returnz = nf.format(getLastTaskTime(unit));
		if (unit == TimeUnit1Ponto6.SECONDS)
			return returnz + " sec";
		else if (unit == TimeUnit1Ponto6.MINUTES)
			return returnz + " min";
		else if (unit == TimeUnit1Ponto6.HOURS)
			return returnz + " hours";
		else
			return returnz + " milis";
	}
	
	public final double getInstantSpeed(TimeUnit1Ponto6 unit) {
		if (completedTaskCount < 1)
			return 0.0D;
		
		double returnz = instantSpeed;
		
		if (lastTimeInstantSpeedCalculated < completedTasksQueue.getLastChangeTime()
				|| lastTimeInstantSpeedCalculated + 3 * 1000 < System.currentTimeMillis()) {
			lastTimeInstantSpeedCalculated = completedTasksQueue.getLastChangeTime();
			long firstFinished = Long.MAX_VALUE;
			long lastFinished = 0L;
			int amout = 0;
			for (TaskData task : completedTasksQueue) {
				//if (task.getEndTime().getTime() + 5000/* 5 * task.getTaskTime() */< System
				//		.currentTimeMillis())
				//	continue;
				if (task.getEndTime().getTime() < firstFinished) {
					firstFinished = task.getEndTime().getTime();
				}
				if (task.getEndTime().getTime() > lastFinished) {
					lastFinished = task.getEndTime().getTime();
				}
				amout++;
			}
			returnz = 1.0D * amout / (lastFinished - firstFinished);
			if (!Double.isInfinite(returnz))
				instantSpeed = returnz;
		}
		
		if (unit == TimeUnit1Ponto6.SECONDS)
			returnz = instantSpeed * 1000.0; // em segundos
		else if (unit == TimeUnit1Ponto6.MINUTES)
			returnz = instantSpeed * (1000.0 * 60); // em minutos
		else if (unit == TimeUnit1Ponto6.HOURS)
			returnz = instantSpeed * (1000.0 * 60 * 60); // em horas
		return returnz;
	}

	public final String getInstantSpeedFormatted(TimeUnit1Ponto6 unit) {
		String returnz = nf.format(getInstantSpeed(unit)) + " task/";
		if (unit == TimeUnit1Ponto6.SECONDS)
			return returnz + "sec";
		else if (unit == TimeUnit1Ponto6.MINUTES)
			return returnz + "min";
		else if (unit == TimeUnit1Ponto6.HOURS)
			return returnz + "hours";
		else
			return returnz + "milis";
	}

	public final synchronized void taskStart(Object task) {
		if (initializedTasksTable.contains(task)){
			throw new RuntimeException("Tentando iniciar tarefa já iniciada [" + task + "]");
		}
		processingStart();
		initializedTasksTable.put(task, new TaskData());
	}
	
	public final synchronized void taskEnd(TaskDecorator task) {
		TaskData taskData = initializedTasksTable.get(task);
		if (taskData == null){
			throw new RuntimeException("Tentando finalizar tarefa não iniciada [" + task + "]");
		}
		completedTaskCount+= task.getSize();
		taskData.end();
		initializedTasksTable.remove(task);
		long thisTasktime = lastTaskTime = taskData.getTaskTime();
		allTasksTime += thisTasktime;
		try {
			completedTasksQueue.put(taskData);
			long taskCount = getTasksCount();
			if (taskCount > 0 && getCompletedTaskCount() + 1 >= taskCount)
				processingEnd();
		} catch (InterruptedException e) {
			log.error("Falha ao adicionar tarefa completada na fila.", e);
		}
	}
	
	public void showUI(boolean exitOnClose) {
		JFrame f = BasicControlPanel.getJFrame(this);
		if (exitOnClose)
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	 public void showUI() {
		showUI(true);
	}

	/*
	 * MÉTODOS PROTEGIDOS
	 */
	
	protected synchronized void processingEnd() {
		if (endTime == null) {
			log.info("Processing End");
			endTime = new Date();
		}
	}
	
	/*
	 * MÉTODOS PRIVADOS
	 */
	private synchronized void processingStart() {
		if (processingStartTime == null) {
			log.info("Processing Start");
			processingStartTime = new Date();
		}
	}
	
	private static String format(Date date) {
		if (date != null) {
			return df.format(date);
		} else
			return "--/--/--";
	}

	public class TaskDecorator implements Runnable {

		private Task toRun;

		TaskDecorator(Task toRun) {
			this.toRun = toRun;
		}

		public void run() {
			taskStart(this);
			toRun.run();
			taskEnd(this);
		}
		
		public int getSize(){
			return toRun.getSize();
		}
	}
	/*
	 * PRIVATE INNER CLASSES 
	 */
	private class TaskData {
		private Date initialTime;
		private Date endTime;
		
		public TaskData() {
			this.initialTime = new Date();
		}
		
		public void end(){
			this.endTime = new Date();
		}
		
		public Date getEndTime() {
			return endTime;
		}
		
		long getTaskTime() {
			if (endTime != null)
				return endTime.getTime() - initialTime.getTime();
			return 0L;
		}
	}
	
	private final class CompletedTasksQueue extends ArrayBlockingQueue<TaskData> {

		long lastChangeTime = System.currentTimeMillis();

		CompletedTasksQueue(int capacity) {
			super(capacity, true);
		}

		public synchronized void put(TaskData o) throws InterruptedException {
			lastChangeTime = System.currentTimeMillis();
			if (remainingCapacity() == 0) {
				poll();
			}
			super.put(o);
		}

		long getLastChangeTime() {
			return lastChangeTime;
		}
	}

}
