package br.jus.tse.secad.taskexecutor;

/**
 * Interface que define a fábrica de tarefas a ser executada.
 */
public interface TaskFactory {

	public Task next();

	public int getSize();
}
