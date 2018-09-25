package br.jus.tse.secad.taskexecutor.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import br.jus.tse.secad.taskexecutor.DumbFactory;
import br.jus.tse.secad.taskexecutor.TaskExecutor;
import br.jus.tse.secad.taskexecutor.DefaultTaskExecutor;
import br.jus.tse.secad.taskexecutor.util.TimeUnit1Ponto6;

/**
 * Panel básico de apresentação das atividades sendo executadas.
 * 
 * @author luciano.bohnert
 */
public class BasicControlPanel extends JPanel {
	private static Logger log = Logger.getLogger(BasicControlPanel.class);

	private TaskExecutor taskExecutor;
	//private JTextField tfThreadCount;
	private JProgressBar pbFullProgress;
	private JTextField tfCurrentAmount;
	private JTextField tfTotalAmount;
	private JProgressBar pbQueueBuffer;
	private JTextField tfQueueSize;
	private JTextField tfQueueCapacity;
	private JTextField tfStart;
	private JTextField tfElapsedTime;
	private JTextField tfEstimatedEndTime;
	private JTextField tfEndTime;
	private JTextField tfProcessingStart;
	private JTextField tfAverageSpeed;
	private JTextField tfAverageTaskTime;
	private JTextField tfInstantSpeed;
	private JTextField tfLastTaskTime;
	private JRadioButton rbSec;
	private JRadioButton rbMin;
	private JRadioButton rbHou;
	
	private TimeUnit1Ponto6 timeUnit = TimeUnit1Ponto6.SECONDS;
	private ThreadsCountVsSpeedGraphicPanel threadsCountVsSpeedGraphicPanel;
	private ThreadCountDial threadCountDial;
	
