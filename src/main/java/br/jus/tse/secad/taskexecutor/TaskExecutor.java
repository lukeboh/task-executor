package br.jus.tse.secad.taskexecutor;

import java.util.Date;

import br.jus.tse.secad.taskexecutor.util.TimeUnit1Ponto6;

/**
 * Abstrai um executor de tarefas, responsável por executar (ou delegar) a
 * execução de tarefas.<br>
 * Uma <strong>tarefa</strong> é a menor porção de trabalho, indivisível, que é
 * executada pelo executor.<br>
 * Uma <strong>thread</strong> é responsável por executar várias tarefas, uma de
 * cada vez. O Executor pode ter várias threads executadas as tarefas
 * simultaneamete.<br>
 * O <code>Task Executor</code> pode ou não ter um buffer. Se tiver, há métodos
 * para retornar a utilização do buffer.
 * 
 * @author luciano.bohnert
 */
public interface TaskExecutor {

	/**
	 * Retorna o número de threads que estão sendo executadas simultaneamente
	 * para processamento das tarefas.
	 */
	int getThreadCount();

	/**
	 * Define o número de threads que deve ser executadas simultaneamente para
	 * processar as tarefas.
	 */
	void setThreadCount(int value);

	/**
	 * Retonar o percentual do esforço efetuado em relação ao trabalho total a
	 * ser feito.
	 * 
	 * @return o Resultado é exibido em termos de porcentagem - 0.0 a 100.0.
	 */
	double getProgress();

	/**
	 * Retorna o total de tarefas a ser executado para completar o
	 * processamento.
	 * 
	 * @return 0 ou negativo caso não seja conhecido a quantidade total de
	 *         tarefas. Sem essa informação, a previsão de final do
	 *         processamento não existe.
	 */
	long getTasksCount();

	/**
	 * Retorna quantidade de tarefas com processamento completo.
	 */
	long getCompletedTaskCount();

	/**
	 * Caso suporte buffer,retonar o percentual usado do buffer.
	 * 
	 * @return o Resultado é exibido em termos de porcentagem - 0.0 a 100.0.
	 *         Retorna {@link TaskExecutor#getQueueSize()} /
	 *         {@link TaskExecutor#getQueueCapacity()}.
	 */
	double getQueueUsage();

	/**
	 * Caso suporte buffer, retorna a capacidade total do buffer.
	 * 
	 * @return Tamanho do buffer.
	 */
	int getQueueCapacity();

	/**
	 * Caso suporte buffer, retorna a quantidade de tarefas no buffer.
	 */
	int getQueueSize();

	/**
	 * Retorna a hora de início do processo. Não se confunde com a hora do
	 * processamento, que é a hora que se inicia o processamento da primeira
	 * tarefa.
	 */
	Date getStartTime();

	/**
	 * Método facilitador, que retorna {@link #getStartTime()} formatado para
	 * apresentação.
	 */
	String getStartTimeFormatted();

	/**
	 * Retorna a hora que terminou o processamento da última tarefa.
	 */
	Date getEndTime();

	/**
	 * Método facilitador, que retorna {@link #getEndTime()} formatado para
	 * apresentação.
	 */
	String getEndTimeFormatted();

	/**
	 * Retorna o período de tempo gasto até o momemto no processamento das
	 * tarefas.
	 */
	Date getElapsedTime();

	/**
	 * Método facilitador, que retorna {@link #getElapsedTime()} formatado para
	 * apresentação.
	 */
	String getElapsedTimeFormatted();

	/**
	 * Retorna a hora de início do processamento. Não se confunde com a hora do
	 * processo, que é a hora que se inicia o processo.
	 */
	Date getProcessingStartTime();

	/**
	 * Método facilitador, que retorna {@link #getProcessingStartTime()}
	 * formatado para apresentação.
	 */
	String getProcessingStartTimeFormatted();

	/**
	 * Baseado na velocidade média {@link #getAverageSpeed()} e na quantidade de
	 * tarefas restantes, calcula o instante mais provável de término do
	 * processamento.
	 */
	Date getEstimatedEndTime();

	/**
	 * Método facilitador, que retorna {@link #getEstimatedEndTime()} formatado
	 * para apresentação.
	 */
	String getEstimatedEndTimeFormatted();

	/**
	 * Retorna a velocidade média do processo em tarefas completadas por unidade
	 * de tempo.
	 * 
	 * @param unit
	 *            Se for passado null, assume o default que é em milisegundos.
	 */
	double getAverageSpeed(TimeUnit1Ponto6 unit);

	/**
	 * Retorna {@link #getAverageSpeed(TimeUnit1Ponto6)} com parâmetro default
	 * {@link TimeUnit1Ponto6#SECONDS}
	 */
	double getAverageSpeed();

	/**
	 * Método facilitador, que retorna {@link #getAverageSpeed(TimeUnit1Ponto6)
	 * )} formatado para apresentação.
	 */
	String getAverageSpeedFormatted(TimeUnit1Ponto6 unit);

	/**
	 * Retorna o tempo médio de execução de cada tarefa.
	 */
	double getAverageTaskTime(TimeUnit1Ponto6 unit);

	/**
	 * Método facilitador, que retorna
	 * {@link #getAverageTaskTime(TimeUnit1Ponto6)} formatado para apresentação.
	 */
	String getAverageTaskTimeFormatted(TimeUnit1Ponto6 unit);

	/**
	 * Retorna o tempo de execução da última tarefa completa.
	 */
	double getLastTaskTime(TimeUnit1Ponto6 unit);

	/**
	 * Método facilitador, que retorna {@link #getLastTaskTime(TimeUnit1Ponto6)}
	 * formatado para apresentação.
	 */
	String getLastTaskTimeFormatted(TimeUnit1Ponto6 unit);

	/**
	 * Retorna a velocidade instantânea de execução das tarefas. A metodologia
	 * de calculo é responsabilidade de cada implementação.
	 */
	double getInstantSpeed(TimeUnit1Ponto6 unit);

	/**
	 * Método facilitador, que retorna {@link #getInstantSpeed(TimeUnit1Ponto6)}
	 * formatado para apresentação.
	 */
	String getInstantSpeedFormatted(TimeUnit1Ponto6 unit);
	
	/**
	 * Exibe uma interface com o usuário padrão.
	 */
	void showUI();
	
	/**
	 * Exibe uma interface com o usuário padrão.
	 * @param exitOnClose Se <code>true</code>, sai da aplicação ao fechar a janela. 
	 */
	void showUI(boolean exitOnClose);

}