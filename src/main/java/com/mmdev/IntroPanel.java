package com.mmdev;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.YELLOW;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.SwingConstants.CENTER;

public class IntroPanel extends JFrame {

	static final int WIDTH = 600;
	static final int HEIGHT = 600;
	static final int INPUT_FIELD_WIDTH = 200;
	static final int INPUT_FIELD_HEIGHT = 25;
	static final int BUTTON_WIDTH = 100;
	static final int BUTTON_HEIGHT = 30;
	static final int MAX_NUMBER = 1000;
	static final int MIN_NUMBER = 1;
	static final int MAX_PER_COLUMN = 10;
	static final int GAP = 10;
	static final int TOP_MARGIN = 10;
	static final int BOTTOM_MARGIN = 10;
	static final int RIGHT_MARGIN = 10;
	static final int LEFT_MARGIN = 10;
	static final int COLUMNS = 10;
	static boolean ascending = true;

	private JTextField numberInputField;
	private JLabel instructionLabel;
	private JPanel inputPanel;
	private JPanel numbersPanel;
	private List<JButton> numberButtons;
	private List<JPanel> numberPanels;
	private List<Integer> numbers;
	private SortButtonListener sortButtonListener;

	public IntroPanel() {
		setTitle("Intro");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new CardLayout());

		inputPanel = createInputPanel();
		add(inputPanel, "InputPanel");

		numbersPanel = new JPanel();
		numbersPanel.setLayout(new BorderLayout());
		add(numbersPanel, "NumbersPanel");

		numberButtons = new ArrayList<>();
		numberPanels = new ArrayList<>();

