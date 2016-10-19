import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import static java.util.Collections.*;
 
@SuppressWarnings("serial")
public class NumberGame extends JFrame {

    private JButton replay, Quit;
    private JButton[] numberButtons = new JButton[16];
    private int counter = 0;
    private int correctCounter = 0;
    private int[] buttonID = new int[2];
    private int[] number = new int[2];
    private ArrayList<Integer> list = new ArrayList<Integer>();
    private JPanel NumberGameBoard = new JPanel();

    public NumberGame() {
    	setUpActionListners();
    	setUpGamePanel();
        setButtonPairs();
        setTitle("Number Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        setResizable(false);
        setVisible(true);
    }
 
    public void setUpGamePanel() {
        NumberGameBoard.setLayout(new GridLayout(4, 4));
        for (int i = 0; i < numberButtons.length; i++) {
            NumberGameBoard.add(numberButtons[i]);
        }
        Panel buttonPanel = new Panel();
        buttonPanel.add(replay);
        buttonPanel.add(Quit);
        buttonPanel.setLayout(new GridLayout(1, 0));
        add(NumberGameBoard, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
    }
    
    public void setUpActionListners() {
        Quit = new JButton("Quit");
        Quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
        
        replay = new JButton("Replay");
        replay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 dispose();
		         new NumberGame();
			}
		});
        
		for (int i = 0; i < numberButtons.length; i++) {
			numberButtons[i] = new JButton();
			int buttonNumber = i;
			numberButtons[i].setFont(new Font("SansSerif", Font.BOLD, 28));
			numberButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JButton button = (JButton) e.getSource();
					button.setText(list.get(buttonNumber) + "");
					button.setEnabled(false);
					counter++;
					
					if (counter == 3) {
						if (CompareValue()) {
							numberButtons[buttonID[0]].setEnabled(false);
							numberButtons[buttonID[1]].setEnabled(false);
							System.out.println(correctCounter);
						} else {
							numberButtons[buttonID[0]].setEnabled(true);
							numberButtons[buttonID[0]].setText("");
							numberButtons[buttonID[1]].setEnabled(true);
							numberButtons[buttonID[1]].setText("");
						}
						counter = 1;
					}
					if (counter == 1) {
						buttonID[0] = buttonNumber;
						number[0] = list.get(buttonNumber);
					} 

					if(counter == 2) {
						buttonID[1] = buttonNumber;
						number[1] = list.get(buttonNumber);
					}
				}
			});
		}
	}
 
    public void setButtonPairs() {
        for (int i = 0; i < 2; i++) {
            for (int j = 1; j < (numberButtons.length / 2) + 1; j++) {
                list.add(j);
            }
        }
        shuffle(list);
    }
 
    public boolean CompareValue() {
        if (number[0] == number[1]) {
        	correctCounter++;
/*        	if(correctCounter == (numberButtons.length/2)){
				JOptionPane.showMessageDialog(null, "Congratulations! you have finished the game.\nClick Replay to start a new Game.\nClick Quit to return to the spelling quiz.");	
			}*/
            return true;
        }
        return false;
    }
 
    public static void main(String[] args) {
        new NumberGame();
    }
}
