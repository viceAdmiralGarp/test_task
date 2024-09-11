package com.mmdev;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class IntroPanel extends JFrame {

	static final int WIDTH = 800;
	static final int HEIGHT = 600;
	static final int INPUT_FIELD_WIDTH = 200;
	static final int INPUT_FIELD_HEIGHT = 25;
	static final int BUTTON_WIDTH = 100;
	static final int BUTTON_HEIGHT = 30;
	static final int MAX_NUMBER = 1000;
	static final int MIN_NUMBER = 0;
	static final int MAX_PER_COLUMN = 10;
	static final int COLUMNS = 10;
	static final int GAP = 10;
	static final int TOP_MARGIN = 10;
	static final int BOTTOM_MARGIN = 10;
	static final int RIGHT_MARGIN = 10;
	static final int LEFT_MARGIN = 10;
	static boolean ascending = true;

	private final JTextField numberInputField;
	private final JLabel instructionLabel;

	private JFrame numbersFrame;

	public IntroPanel() {
		setTitle("Intro");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		instructionLabel = new JLabel("How many numbers to display?", SwingConstants.CENTER);
		instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		inputPanel.add(instructionLabel);

		numberInputField = new JTextField(COLUMNS);
		numberInputField.setMaximumSize(new Dimension(INPUT_FIELD_WIDTH, INPUT_FIELD_HEIGHT));
		numberInputField.setAlignmentX(Component.CENTER_ALIGNMENT);
		inputPanel.add(Box.createVerticalStrut(GAP));
		inputPanel.add(numberInputField);

		JButton enterButton = new JButton("Enter");
		enterButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		enterButton.addActionListener(new EnterButtonListener());
		inputPanel.add(Box.createVerticalStrut(GAP));
		inputPanel.add(enterButton);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(TOP_MARGIN, LEFT_MARGIN, BOTTOM_MARGIN, RIGHT_MARGIN));
		centerPanel.add(Box.createVerticalGlue());
		centerPanel.add(inputPanel);
		centerPanel.add(Box.createVerticalGlue());

		add(centerPanel, BorderLayout.CENTER);
		setVisible(true);
	}

	private class EnterButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String input = numberInputField.getText();
			try {
				int number = Integer.parseInt(input);

				if (number <= MIN_NUMBER || number > MAX_NUMBER) {
					JOptionPane.showMessageDialog(IntroPanel.this, "Please enter a valid number between 1 and 1000.");
				} else {
					numbersFrame = new JFrame("Random Numbers");
					numbersFrame.setSize(WIDTH, HEIGHT);
					numbersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

					JPanel numbersPanel = new JPanel();
					numbersPanel.setLayout(new BoxLayout(numbersPanel, BoxLayout.X_AXIS));

					JScrollPane scrollPane = new JScrollPane(numbersPanel);
					scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
					numbersFrame.add(scrollPane, BorderLayout.CENTER);

					JPanel sortPanel = new JPanel();
					sortPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

					JButton sortButton = new JButton("Sort");
					SortButtonListener sortButtonListener = new SortButtonListener(numbersPanel);
					sortButton.addActionListener(sortButtonListener);
					sortPanel.add(sortButton);

					JButton resetButton = new JButton("Reset");
					resetButton.addActionListener(new ResetButtonListener());
					sortPanel.add(resetButton);

					sortPanel.setBorder(BorderFactory.createEmptyBorder(TOP_MARGIN, LEFT_MARGIN, BOTTOM_MARGIN, RIGHT_MARGIN));
					numbersFrame.add(sortPanel, BorderLayout.SOUTH);

					List<Integer> numbers = generateRandomNumbers(number);

					JPanel displayPanel = new JPanel();
					displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.X_AXIS));

					updateNumberPanel(numbersPanel, numbers);

					sortButtonListener.setNumbers(numbers);

					numbersFrame.setVisible(true);
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(IntroPanel.this, "Please enter a valid integer.");
			}
		}

		private List<Integer> generateRandomNumbers(int count) {
			List<Integer> numbers = new ArrayList<>();
			Random random = new Random();

			if (count > 0) {
				numbers.add(random.nextInt(31)); // Число от 0 до 30
				count--;
			}

			for (int i = 0; i < count; i++) {
				numbers.add(random.nextInt(MAX_NUMBER + 1));
			}

			Collections.shuffle(numbers);
			return numbers;
		}
	}

	private class SortButtonListener implements ActionListener {
		private final JPanel numbersPanel;
		private List<Integer> numbers;

		public SortButtonListener(JPanel numbersPanel) {
			this.numbersPanel = numbersPanel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (numbers == null) {
				return;
			}

			quickSort(numbers, 0, numbers.size() - 1);

			if (ascending) {
				Collections.reverse(numbers);
			}

			ascending = !ascending; // Переключаем состояние

			updateNumberPanel(numbersPanel, numbers);

			numbersPanel.revalidate();
			numbersPanel.repaint();
		}

		public void setNumbers(List<Integer> numbers) {
			this.numbers = numbers;
		}

		private void quickSort(List<Integer> arr, int low, int high) {
			if (low < high) {
				int pi = partition(arr, low, high);
				quickSort(arr, low, pi - 1);
				quickSort(arr, pi + 1, high);
			}
		}

		private int partition(List<Integer> arr, int low, int high) {
			int pivot = arr.get(high);
			int i = (low - 1);
			for (int j = low; j < high; j++) {
				if (arr.get(j) < pivot) {
					i++;
					Collections.swap(arr, i, j);
				}
			}
			Collections.swap(arr, i + 1, high);
			return i + 1;
		}
	}

	private class ResetButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (numbersFrame != null) {
				numbersFrame.dispose();
			}

			IntroPanel.this.setVisible(true);
		}
	}

	private void updateNumberPanel(JPanel numbersPanel, List<Integer> numbers) {
		numbersPanel.removeAll();

		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.X_AXIS));

		int columnCount = (int) Math.ceil((double) numbers.size() / MAX_PER_COLUMN);

		for (int i = 0; i < columnCount; i++) {
			JPanel columnPanel = new JPanel();
			columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));

			for (int j = i * MAX_PER_COLUMN; j < (i + 1) * MAX_PER_COLUMN && j < numbers.size(); j++) {
				JButton numberButton = new JButton(String.valueOf(numbers.get(j)));
				numberButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
				numberButton.setAlignmentX(Component.CENTER_ALIGNMENT);

				numberButton.addActionListener(new NumberButtonListener());

				columnPanel.add(numberButton);
			}

			displayPanel.add(columnPanel);
		}

		numbersPanel.add(displayPanel);
		numbersPanel.revalidate();
		numbersPanel.repaint();
	}

	private class NumberButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton clickedButton = (JButton) e.getSource();
			int value = Integer.parseInt(clickedButton.getText());

			if (value <= 30) {
				int number = Integer.parseInt(numberInputField.getText());
				List<Integer> newNumbers = new EnterButtonListener().generateRandomNumbers(number);

				numbersFrame.dispose();

				numbersFrame = new JFrame("Random Numbers");
				numbersFrame.setSize(WIDTH, HEIGHT);
				numbersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				JPanel numbersPanel = new JPanel();
				numbersPanel.setLayout(new BoxLayout(numbersPanel, BoxLayout.X_AXIS));

				JScrollPane scrollPane = new JScrollPane(numbersPanel);
				scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				numbersFrame.add(scrollPane, BorderLayout.CENTER);

				JPanel sortPanel = new JPanel();
				sortPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

				JButton sortButton = new JButton("Sort");
				SortButtonListener sortButtonListener = new SortButtonListener(numbersPanel);
				sortButton.addActionListener(sortButtonListener);
				sortPanel.add(sortButton);

				JButton resetButton = new JButton("Reset");
				resetButton.addActionListener(new ResetButtonListener());
				sortPanel.add(resetButton);

				sortPanel.setBorder(BorderFactory.createEmptyBorder(TOP_MARGIN, LEFT_MARGIN,BOTTOM_MARGIN , RIGHT_MARGIN));
				numbersFrame.add(sortPanel, BorderLayout.SOUTH);

				updateNumberPanel(numbersPanel, newNumbers);

				sortButtonListener.setNumbers(newNumbers);

				numbersFrame.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(IntroPanel.this, "Please select a value smaller or equal to 30.");
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(IntroPanel::new);
	}
}
