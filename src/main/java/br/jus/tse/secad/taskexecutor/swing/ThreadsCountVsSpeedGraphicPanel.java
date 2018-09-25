package br.jus.tse.secad.taskexecutor.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RefineryUtilities;

public class ThreadsCountVsSpeedGraphicPanel extends JPanel {

	private TimeSeries threadCountTimeSeries;
	private TimeSeries speedTimeSeries;
	private ChartPanel chartPanel;
	private JFreeChart chart;

	public ThreadsCountVsSpeedGraphicPanel() {
		XYDataset datasetThreads = createDatasetThreads();
		XYDataset datasetSpeed = createDatasetSpeed();

		chart = createChart(datasetThreads, datasetSpeed);
		chartPanel = new ChartPanel(chart);
		chartPanel.setMouseZoomable(true, false);
		setLayout(new BorderLayout());
		add(chartPanel, BorderLayout.CENTER);
	}

	public void add(int instantThreadCount, double instantSpeed) {
		Second s = new Second();
		threadCountTimeSeries.addOrUpdate(s, instantThreadCount);
		speedTimeSeries.addOrUpdate(s, instantSpeed);
	}

	private XYDataset createDatasetThreads() {
		threadCountTimeSeries = new TimeSeries("Threads Count");
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(threadCountTimeSeries);
		return dataset;
	}

	private XYDataset createDatasetSpeed() {
		speedTimeSeries = new TimeSeries("Tasks/Sec");
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(speedTimeSeries);
		return dataset;
	}

	private JFreeChart createChart(XYDataset threadsDataset, XYDataset speedDataset) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(null, null, "Threads Count",
				threadsDataset, true, true, false);
		chart.setBackgroundPaint(null);

		XYPlot plot = chart.getXYPlot();
		// Primeiro Eixo - default.
//		Color c = Color.BLUE;
//		plot.getRangeAxis(0).setAxisLinePaint(c);
//		plot.getRangeAxis(0).setLabelPaint(c);
//		plot.getRangeAxis(0).setTickLabelPaint(c);
//		plot.getRenderer(0).setSeriesPaint(0, c);
//		plot.
		// Segundo Eixo.
		//c = new Color(100, 50, 200);
		NumberAxis axisSpeed = new NumberAxis("Speed");
		axisSpeed.setAutoRangeIncludesZero(false);
//		axisSpeed.setAxisLinePaint(c);
//		axisSpeed.setLabelPaint(c);
//		axisSpeed.setTickLabelPaint(c);
		plot.setRangeAxis(1, axisSpeed);
		plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
		plot.setDataset(1, speedDataset);
		plot.mapDatasetToRangeAxis(1, 1);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		plot.setRenderer(1, renderer);
		//plot.getRenderer(1).setSeriesPaint(0, c);
		
		return chart;
	}

	public static void main(String[] args) {
		JFrame demo = new JFrame();
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final ThreadsCountVsSpeedGraphicPanel panel = new ThreadsCountVsSpeedGraphicPanel();
		demo.getContentPane().add(panel);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
		new Thread() {
			public void run() {
				while (true) {
					panel.add((int) (Math.random() * 100), Math.random() * 10);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

}