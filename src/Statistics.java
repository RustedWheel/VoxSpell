import java.awt.BorderLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Statistics{

	private JFrame frame;
	private JButton close = new JButton("Main Menu");
	private Spelling_Aid _spelling_Aid;
	private JTable table;
	private static DecimalFormat df = new DecimalFormat("#.#");
	private final static String[] columns = {"Level", "Passed", "Failed", "Average Score", "Total Attempts"};

	public Statistics(Spelling_Aid spelling_Aid) {
		_spelling_Aid = spelling_Aid;
	}

	/**
	 * This method displays the user's statistics in a JFrame with a JTable storing the data
	 * It is reused code from A2
	 */
	public void showStats() {
		calculateStats();

		if (table != null) {

			frame = new JFrame("Statistics");
			frame.setSize(500,400);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			// Adds a listener to display the main menu once the statistics is closed
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

			// Adds the JTable to a JScrollPane to allow for scrolling and for headers to show up
			JScrollPane scroll = new JScrollPane(table);
			frame.add(scroll,BorderLayout.CENTER);

			// Disposes the JFrame and unhides the main menu once the "Main Menu" button is pressed
			close.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
					_spelling_Aid.setVisible(true);
				}

			});

			// Finally displays the JFrame containing the statistics
			frame.add(close,BorderLayout.SOUTH);
			frame.setResizable(false);
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
			JOptionPane.showMessageDialog(new JFrame(), "Error, no results saved", "Error",
					JOptionPane.ERROR_MESSAGE);
			_spelling_Aid.setVisible(true);
		} else {
			// Stores the results for every level as a HashMap with a 3 element array representing passed, failed and total score
			HashMap<Integer, Integer[]> stats = new HashMap<Integer, Integer[]>();
			ArrayList<Integer> levels = new ArrayList<Integer>();

			for (String result : results) {
				String[] split = result.split(" ");
					int levelKey = Integer.parseInt(split[0].substring(5));
					// If the HashMap does not contain the current level, add it along with a [0,0,0] array
					if (!stats.containsKey(levelKey)) {
						levels.add(levelKey);
						Integer[] blank = new Integer[3];

						for (int i = 0; i < 3; i++) {
							blank[i] = 0;
						}

						stats.put(levelKey, blank);
					}
					
					int score = Integer.parseInt(split[1]);

					if(score >= 9){
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
				
				if(total > 0){
					data[row][3] = df.format((double) subtotals[2] / total);
				} else {
					data[row][3] = 0;
				}
				
				data[row][4] = total;

				row++;
			}

			// Makes the JTable and disallows editing and resizing
			table = new JTable(data, columns);
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setResizingAllowed(false);
			table.getColumnModel().getColumn(0).setPreferredWidth(40);
			table.setEnabled(false);
		}
	}
}