		setVisible(true);
	}

	private JPanel createInputPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(GAP, GAP, GAP, GAP);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;

		instructionLabel = new JLabel("How many numbers to display?", CENTER);
		panel.add(instructionLabel, gbc);

		gbc.gridy++;
		numberInputField = new JTextField(COLUMNS);
		numberInputField.setMaximumSize(new Dimension(INPUT_FIELD_WIDTH, INPUT_FIELD_HEIGHT));
		panel.add(numberInputField, gbc);

		gbc.gridy++;
		JButton enterButton = new JButton("Enter");
		enterButton.addActionListener(new EnterButtonListener());
		panel.add(enterButton, gbc);

		return panel;
	}

	private class EnterButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String input = numberInputField.getText();
			try {
				int number = Integer.parseInt(input);

				if (number < MIN_NUMBER || number > MAX_NUMBER) {
					JOptionPane.showMessageDialog(IntroPanel.this, "Please enter a valid number between 1 and 1000.");
				} else {
					CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
					cardLayout.show(getContentPane(), "NumbersPanel");

					JPanel displayPanel = new JPanel();
					displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.X_AXIS));

					JScrollPane scrollPane = new JScrollPane(displayPanel);
					scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
					scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
					numbersPanel.add(scrollPane, BorderLayout.CENTER);

					JPanel sortPanel = new JPanel();
					sortPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

					JButton sortButton = new JButton("Sort");
					sortButtonListener = new SortButtonListener(displayPanel);
					sortButton.addActionListener(sortButtonListener);
					sortPanel.add(sortButton);

					JButton resetButton = new JButton("Reset");
					resetButton.addActionListener(new ResetButtonListener());
					sortPanel.add(resetButton);

					sortPanel.setBorder(BorderFactory.createEmptyBorder(TOP_MARGIN, LEFT_MARGIN, BOTTOM_MARGIN, RIGHT_MARGIN));
					numbersPanel.add(sortPanel, BorderLayout.SOUTH);

					numbers = generateRandomNumbers(number);
					updateNumberPanel(displayPanel, numbers);

					sortButtonListener.setNumbers(numbers);

					numbersPanel.revalidate();
					numbersPanel.repaint();
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(IntroPanel.this, "Please enter a valid integer.");
			}
		}
	}

	private class SortButtonListener implements ActionListener {
		private final JPanel numbersPanel;
		private List<Integer> numbers;
		private Timer timer;
		private final int delay = 500;

		public SortButtonListener(JPanel numbersPanel) {
			this.numbersPanel = numbersPanel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (numbers == null || numbers.isEmpty()) {
				return;
			}

			ascending = !ascending;

			if (timer != null && timer.isRunning()) {
				timer.stop();
			}

			timer = new Timer(delay, new ActionListener() {
				private int i = 0;
				private int j = 0;

				@Override
				public void actionPerformed(ActionEvent evt) {
					if (i < numbers.size() - 1) {
						if (j < numbers.size() - i - 1) {
							if ((ascending && numbers.get(j) > numbers.get(j + 1)) || (!ascending && numbers.get(j) < numbers.get(j + 1))) {

								highlightButton(j, YELLOW);
								highlightButton(j + 1, YELLOW);

								Collections.swap(numbers, j, j + 1);
								updateNumberPanel(numbersPanel, numbers);

								SwingUtilities.invokeLater(() -> {
									highlightButton(j, BLUE);
									highlightButton(j + 1, BLUE);
								});
							}
							j++;
						} else {
							j = 0;
							i++;
						}
					} else {
						SwingUtilities.invokeLater(() -> {
							highlightAllButtons(GREEN);
						});
						timer.stop();
					}
				}
			});
			timer.start();
		}

		public void setNumbers(List<Integer> numbers) {
			this.numbers = numbers;
		}

		private void highlightButton(int index, Color color) {
			SwingUtilities.invokeLater(() -> {
				if (index < numberButtons.size()) {
					numberButtons.get(index).setBackground(color);
				}
				numbersPanel.revalidate();
				numbersPanel.repaint();
			});
		}

		private void highlightAllButtons(Color color) {
			SwingUtilities.invokeLater(() -> {
				for (JButton button : numberButtons) {
					button.setBackground(color);
				}
				numbersPanel.revalidate();
				numbersPanel.repaint();
			});
		}
	}

	private class ResetButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
			cardLayout.show(getContentPane(), "InputPanel");

			numbersPanel.removeAll();
		}
	}

	private void updateNumberPanel(JPanel displayPanel, List<Integer> numbers) {
		displayPanel.removeAll();

		numberButtons.clear();
		numberPanels.clear();

		int columnCount = (int) Math.ceil((double) numbers.size() / MAX_PER_COLUMN);

		for (int i = 0; i < columnCount; i++) {
			JPanel columnPanel = new JPanel();
			columnPanel.setLayout(new BoxLayout(columnPanel, Y_AXIS));
			numberPanels.add(columnPanel);

			for (int j = i * MAX_PER_COLUMN; j < (i + 1) * MAX_PER_COLUMN && j < numbers.size(); j++) {
				JButton numberButton = new JButton(String.valueOf(numbers.get(j)));
				numberButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
				numberButton.setAlignmentX(CENTER_ALIGNMENT);
				numberButton.setBackground(BLUE);
				numberButton.addActionListener(new NumberButtonListener());

				columnPanel.add(numberButton);
				numberButtons.add(numberButton);
			}

			displayPanel.add(columnPanel);
		}

		displayPanel.revalidate();
		displayPanel.repaint();
	}

	private class NumberButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
			int value = Integer.parseInt(button.getText());

			if (value <= 30) {
				List<Integer> newNumbers = generateRandomNumbers(value);
				updateNumberPanel((JPanel) ((JScrollPane) numbersPanel.getComponent(0)).getViewport().getView(), newNumbers);
				sortButtonListener.setNumbers(newNumbers);
			} else {
				JOptionPane.showMessageDialog(IntroPanel.this, "Choose a value less than or equal to 30.");
			}
		}
	}

	private List<Integer> generateRandomNumbers(int count) {
		List<Integer> numbers = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < count; i++) {
			numbers.add(random.nextInt(MAX_NUMBER + 1));
		}

		boolean hasNumberLessThanOrEqualTo30 = numbers.stream().anyMatch(num -> num <= 30);
		if (!hasNumberLessThanOrEqualTo30) {

			int indexToReplace = random.nextInt(count);
			numbers.set(indexToReplace, random.nextInt(31));
		}

		Collections.shuffle(numbers);
		return numbers;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(IntroPanel::new);
	}
}
