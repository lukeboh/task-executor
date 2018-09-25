package br.jus.tse.secad.taskexecutor;

import org.apache.log4j.Logger;

public class DumbFactory implements RunnableFactory {
	private static Logger log = Logger.getLogger(DumbFactory.class);
	
	int size = 100;
	
	int next = 1;
	
	public Runnable next() {
		if (next > size)
			return null;
		final int internal = next++;
		
		return new Runnable() {
			
			public void run() {
				try {
					Thread.sleep((long) (10 * 1000 * Math.random()));
					log.info("Terminou " + internal);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
	}

	public int size() {
		return 0;
	}

}
