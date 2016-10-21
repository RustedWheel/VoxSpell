import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/*import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;*/
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import utility.FileContentReader;

@SuppressWarnings("serial")
public class Statistics extends JPanel {
	
	private Gui _frame;
	private JButton close, statDetail;
	private JTable table;
	private double highest = 0;
	private static DecimalFormat df = new DecimalFormat("#.#");
	private final static String[] columns = { "Level", "Passed", "Failed", "Average Score", "Total Attempts", "Highest Score" };
	private HashMap<Integer, Integer[]> stats = new HashMap<Integer, Integer[]>();
	private HashMap<Integer, ArrayList<Integer>> scores = new HashMap<Integer, ArrayList<Integer>>();
	private ChartPanel chartPanel = null;
	private FileContentReader reader = new FileContentReader();
	private String _spellingList;

	public Statistics(Gui frame, String spellingList) {
		_frame = frame;
		_spellingList = spellingList;
		setLayout(new BorderLayout());
	}

	/**
	 * This method displays the user's statistics in a JFrame with a JTable
	 * storing the data It is reused code from A2
	 */
	public void showStats() {
		calculateStats();

		if (table != null) {
			setUpButtons();
			JPanel options = new JPanel();
			options.setBackground(new Color(220,221,225));
			options.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel highestScore = new JLabel();
			JLabel spellingList = new JLabel(" " + " Spelling list: " +  _spellingList);
			Font font = highestScore.getFont();
			Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
			highestScore.setFont(boldFont);
			spellingList.setFont(boldFont);
			highestScore.setText(" Personal highest score: " + df.format(highest) + "	");
			options.add(close);
			options.add(statDetail);
			options.add(highestScore);
			options.add(spellingList);

			// Adds the JTable to a JScrollPane to allow for scrolling and for
			// headers to show up
			JScrollPane scroll = new JScrollPane(table);
			scroll.setBackground(new Color(214, 217, 223));
			scroll.setPreferredSize(new java.awt.Dimension(500, 270));
			add(chartPanel, BorderLayout.EAST);
			add(scroll, BorderLayout.WEST);

			// Finally displays the JFrame containing the statistics
			add(options, BorderLayout.SOUTH);
			setBackground(new Color(214, 217, 223));
			setVisible(true);

		}
	}

	/*
	 * Modified A2 code
	 */
	private void calculateStats() {
		// Reads the current results
		FileContentReader reader = new FileContentReader();
		ArrayList<String> results = reader.readList(new File(".results"));

		// Displays an error message if there are no statistics to be shown
		if (results.size() == 0) {
			JOptionPane.showMessageDialog(new JFrame(), "Error, no results saved", "Error", JOptionPane.ERROR_MESSAGE);			
		} else {
			// array representing passed, failed and total score and highest score
			ArrayList<Integer> levels = new ArrayList<Integer>();

			for (String result : results) {
				String[] split = result.split(" ");
				int levelKey = Integer.parseInt(split[0].substring(5));
				// If the HashMap does not contain the current level, add it
				// along with a [0,0,0,0] array
				if (!stats.containsKey(levelKey)) {
					levels.add(levelKey);
					Integer[] blank = new Integer[4];

					for (int i = 0; i < 4; i++) {
						blank[i] = 0;
					}

					stats.put(levelKey, blank);
				}

				if (!scores.containsKey(levelKey)) {
					ArrayList<Integer> scorelist = new ArrayList<Integer>();
					scores.put(levelKey, scorelist);
				}

				int score = Integer.parseInt(split[1]);

				if(score > highest){
					highest = score;
				}
				
				scores.get(levelKey).add(score);

				if (score >= 9) {
					stats.get(levelKey)[0]++;
				} else {
					stats.get(levelKey)[1]++;
				}

				stats.get(levelKey)[2] = stats.get(levelKey)[2] + score;
				
				if(score > stats.get(levelKey)[3]){
					stats.get(levelKey)[3] = score;
				}
				
			}

			// Sorts the levels in ascending order
			Collections.sort(levels);

			Object[][] data = new Object[levels.size()][6];

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
				data[row][5] = subtotals[3];

				row++;
			}

			// Makes the JTable and resizing
			table = new JTable(new Model(data, columns));
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setResizingAllowed(false);
			table.getColumnModel().getColumn(0).setPreferredWidth(25);
			table.getColumnModel().getColumn(1).setPreferredWidth(40);
			table.getColumnModel().getColumn(2).setPreferredWidth(27);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setRowSelectionAllowed(true);
			table.getColumnModel().getColumn(3).setPreferredWidth(90);
			table.getColumnModel().getColumn(4).setPreferredWidth(90);
			table.getColumnModel().getColumn(5).setPreferredWidth(90);

			table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent event) {
					statDetail.setEnabled(true);
				}
			});

			//Makes the bar graph for all levels attempted
			DefaultCategoryDataset dataset = NewDataset(data);
			JFreeChart chart = NewChart(dataset);
			chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new java.awt.Dimension(400, 270));
			chartPanel.setBorder(new MatteBorder(1,1,1,1, (Color) new Color(83, 104, 120, 255)));
		}
	}

	private JFreeChart NewChart(DefaultCategoryDataset dataset) {
		//Create the chart and set the axis and colours
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
		yAxis.setRange(0.0,10.5);
		yAxis.setTickUnit(new NumberTickUnit(1.0));

		return chart;
	}

	private DefaultCategoryDataset NewDataset(Object[][] data) {
		//Creates the chart data set
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < data.length; i++) {
			dataset.setValue(Double.parseDouble((String) data[i][3]), "Score", Integer.toString((int) data[i][0]));
		}

		return dataset;
	}

	private class Model extends DefaultTableModel {

		//Creates the table model to disallow editing
		Model(Object[][] data, String[] column) {
			super(data, column);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
	
	public boolean isEmpty(){
		boolean isEmpty = false;
		if(table == null){
			isEmpty = true;
		}
		return isEmpty;
	}
	
	private void setUpButtons(){
		close = new JButton("Main Menu",new ImageIcon(Statistics.class.getResource("/img/home.png")));
		statDetail = new JButton("Graphical feedback",new ImageIcon(Statistics.class.getResource("/img/graph.png")));
		statDetail.setEnabled(false);
		statDetail.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int row = table.getSelectedRow();
				int level = (int) table.getValueAt(row, 0);

				Integer[] levelStats = stats.get(level);
				ArrayList<Integer> levelScores = scores.get(level);
				ArrayList<String> failedWords =  reader.readFailed(level);
				graphFeedback feedback = new graphFeedback(levelStats, levelScores,failedWords);
				feedback.setVisible(true);

			}

		});
		
		// Disposes the JFrame and unhides the main menu once the "Main
		// Menu" button is pressed
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_frame.getContentPane().removeAll();
				_frame.setTitle("VoXSpell");
				MainMenu menu = new MainMenu(_frame);
				_frame.setSize(550, 575);
				_frame.getContentPane().add(menu);
				_frame.revalidate();
				_frame.repaint();
			}

		});
		
		
	}

}
