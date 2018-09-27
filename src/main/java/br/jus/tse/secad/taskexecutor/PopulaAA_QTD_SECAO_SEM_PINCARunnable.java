package br.jus.tse.secad.taskexecutor;

import org.apache.log4j.Logger;

public class PopulaAA_QTD_SECAO_SEM_PINCARunnable implements Runnable{
	
	private static Logger log = Logger.getLogger(PopulaAA_QTD_SECAO_SEM_PINCARunnable.class);
	
	public int index;
	public String pmt1, pmt2, pmt3;
	
	public PopulaAA_QTD_SECAO_SEM_PINCARunnable(int index, String pmt1, String pmt2, String pmt3) {
		this.index = index;
		this.pmt1 = pmt1;
		this.pmt2 = pmt2;
		this.pmt3 = pmt3;
		
	}
	
	public void run() {
		try {
			log.info("Linha [" + index + "] ["+ pmt1 + "] [" + pmt2  + "] [" + pmt3 + "]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}