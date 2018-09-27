package br.jus.tse.secad.taskexecutor;

import org.apache.log4j.Logger;

public class PopulaAA_QTD_SECAO_SEM_PINCARunnable implements Runnable{
	
	private static Logger log = Logger.getLogger(PopulaAA_QTD_SECAO_SEM_PINCARunnable.class);
	
	public int index;
	public String codObjetoSecao;
	
	public PopulaAA_QTD_SECAO_SEM_PINCARunnable(int index, String codObjetoSecao) {
		this.index = index;
		this.codObjetoSecao = codObjetoSecao;
	}
	
	public void run() {
		try {
			log.info("Secao [" + index +"] ["+ codObjetoSecao + "]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}