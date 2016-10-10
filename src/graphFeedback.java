import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

@SuppressWarnings("serial")
public class graphFeedback extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	}

	/**
	 * Create the dialog.
	 */
	public graphFeedback(Integer[] levelStats, ArrayList<Integer> levelScores) {
		setTitle("Graphical feedback");
		setBounds(100, 100, 900, 660);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JPanel sidePanel = new JPanel(new BorderLayout());
		sidePanel.setBackground(new Color(240, 240, 240, 255));
		
		//Creates the line graph
		DefaultCategoryDataset lineData = newLineDataset(levelScores);
		JFreeChart scoreAttemptChart = LineChart(lineData);
		ChartPanel chartPanel = new ChartPanel(scoreAttemptChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 300));
		contentPanel.add(chartPanel,BorderLayout.EAST);
		
		//Creates the pie chart
		PieDataset pieData = newPieDataset(levelStats);
		JFreeChart pieChart = PieChart(pieData);
		ChartPanel pieChartPanel = new ChartPanel(pieChart);
		pieChartPanel.setPreferredSize(new java.awt.Dimension(380, 300));
		sidePanel.add(pieChartPanel,BorderLayout.NORTH);
		
		//Creates the bar chart
		DefaultCategoryDataset barDataset = NewBarDataset(levelScores);
		JFreeChart barChart = NewBarChart(barDataset);
		ChartPanel barChartPanel = new ChartPanel(barChart);
		barChartPanel.setPreferredSize(new java.awt.Dimension(380, 300));
		sidePanel.add(barChartPanel,BorderLayout.SOUTH);
		
		contentPanel.add(sidePanel,BorderLayout.WEST);
		contentPanel.setBackground(new Color(240, 240, 240, 255));
		contentPanel.setBorder(new MatteBorder(1,1,1,1, (Color) new Color(83, 104, 120, 255)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			//Add a new back button
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout());
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton backButton = new JButton("Back");
				backButton.setActionCommand("Back");
				buttonPane.add(backButton);
				getRootPane().setDefaultButton(backButton);
				backButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();	
					}

				});

			}
			
		}
	}
	
	private DefaultCategoryDataset newLineDataset(ArrayList<Integer> data){
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for(int i = 0; i < data.size(); i++){
			dataset.addValue(data.get(i), "Score", Integer.toString(i+1));
		}
		
		return dataset;
		
	}
	
	private JFreeChart LineChart(DefaultCategoryDataset dataset){
		//Creates the line graph and sets its axis and colours
		JFreeChart lineChart = ChartFactory.createLineChart(
				"Score Vs Attempt", 
				"Number of Attempt",
				"Score",
				dataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false);
		lineChart.getTitle().setFont(new Font("SansSerif", Font.ITALIC, 20));
		lineChart.getTitle().setPaint(new Color(51, 47, 47));
		lineChart.setBackgroundPaint(new Color(240, 240, 240, 255));
		CategoryPlot plot = lineChart.getCategoryPlot();
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		plot.setBackgroundPaint(new Color(248, 248, 255, 255));
		plot.setRangeGridlinePaint(new Color(112, 128, 144, 255));
		renderer.setBaseShapesVisible(true);
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setRange(0.0,10.0);
		yAxis.setTickUnit(new NumberTickUnit(1.0));
		
		
		return lineChart;
	}
	
	private PieDataset newPieDataset(Integer[] data){
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Mastered", new Double(data[0]));
		dataset.setValue("Failed", new Double(data[1]));
		return dataset;
		
	}
	
	private JFreeChart PieChart(PieDataset dataset){
		//Creates the pie chart and sets its axis and colours
		JFreeChart pieChart = ChartFactory.createPieChart(
				"Percentage of times passed Vs percentage of times \n fail the level", 
				dataset,
				true,
				true,
				false);
		pieChart.getTitle().setFont(new Font("SansSerif", Font.ITALIC, 20));
		pieChart.getTitle().setPaint(new Color(51, 47, 47));
		pieChart.setBackgroundPaint(new Color(240, 240, 240, 255));
		PiePlot plot = (PiePlot) pieChart.getPlot();
		plot.setBackgroundPaint(new Color(248, 248, 255, 255));
		plot.setSectionPaint("Mastered",new Color(83, 104, 120, 255));
		plot.setSectionPaint("Failed",new Color(255, 84, 83, 255));
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
				));
		
		return pieChart;
	} 
	
	private DefaultCategoryDataset NewBarDataset(ArrayList<Integer> data) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for(int i = 0; i < data.size(); i++){
			
			dataset.addValue(data.get(i), "mastered", Integer.toString(i+1));
			dataset.addValue(10 - data.get(i), "failed", Integer.toString(i+1));
			
		}

		return dataset;
	}
	
	private JFreeChart NewBarChart(DefaultCategoryDataset dataset) {
		//Creates the bar chart and sets its axis and colours
		final JFreeChart chart = ChartFactory.createBarChart(
				"Number of mastered and failed words for each attempt", 
				"Number of attempt", 
				"Number of words",
				dataset, 
				PlotOrientation.VERTICAL, 
				true, 
				true, 
				false);
		chart.getTitle().setFont(new Font("SansSerif", Font.ITALIC, 20));
		chart.getTitle().setPaint(new Color(51, 47, 47));
		CategoryPlot plot = chart.getCategoryPlot();
		BarRenderer bar = (BarRenderer) plot.getRenderer();
		Paint mastered = new Color(83, 104, 120, 255);
		Paint failed = new Color(255, 84, 83, 255);
		Paint background = new Color(248, 248, 255, 255);
		Paint grid = new Color(112, 128, 144, 255);
		bar.setSeriesPaint(0, mastered);
		bar.setSeriesPaint(1, failed);
		plot.setBackgroundPaint(background);
		plot.setRangeGridlinePaint(grid);
		chart.setBackgroundPaint(new Color(240, 240, 240, 255));
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setRange(0.0,10.0);
		yAxis.setTickUnit(new NumberTickUnit(1.0));

		return chart;
	}

}
