package Wortbildungsspiel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

import javax.sql.PooledConnection;
import javax.swing.*;

public class Wortbildungsspiel extends JFrame implements ActionListener {
	private JPanel gamePanel;
	private JPanel controlPanel;
	
	private JTextArea results;
	private JTextField inputField;
	private JTextField wordField;
	private JButton restartButton;
	private JTextField scoreField;
	private JTextField timerField;
	
	private JLabel scoreLabel;
	private JLabel timerLabel;
	private Timer timer;
	private int remainingTime;
	private ArrayList<String> guesses = new ArrayList<String>();
	private ArrayList<String> possibleCombinations = new ArrayList<>();
	private int highscore;
	private DBConnection db;
	private String randomWord;
	public Wortbildungsspiel() {
		initComponents();
		db = new DBConnection();
		startNewGame();

	}
	// initialising a timer that determines the duration of the game (unless a user guessed all words)
	private void startTimer(int duration) {
		if (timer != null) {
			timer.stop();
		}
		remainingTime = duration;
		// updating the image of the timer
		ActionListener updateTimer = new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if (remainingTime > 0) {
					remainingTime-- ;
					timerField.setText(formatTime(remainingTime));
				}
				else {
					timer.stop();
					onTimerEnd();
				}
			}
		};
		// timer is being updated every second
	    timer = new Timer(1000, updateTimer);
	    timer.start();	
	}
	
	// formatting time so it looks like minutes:seconds
	private String formatTime(int seconds) {
	    int minutes = seconds / 60;
	    int remainingSeconds = seconds % 60;
	    return String.format("%02d:%02d", minutes, remainingSeconds);
	}

	// splitting word into letters and counting the amount of each individual letter 
	// in the word
	private HashMap<Character, Integer> countLetters(String word) {
		HashMap<Character, Integer> result = new HashMap<>();
		for (char letter : word.toCharArray()) {
			int amount = 1;
			// if there are more than 1 letter, concatenate
			if (result.containsKey(letter)) {
				amount = result.get(letter) + 1;
			}
			result.put(letter, amount);
		}
		return result;
	}
	
	private void startNewGame() {
		// clearing fields and starting a new timer
		startTimer(10);
		guesses.clear();
		results.setText("");
		inputField.setEditable(true);
		// show a new word
		randomWord = db.pickRandomWord();
		highscore = db.getHighScore(randomWord);
		scoreField.setText("current: 0 best: " + highscore);
		wordField.setText(capitalizeFirstLetter(randomWord));
		possibleCombinations = db.getPossibleCombinations(randomWord);
		// console print possible combinations
		for (String word : possibleCombinations) {
			System.out.println(word);
		}
	
	}	

	private void submitGuess()
	{
		String inputWord = inputField.getText().toLowerCase();
		// check if input empty, don't allow it
		if (inputWord.isBlank()) {
			return;
		}
		// Если это слово было уже добавлено до этого
		if (guesses.contains(inputWord)) {
			JOptionPane.showMessageDialog(this, "Fehler bei der Eingabe: " + inputWord + "  wurde bereits eingegeben", "Information", JOptionPane.INFORMATION_MESSAGE);
			inputField.setText("");
			return;
		}
		if (!possibleCombinations.contains(inputWord)) {
			JOptionPane.showMessageDialog(this, "Fehler bei der Eingabe " + inputWord + " ist eine falsche Vermutung", "Fehler", JOptionPane.ERROR_MESSAGE);
			inputField.setText("");
			return;
		}
		// setting a word popularity for statistics on the end of the game
		db.setWordPopularity(randomWord, inputWord);
		
		guesses.add(inputWord);
		// collecting guesses and counting the points for them
		StringBuilder resultText = new StringBuilder();
		int score = 0;
		
		for (String word : guesses) {
			int submittedWordScore = word.length();
			score += submittedWordScore;
			String capitalizedWord = capitalizeFirstLetter(word);
			resultText.append(capitalizedWord).append(" —  ").append(submittedWordScore).append(" points\n");

		}
		results.setText(resultText.toString());
		
		// clearing the input field after correctly inserting the guess
		inputField.setText("");
		
		if (highscore < score) {
			highscore = score;
			db.setHighScore(wordField.getText().toLowerCase(), highscore);
		}
		scoreField.setText("current: " + score + " best: " + highscore);

		// calculating the amount of not guessed words
		int amountNotGuessedWords = possibleCombinations.size() - guesses.size();
		// end the game when there are no more words to guess
		if (amountNotGuessedWords == 0) {
			onGameEnd();
		}
	}



	// called when a user guessed all the possible combinations
	private void onGameEnd()
	{	
		timer.stop();
		showStats();
		// disable input before prompting an answer 
		inputField.setEditable(false);
		int answer = JOptionPane.showConfirmDialog(this, "Sie haben alle möglichen Wörter erraten. Ein neues Spiel beginnen?", "Herzlichen Glückwunsch!", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
             startNewGame();
        } 
	}
	
	// called when the timer is due
	private void onTimerEnd() {
		timer.stop();
		showStats();
		// disable input before prompting an answer 
		inputField.setEditable(false);
		int answer = JOptionPane.showConfirmDialog(this, "Die Zeit is abgelaufen. Ein neues Spiel beginnen?", "Zeit ist abgelaufen", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
             startNewGame();
         } 
	}


	private void initComponents()
	{	// Creating a frame
		this.setTitle("Wortbildungsspiel");
		this.setSize(new Dimension(800, 600));
		this.setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Adding main panels to the frame
		// Game panel
		JPanel gamePanel = new JPanel();
		gamePanel.setBounds(20, 15, 500, 530);
		gamePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
		this.add(gamePanel);
		
		// Control panel
		JPanel controlPanel = new JPanel();
		controlPanel.add(new JLabel("Control panel"));
		controlPanel.setLayout(null); 
		controlPanel.setBounds(470, 15, 300, 530);
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.add(controlPanel);
		
		
		// Adding the word field
		wordField = new JTextField();
		wordField.setPreferredSize(new Dimension(150, 30)); 
		wordField.setEditable(false);
		wordField.setHorizontalAlignment(JTextField.CENTER);
		wordField.setFont(new Font("Serif", Font.BOLD, 20));
		gamePanel.add(wordField);
		
		// Adding the input field
		inputField = new JTextField();
		// Setting preferredSize for the pack() method
		inputField.setPreferredSize(new Dimension(150, 30));
		inputField.addActionListener(this);
		gamePanel.add(inputField);
		
		// Adding the results text area
		results = new JTextArea();
		results.setPreferredSize(new Dimension(400, 100)); 
		results.setEditable(false);
		gamePanel.add(results);
		
		// Align all elements withing gamePanel
		gamePanel.revalidate();
		
		// Adding timer
		timerLabel = new JLabel("Timer:");
		timerLabel.setHorizontalAlignment(JLabel.CENTER);
		timerLabel.setBounds(10, 30, 140, 25);
		controlPanel.add(timerLabel);
		
		timerField = new JTextField();
		timerField.setBounds(110,30, 140, 25);
		timerField.setEditable(false);
		controlPanel.add(timerField);
		
		// Adding score field

		scoreLabel = new JLabel("Score:");
		scoreLabel.setHorizontalAlignment(JLabel.CENTER);
		scoreLabel.setBounds(10, 90, 140, 25);
		controlPanel.add(scoreLabel);
		
		scoreField = new JTextField();
		scoreField.setBounds(110,90, 140, 25);
		scoreField.setEditable(false);
		controlPanel.add(scoreField);
		
		// Adding Restart button
		restartButton = new JButton("Restart game");
		restartButton.setBounds(120,200, 140, 25);
		restartButton.addActionListener(this);
		controlPanel.add(restartButton);

		//
		
	}
	
	public static String capitalizeFirstLetter(String word) {
        if (word == null || word.isEmpty()) {
        	// dont change anything if it's an empty strint
            return word; 
        }
        // the first letter is uppercased, the remaining letters are set to lower case
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
	
	public void showFrame() {
		initFrame();
		// Adjust the size of child elements so they fit into panel
		this.setVisible(true);
	}
	
	

	private void initFrame()
	{
		// Show in the middle of the desktop
		this.setLocationRelativeTo(null);
	}

	public static void main(String[] args)
	{
		Wortbildungsspiel spiel = new Wortbildungsspiel();
		spiel.showFrame();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == restartButton) {
			startNewGame();
		}
		if (e.getSource() == inputField) {
			submitGuess();
		}
	}

	private void showStats()
	{
		// Получить размеры основного окна
	    int mainWidth = this.getWidth();
	    int mainHeight = this.getHeight();
	    
	    // Установить размеры окна статистики немного меньше основного окна
	    int statsWidth = (int) (mainWidth * 0.75);
	    int statsHeight = (int) (mainHeight * 0.75);
	    
	    // Получить текущее положение основного окна
	    int x = this.getX();
	    int y = this.getY();
	    
	    // Создать окно статистики
	    JFrame statsFrame = new JFrame("Spielstatistik für das Wort: " + capitalizeFirstLetter(randomWord));
	    statsFrame.setSize(statsWidth, statsHeight);
	    statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    
	    // Установить положение окна статистики слева и немного наезжать на основное окно
	    int overlap = 170; // Количество пикселей, на которое окно статистики наезжает на основное окно
	    statsFrame.setLocation(x - statsWidth + overlap, y); // Установить положение окна

	    JPanel panel = new JPanel(new GridLayout(1, 2)); // Один ряд и два столбца

	    JTextArea guessedWordsArea = new JTextArea();
	    guessedWordsArea.setEditable(false); 
	    
	    StringBuilder displayText = new StringBuilder("Erratene Wörter:\n");

	    for (String word : guesses) {
	    	String capitalizedWord = capitalizeFirstLetter(word);
	        displayText.append(capitalizedWord)
	                   .append(" —  ")
	                   .append(db.getWordPopularity(randomWord, word))
	                   .append(" Mal erraten\n");
	    }

	    guessedWordsArea.setText(displayText.toString());
	    
	    JTextArea missedWordsArea = new JTextArea();
	    missedWordsArea.setEditable(false);
	    // creating a new ArrayList on a basis of possibleCombination
	    ArrayList<String> notGuessedWords = new ArrayList<>(possibleCombinations);
	    notGuessedWords.removeAll(guesses);
	    
	    StringBuilder displayText2 = new StringBuilder("Nicht erratene Wörter:\n");
	    for (String word : notGuessedWords) {
	    	String capitalizedWord = capitalizeFirstLetter(word);
	    	displayText2.append(capitalizedWord)
	                   .append(" —  ")
	                   .append(db.getWordPopularity(randomWord, word))
	                   .append(" Mal erraten\n");
	    }
	    
	    missedWordsArea.setText(displayText2.toString());
	    
	    panel.add(new JScrollPane(guessedWordsArea));
	    panel.add(new JScrollPane(missedWordsArea));
	    
	    statsFrame.add(panel);
	    
	    statsFrame.setVisible(true);
	}
	}
	