	public BasicControlPanel(TaskExecutor taskExecutor) {
		setTaskExecutor(taskExecutor);
		
		setLayout(new BorderLayout());
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		BevelBorder border = new BevelBorder(BevelBorder.LOWERED);
		leftPanel.setBorder(new TitledBorder(border, "Control", TitledBorder.LEFT,
				TitledBorder.ABOVE_TOP));
		leftPanel.setPreferredSize(new Dimension(280, 800));
		add(leftPanel, BorderLayout.WEST);
		
		//Panel do Slider da Quantidade de Threads
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBorder(new TitledBorder("Thread Count"));
		
		threadCountDial = new ThreadCountDial();
		threadCountDial.getSlider().addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				int value = threadCountDial.getSlider().getValue();
				BasicControlPanel.this.taskExecutor.setThreadCount(value);
			}
		});
		
		p.add(threadCountDial, BorderLayout.CENTER);
		//p.add(tfThreadCount, BorderLayout.EAST);
		leftPanel.add(p);
		
		//Panel da Barra de Progresso Total
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(new TitledBorder("Full Progress"));
		
		pbFullProgress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		p.add(pbFullProgress);
		leftPanel.add(p);
		
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		
		tfCurrentAmount = new JTextField();
		tfCurrentAmount.setEditable(false);
		tfCurrentAmount.setFont(new Font("SansSerif", Font.BOLD, 20));
		tfCurrentAmount.setHorizontalAlignment(JTextField.CENTER);
		tfCurrentAmount.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		tfCurrentAmount.setBorder(new TitledBorder("Current"));
		
		p1.add(tfCurrentAmount);
		
		tfTotalAmount = new JTextField();
		tfTotalAmount.setEditable(false);
		tfTotalAmount.setFont(new Font("SansSerif", Font.BOLD, 20));
		tfTotalAmount.setHorizontalAlignment(JTextField.CENTER);
		tfTotalAmount.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		tfTotalAmount.setBorder(new TitledBorder("Total"));
		
		p1.add(tfTotalAmount);
		
		p.add(p1);
		
		//Panel da Barra do Volume da Queue
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(new TitledBorder("Queue Buffer"));
		
		pbQueueBuffer = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		p.add(pbQueueBuffer);
		leftPanel.add(p);
		
		p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		
		tfQueueSize = new JTextField();
		tfQueueSize.setEditable(false);
		tfQueueSize.setFont(new Font("SansSerif", Font.BOLD, 20));
		tfQueueSize.setHorizontalAlignment(JTextField.CENTER);
		tfQueueSize.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		tfQueueSize.setBorder(new TitledBorder("Size"));
		
		p1.add(tfQueueSize);
		
		tfQueueCapacity = new JTextField();
		tfQueueCapacity.setEditable(false);
		tfQueueCapacity.setFont(new Font("SansSerif", Font.BOLD, 20));
		tfQueueCapacity.setHorizontalAlignment(JTextField.CENTER);
		tfQueueCapacity.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		tfQueueCapacity.setBorder(new TitledBorder("Capacity"));
		
		p1.add(tfQueueCapacity);
		
		p.add(p1);
		
		//Panel com tempos
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(new TitledBorder("Time Control"));
		
		JPanel pi = new JPanel();
		pi.setLayout(new GridLayout(3,2));
		tfStart = new JTextField("--/--/---- **:**:**");
		tfStart.setEditable(false);
		tfStart.setBorder(new TitledBorder("Start Time"));
		pi.add(tfStart);
		
		tfProcessingStart = new JTextField("--/--/---- **:**:**");
		tfProcessingStart.setEditable(false);
		tfProcessingStart.setBorder(new TitledBorder("Processing Start Time"));
		pi.add(tfProcessingStart);
		
		tfElapsedTime = new JTextField("--/--/---- **:**:**");
		tfElapsedTime.setEditable(false);
		tfElapsedTime.setBorder(new TitledBorder("Elapsed Time"));
		pi.add(tfElapsedTime);
		
		tfEstimatedEndTime = new JTextField("--/--/---- **:**:**");
		tfEstimatedEndTime.setEditable(false);
		tfEstimatedEndTime.setBorder(new TitledBorder("Estimated End Time"));
		pi.add(tfEstimatedEndTime);
		
		tfEndTime = new JTextField("--/--/---- **:**:**");
		tfEndTime.setEditable(false);
		tfEndTime.setBorder(new TitledBorder("End Time"));
		pi.add(tfEndTime);
		
		JButton btPrint = new JButton("Print Results");
		btPrint.setEnabled(false);
		//pi.add(btPrint);
		
		p.add(pi);
		
		leftPanel.add(p);
		
		//Panel de Velocidade
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(new TitledBorder("Speed Control"));
		
		pi = new JPanel();
		pi.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		tfAverageSpeed = new JTextField();
		tfAverageSpeed.setEditable(false);
		tfAverageSpeed.setBorder(new TitledBorder("Average Speed"));
		pi.add(tfAverageSpeed, c);
		
		tfAverageTaskTime = new JTextField();
		tfAverageTaskTime.setEditable(false);
		tfAverageTaskTime.setBorder(new TitledBorder("Average Task Time"));
		c.gridx = 1;
		c.gridy = 0;
		pi.add(tfAverageTaskTime, c);
		
		tfInstantSpeed = new JTextField();
		tfInstantSpeed.setEditable(false);
		tfInstantSpeed.setBorder(new TitledBorder("Instant Speed"));
		c.gridx = 0;
		c.gridy = 1;
		pi.add(tfInstantSpeed, c);
		
		tfLastTaskTime = new JTextField();
		tfLastTaskTime.setEditable(false);
		tfLastTaskTime.setBorder(new TitledBorder("Last Task Time"));
		c.gridx = 1;
		c.gridy = 1;
		pi.add(tfLastTaskTime, c);
		
		p.add(pi);
		
		//Panel de Unidade de Tempo para Velocidade
		JPanel pii = new JPanel();
		pii.setBorder(new TitledBorder("Time Unit"));
		pii.setLayout(new GridLayout(1,3));
		
		ButtonGroup bgTimeUnit = new ButtonGroup();
		
		rbSec = new JRadioButton("sec", true); 
		bgTimeUnit.add(rbSec);
		pii.add(rbSec);
		rbSec.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				timeUnit = TimeUnit1Ponto6.SECONDS;
				update();
			}
		});
		
		rbMin = new JRadioButton("min"); 
		bgTimeUnit.add(rbMin);
		pii.add(rbMin);
		rbMin.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				timeUnit = TimeUnit1Ponto6.MINUTES;
				update();
			}
		});
		
		rbHou = new JRadioButton("hour"); 
		bgTimeUnit.add(rbHou);
		pii.add(rbHou);
		rbHou.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				timeUnit = TimeUnit1Ponto6.HOURS;
				update();
			}
		});

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		pi.add(pii, c);
		
		leftPanel.add(p);
		//Panel Central
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setBorder(new TitledBorder(border, "Analysis", TitledBorder.LEFT,
				TitledBorder.ABOVE_TOP));
		add(centerPanel, BorderLayout.CENTER);
		//Panel com Graficos
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(new TitledBorder("Thread Count X Speed"));
		
		threadsCountVsSpeedGraphicPanel = new ThreadsCountVsSpeedGraphicPanel();
		p.add(threadsCountVsSpeedGraphicPanel);
		
		centerPanel.add(p);
		
		update();
		
		Thread t = new Thread("UI-Updater"){
			public void run() {
				while(true){
					try {
						update();
						if (BasicControlPanel.this.taskExecutor.getEndTime() != null)
							break;
						Thread.sleep(1000);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			};
		};
		t.start();
	}
	
	public void update(){
		threadCountDial.setCurrentValue(taskExecutor.getThreadCount());
		pbFullProgress.setValue((int) this.taskExecutor.getProgress());
		tfCurrentAmount.setText(String.valueOf(this.taskExecutor.getCompletedTaskCount()));
		long taskCount = this.taskExecutor.getTasksCount();
		if (taskCount > 0)
			tfTotalAmount.setText(String.valueOf(taskCount));
		pbQueueBuffer.setValue((int) this.taskExecutor.getQueueUsage());
		tfQueueSize.setText(String.valueOf(this.taskExecutor.getQueueSize()));
		tfQueueCapacity.setText(String.valueOf(taskExecutor.getQueueCapacity()));
		tfStart.setText(taskExecutor.getStartTimeFormatted());
		tfProcessingStart.setText(taskExecutor.getProcessingStartTimeFormatted());
		tfElapsedTime.setText(taskExecutor.getElapsedTimeFormatted());
		tfEstimatedEndTime.setText(taskExecutor.getEstimatedEndTimeFormatted());
		tfEndTime.setText(taskExecutor.getEndTimeFormatted());
		tfAverageSpeed.setText(taskExecutor.getAverageSpeedFormatted(timeUnit));
		tfAverageTaskTime.setText(taskExecutor.getAverageTaskTimeFormatted(timeUnit));
		tfInstantSpeed.setText(taskExecutor.getInstantSpeedFormatted(timeUnit));
		tfLastTaskTime.setText(taskExecutor.getLastTaskTimeFormatted(timeUnit));
		if (log.isDebugEnabled()){
			log.debug("ThreadCount [" + taskExecutor.getThreadCount() + "]");
			log.debug("InstantSpeed [" + taskExecutor.getInstantSpeed(TimeUnit1Ponto6.SECONDS) + "]");
		}
		threadsCountVsSpeedGraphicPanel.add(taskExecutor.getThreadCount(), 
				taskExecutor.getInstantSpeed(TimeUnit1Ponto6.SECONDS));
	}
	
	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
	

	public static JFrame getJFrame(TaskExecutor te) {
		JFrame f = new JFrame();
		f.getContentPane().add(new BasicControlPanel(te));
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		return f;
	}
	
	public static void main(String[] args) throws SQLException, IOException {
//		try {
//			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		} catch (Exception e) {
//			
//		}
		DefaultTaskExecutor t = new DefaultTaskExecutor(new DumbFactory());
		JFrame f = getJFrame(t);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		t.start();
	}
}
