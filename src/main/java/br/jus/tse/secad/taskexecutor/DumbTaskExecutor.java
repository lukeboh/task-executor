package br.jus.tse.secad.taskexecutor;

public class DumbTaskExecutor extends DefaultTaskExecutor {

	public static void main(String[] args) {
		DefaultTaskExecutor dte = new DefaultTaskExecutor(new DumbFactory());
		dte.showUI();
		dte.start();
	}
}
