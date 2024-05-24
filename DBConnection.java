package Wortbildungsspiel;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import dbClient.DBConnection;

class DBConnection
{
	
	private static Connection dbConn = null;
	private static String connectionString;
	public ArrayList<String> dictionary = new ArrayList<>();

	
	// Privater Standardkonstruktor.
	// Alle Methoden dieser Klasse sind statisch. Durch die Deklaration eines
	// eigenen Standardkonstruktors wird verhindert, dass Java einen Standardkonstruktor
	// erstellt.
	// Die Änderung des Zugriffsmodifizierers von 'public' in 'private'
	// verhindert, dass eine Instanz dieser Klasse erstellt werden kann.
	DBConnection() {
		try
		{
			dbConn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/alfatraining", "root", "");
			DBConnection.connectionString = connectionString;
		}
		catch (Exception ex)
		{
			dbConn = null;
			JOptionPane.showMessageDialog(null, "Fehler beim Zugriff auf die Datenbank: " + ex.getMessage(), "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
		}
		
		// pulling all words from DB to ArrayList
		try {
	        String query = "SELECT WORD FROM `germannouns`";
	        ResultSet resultSet = DBConnection.executeQuery(query);
	        while (resultSet.next()) {
                dictionary.add(resultSet.getString("WORD"));
            }
	        resultSet.close();
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Fehler beim Zugriff auf die Datenbank: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	public static int executeNonQuery(String SQL)
	{
		
		// Die Methode executeUpdate() gibt für einige Non-Query
		// Anweisungen  eine 0 zurück, obwohl sie erfolgreich
		// ausgeführt wurden (z.B. CREATE DATABASE, CREATE TABLE...)
		// Im Fehlerfall wird deshalb der Rückgabewert auf -1 gesetzt.  
		int retValue = -1;
		
		Statement stmt;
		
		if (dbConn == null)
			return retValue;
		
		try
		{
			stmt = dbConn.createStatement();
			retValue = stmt.executeUpdate(SQL);
			stmt.close();
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Fehler beim Zugriff auf die Datenbank: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		
		return retValue;
	}
	
	public static Object executeScalar(String SQL)
	{
		
		Statement stmt;
		Object retValue = null;
		
		if (dbConn == null)
			return retValue;
		
		try
		{
			
			stmt = dbConn.createStatement();
			ResultSet rSet = stmt.executeQuery(SQL);
			
			// Auf die erste Zeile innerhalb des ResultSets positionieren
			rSet.next();
			
			// Den Inhalt der ersten Spalte in der Zeile dem Rückgabewert zuweisen
			retValue = rSet.getObject(1);
			
			// ResultSet schließen
			rSet.close();
			// Statement schließen
			stmt.close();
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Fehler beim Zugriff auf die Datenbank: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		
		return retValue;
		
	}
	
	public static ResultSet executeQuery(String SQL)
	{
		Statement stmt;
		ResultSet rSet = null;
		
		if (dbConn == null)
			return rSet;
		
		try
		{
			stmt = dbConn.createStatement();
			rSet = stmt.executeQuery(SQL);
			
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Fehler beim Zugriff auf die Datenbank: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		
		return rSet;

	}
	
	public String pickRandomWord() {
		String randomWord = null;
	    try {
	        String query = "SELECT WORD FROM `germannouns` WHERE CHAR_LENGTH(WORD) > 6 ORDER BY RAND() LIMIT 1;";
	        ResultSet resultSet = DBConnection.executeQuery(query);
	        if (resultSet.next()) {
                randomWord = resultSet.getString("WORD");
            }
	        resultSet.close();
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Fehler beim Zugriff auf die Datenbank: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
	    }
	    return randomWord;
	}
	
	
	private HashMap<Character, Integer> countLetters(String word) {
		HashMap<Character, Integer> result = new HashMap<>();
		for (char letter : word.toCharArray()) {
			int amount = 1;
			if (result.containsKey(letter)) {
				amount = result.get(letter) + 1;
			}
			result.put(letter, amount);
		}
		return result;
	}
	
	private boolean isPossibleCombination(String combination, String fromWord) {
		HashMap<Character, Integer> availableLetters = countLetters(fromWord);
		HashMap<Character, Integer> requiredLetters = countLetters(combination);
		for (Character letter : requiredLetters.keySet()) {
			// В введенном слове есть буквы, которых нет в исходном
			if (!availableLetters.containsKey(letter)) {
				return false;
			}
			if (availableLetters.get(letter) < requiredLetters.get(letter)) {
				return false;
			}
		}
		return true;
	}
	
	public ArrayList<String> getPossibleCombinations(String forWord) {
		ArrayList<String> possibleCombinations = new ArrayList<>();
		for (String word : dictionary) {
			// the word itself shouldn't be considered as a possible combination
			if (isPossibleCombination(word, forWord) && !word.equals(forWord)) {
				possibleCombinations.add(word);
			}
		}
		return possibleCombinations;
	}
	

    public int getWordPopularity(String word, String combination) {
    	int wordPopularity = 0;
    	 try {
    		 String query = "SELECT POPULARITY FROM `gamestats` WHERE WORD = '" + word + "' AND COMBINATION = '" + combination + "' LIMIT 1;";
 	        ResultSet resultSet = DBConnection.executeQuery(query);
 	        if (resultSet.next()) {
 	        	wordPopularity = resultSet.getInt("POPULARITY");
             }
 	        resultSet.close();
 	    } catch (SQLException ex) {
 	        ex.printStackTrace();
 	        JOptionPane.showMessageDialog(null, "Fehler beim Zugriff auf die Datenbank: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
 	    }
 		return wordPopularity;
    }
    
    public void setWordPopularity(String word, String combination) {
    	 int currentPopularity = getWordPopularity(word, combination);
    	    if (currentPopularity == 0) {
    	        // If empty, insert a new one = 1
    	        String insertQuery = "INSERT INTO `gamestats` (WORD, COMBINATION, POPULARITY) VALUES ('" + word + "', '" + combination + "', 1)";
    	        DBConnection.executeNonQuery(insertQuery);
    	    } else {
    	        // Otherwise ++ for each call
    	        String updateQuery = "UPDATE `gamestats` SET POPULARITY = POPULARITY + 1 WHERE WORD = '" + word + "' AND COMBINATION = '" + combination + "'";
    	        DBConnection.executeNonQuery(updateQuery);
    	    }
    }
   
    
    public int getHighScore(String word) {
    	int highScore = 0;
	    try {
	        String query = "SELECT HIGHSCORE FROM `germannouns` WHERE WORD = '" + word + "' LIMIT 1;";
	        ResultSet resultSet = DBConnection.executeQuery(query);
	        if (resultSet.next()) {
	        	highScore = resultSet.getInt("HIGHSCORE");
            }
	        resultSet.close();
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Fehler beim Zugriff auf die Datenbank: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
	    }
		return highScore;
    	
    }

    public void setHighScore(String word, int highscore) {
    	String query = "UPDATE `germannouns` SET HIGHSCORE=" + highscore + " WHERE WORD = '" + word + "'";
		DBConnection.executeNonQuery(query);
    }
	
	
	
	
	
}
