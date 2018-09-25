package br.jus.tse.secad.taskexecutor;

/**
 * Interface que define a fábrica de tarefas a ser executada.
 */
public interface RunnableFactory {

	public Runnable next();

	public int size();
}
