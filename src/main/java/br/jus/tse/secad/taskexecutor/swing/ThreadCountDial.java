package br.jus.tse.secad.taskexecutor.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Point;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.ui.GradientPaintTransformType;
import org.jfree.chart.ui.StandardGradientPaintTransformer;
import org.jfree.data.general.DefaultValueDataset;


public class ThreadCountDial extends JPanel implements ChangeListener, Runnable {

	/** A slider to update the dataset value. */
	private JSlider slider;
	
	/** The dataset. */
	DefaultValueDataset dataset1;
	
	DefaultValueDataset dataset2;
	
	private Thread threadUpdater;
	
	private int targetValue;
	
	private int currentValue;

	public ThreadCountDial() {
		this.dataset1 = new DefaultValueDataset();
		this.dataset2 = new DefaultValueDataset(0);

		// get data for diagrams
		DialPlot plot = new DialPlot();
		plot.setView(0.0, 0.0, 1.0, 1.0);
		plot.setDataset(0, this.dataset1);
		plot.setDataset(1, this .dataset2);
		StandardDialFrame dialFrame = new StandardDialFrame();
		dialFrame.setBackgroundPaint(Color.lightGray);
		dialFrame.setForegroundPaint(Color.darkGray);
		plot.setDialFrame(dialFrame);

		GradientPaint gp = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(),
				new Color(170, 170, 220));
		DialBackground db = new DialBackground(gp);
		db.setGradientPaintTransformer(new StandardGradientPaintTransformer(
				GradientPaintTransformType.VERTICAL));
		plot.setBackground(db);

		DialValueIndicator dvi = new DialValueIndicator(0);
		dvi.setNumberFormat(new DecimalFormat("0"));
		dvi.setOutlinePaint(Color.BLUE);
		dvi.setRadius(0.60);
		dvi.setAngle(-103.0);
		plot.addLayer(dvi);
		
		DialValueIndicator dvi2 = new DialValueIndicator(1);
		dvi2.setNumberFormat(new DecimalFormat("0"));
		dvi2.setOutlinePaint(Color.RED);
		dvi2.setRadius(0.60);
		dvi2.setAngle(-77.0);
		plot.addLayer(dvi2);

		StandardDialScale scale = new StandardDialScale(0, 100, -120, -300, 10, 9);
		scale.setTickLabelFormatter(new DecimalFormat("0"));
		scale.setTickRadius(0.88);
		scale.setTickLabelOffset(0.15);
		scale.setTickLabelFont(new Font("Dialog", Font.BOLD, 14));
		plot.addScale(0, scale);

		StandardDialRange range = new StandardDialRange(70, 100, Color.RED);
		range.setInnerRadius(0.52);
		range.setOuterRadius(0.55);
		plot.addLayer(range);

		StandardDialRange range2 = new StandardDialRange(40, 70, Color.ORANGE);
		range2.setInnerRadius(0.52);
		range2.setOuterRadius(0.55);
		plot.addLayer(range2);

		StandardDialRange range3 = new StandardDialRange(0, 40, Color.GREEN);
		range3.setInnerRadius(0.52);
		range3.setOuterRadius(0.55);
		plot.addLayer(range3);

		DialPointer needle = new DialPointer.Pointer(0);
		plot.addLayer(needle);
		
		DialPointer needle2 = new DialPointer.Pin(1);
		plot.addLayer(needle2);
		needle2.setRadius(0.60);

		DialCap cap = new DialCap();
		cap.setRadius(0.10);
		plot.setCap(cap);

		JFreeChart chart1 = new JFreeChart(plot);
		// chart1.setTitle("Thread Count");
		ChartPanel cp1 = new ChartPanel(chart1);
		cp1.setPreferredSize(new Dimension(200, 200));
		this.slider = new JSlider(0, 100, 0);
		this.slider.setMajorTickSpacing(10);
		this.slider.setPaintLabels(true);
		this.slider.addChangeListener(this);
		this.dataset1.setValue(this.slider.getValue());
		JPanel content = new JPanel(new BorderLayout());
		content.add(cp1);
		content.add(this.slider, BorderLayout.SOUTH);
		add(content);
		threadUpdater = new Thread(this, "ThreadCountDial-Updater");
		threadUpdater.setDaemon(true);
		threadUpdater.start();
	}
	
	public void run(){
		while (true){
			try {
				double auxTargetValue = this.dataset1.getValue().doubleValue();
				this.dataset1.setValue((targetValue + auxTargetValue) / 2);
				
				double auxCurrentValue = this.dataset2.getValue().doubleValue();
				if (Math.abs(currentValue - auxCurrentValue) <= 5)
					this.dataset2.setValue((currentValue + auxCurrentValue) / 2);
				else
					this.dataset2.setValue(currentValue > auxCurrentValue?auxCurrentValue + 1: auxCurrentValue - 1);
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public JSlider getSlider() {
		return slider;
	}

	/**
	 * Handle a change in the slider by updating the dataset value. This automatically triggers a
	 * chart repaint.
	 */
	public void stateChanged(ChangeEvent e) {
		this.targetValue = this.slider.getValue();
	}
	
	public void setCurrentValue(int currentValue){
		this.currentValue = currentValue;
	}

	public static void main(String[] args) {
		ThreadCountDial panel = new ThreadCountDial();
		JFrame app = new JFrame();
		app.setContentPane(panel);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.pack();
		app.setVisible(true);
	}

}
