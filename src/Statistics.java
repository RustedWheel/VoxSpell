import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Statistics {

	private JFrame frame;
	private JButton close = new JButton("Main Menu");
	private JButton statDetail = new JButton("Graphical feedback");
	private Spelling_Aid _spelling_Aid;
	private JTable table;
	private static DecimalFormat df = new DecimalFormat("#.#");
	private final static String[] columns = { "Level", "Passed", "Failed", "Average Score", "Total Attempts" };
	private HashMap<Integer, Integer[]> stats = new HashMap<Integer, Integer[]>();
	private HashMap<Integer, ArrayList<Integer>> scores = new HashMap<Integer, ArrayList<Integer>>();
	private ChartPanel chartPanel = null;

	public Statistics(Spelling_Aid spelling_Aid) {
		_spelling_Aid = spelling_Aid;
		statDetail.setEnabled(false);

		statDetail.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int row = table.getSelectedRow();
				int level = (int) table.getValueAt(row, 0);

				Integer[] levelStats = stats.get(level);
				ArrayList<Integer> levelScores = scores.get(level);

				graphFeedback feedback = new graphFeedback(levelStats, levelScores);
				feedback.setVisible(true);

			}

		});

	}

	/**
	 * This method displays the user's statistics in a JFrame with a JTable
	 * storing the data It is reused code from A2
	 */
	public void showStats() {
		calculateStats();

		if (table != null) {

			frame = new JFrame("Statistics");
			frame.setSize(860, 400);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			// Adds a listener to display the main menu once the statistics is
			// closed
			frame.addWindowListener(new WindowListener() {

				@Override
				public void windowOpened(WindowEvent e) {
				}

				@Override
				public void windowClosing(WindowEvent e) {
				}

				@Override
				public void windowClosed(WindowEvent e) {
					_spelling_Aid.setVisible(true);
				}

				@Override
				public void windowIconified(WindowEvent e) {
				}

				@Override
				public void windowDeiconified(WindowEvent e) {
				}

				@Override
				public void windowActivated(WindowEvent e) {
				}

				@Override
				public void windowDeactivated(WindowEvent e) {
				}

			});

			// Disposes the JFrame and unhides the main menu once the "Main
			// Menu" button is pressed
			close.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
					_spelling_Aid.setVisible(true);
				}

			});

			JPanel options = new JPanel();
			options.setBackground(new Color(214, 217, 223));
			options.setLayout(new FlowLayout(FlowLayout.LEFT));
			options.add(close);
			options.add(statDetail);

			// Adds the JTable to a JScrollPane to allow for scrolling and for
			// headers to show up
			JScrollPane scroll = new JScrollPane(table);
			scroll.setBackground(new Color(214, 217, 223));
			frame.add(chartPanel, BorderLayout.EAST);
			frame.add(scroll, BorderLayout.WEST);

			// Finally displays the JFrame containing the statistics
			frame.add(options, BorderLayout.SOUTH);
			frame.setResizable(false);
			frame.setBackground(new Color(214, 217, 223));
			frame.setVisible(true);

		}
	}

	/*
	 * Modified A2 code
	 */
	private void calculateStats() {
		// Reads the current results
		ArrayList<String> results = _spelling_Aid.readList(new File(".results"));

		// Displays an error message if there are no statistics to be shown
		if (results.size() == 0) {
			JOptionPane.showMessageDialog(new JFrame(), "Error, no results saved", "Error", JOptionPane.ERROR_MESSAGE);
			_spelling_Aid.setVisible(true);
		} else {
			// Stores the results for every level as a HashMap with a 3 element
			// array representing passed, failed and total score
			/*
			 * HashMap<Integer, Integer[]> stats = new HashMap<Integer,
			 * Integer[]>();
			 */
			ArrayList<Integer> levels = new ArrayList<Integer>();

			for (String result : results) {
				String[] split = result.split(" ");
				int levelKey = Integer.parseInt(split[0].substring(5));
				// If the HashMap does not contain the current level, add it
				// along with a [0,0,0] array
				if (!stats.containsKey(levelKey)) {
					levels.add(levelKey);
					Integer[] blank = new Integer[3];

					for (int i = 0; i < 3; i++) {
						blank[i] = 0;
					}

					stats.put(levelKey, blank);
				}

				if (!scores.containsKey(levelKey)) {
					ArrayList<Integer> scorelist = new ArrayList<Integer>();
					scores.put(levelKey, scorelist);
				}

				int score = Integer.parseInt(split[1]);

				scores.get(levelKey).add(score);

				if (score >= 9) {
					stats.get(levelKey)[0]++;
				} else {
					stats.get(levelKey)[1]++;
				}

				stats.get(levelKey)[2] = stats.get(levelKey)[2] + score;

			}

			// Sorts the levels in ascending order
			Collections.sort(levels);

			Object[][] data = new Object[levels.size()][5];

			int row = 0;

			// Creates the data array used for the JTable
			for (Integer level : levels) {

				Integer[] subtotals = stats.get(level);
				int total = subtotals[0] + subtotals[1];

				data[row][0] = level;
				data[row][1] = subtotals[0];
				data[row][2] = subtotals[1];

				if (total > 0) {
					data[row][3] = df.format((double) subtotals[2] / total);
				} else {
					data[row][3] = 0;
				}

				data[row][4] = total;

				row++;
			}

			// Makes the JTable and disallows editing and resizing
			table = new JTable(new Model(data, columns));
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setResizingAllowed(false);
			table.getColumnModel().getColumn(0).setPreferredWidth(40);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setRowSelectionAllowed(true);
			table.getColumnModel().getColumn(3).setPreferredWidth(100);
			table.getColumnModel().getColumn(4).setPreferredWidth(100);

			table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent event) {
					statDetail.setEnabled(true);
				}
			});

			DefaultCategoryDataset dataset = NewDataset(data);
			JFreeChart chart = NewChart(dataset);
			chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new java.awt.Dimension(400, 270));
			chartPanel.setBorder(new MatteBorder(1,1,1,1, (Color) new Color(83, 104, 120, 255)));
		}
	}

	private JFreeChart NewChart(DefaultCategoryDataset dataset) {
		final JFreeChart chart = ChartFactory.createBarChart(
				"Average score for each \n attempted level", 
				"Level", 
				"Score",
				dataset, 
				PlotOrientation.VERTICAL, 
				true, 
				true, 
				false);
		chart.setBackgroundPaint(new Color(240, 240, 240, 255));
		/*chart.setBackgroundPaint(new Color(214, 217, 223, 255));*/
		chart.getTitle().setFont(new Font("SansSerif", Font.ITALIC, 20));
		chart.getTitle().setPaint(new Color(51, 47, 47));
		CategoryPlot plot = chart.getCategoryPlot();
		BarRenderer bar = (BarRenderer) plot.getRenderer();
		Paint paint = new Color(83, 104, 120, 255);
		Paint background = new Color(248, 248, 255, 255);
		Paint grid = new Color(112, 128, 144, 255);
		bar.setSeriesPaint(0, paint);
		plot.setBackgroundPaint(background);
		plot.setRangeGridlinePaint(grid);
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setRange(0.0,10.0);
		yAxis.setTickUnit(new NumberTickUnit(1.0));

		return chart;
	}

	private DefaultCategoryDataset NewDataset(Object[][] data) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < data.length; i++) {
			dataset.setValue(Double.parseDouble((String) data[i][3]), "Score", Integer.toString((int) data[i][0]));
		}

		return dataset;
	}

	public class Model extends DefaultTableModel {

		Model(Object[][] data, String[] column) {
			super(data, column);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

}
