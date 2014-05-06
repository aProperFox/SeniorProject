package com.inherentgames;

import android.util.Log;

/**
 * @author Tyler
 * A class that 'translates' a word to another language. Should be expandable to other languages
 */
public class BBTranslator {
	
	// Define valid language values
	/**
	 * @author Tyler
	 * A class with values associated with languages. Could be changed to an enum?
	 */
	public class Language { 
		public static final int ENGLISH = 0;
		public static final int SPANISH = 1;
	}
	
	
	private static String wordList[][] = {	
		//Room 0-1
		{"Desk", "Escritorio"},
		{"Chair", "Silla"},
		{"Chalkboard", "Pizarra"},
		{"Backpack", "Mochila"}, 
		{"Calendar", "Calendario"},
		{"Clock", "Reloj"},
		{"Door", "Puerta"},
		{"Book", "Libro"},
		{"Paper", "Papel"},
		{"Window", "Ventana"},
		//Room 2
		{"Bill", "Cuenta"},
		{"Bread", "Pan"},
		{"Cake", "Pastel"},
		{"Cup", "Taza"},
		{"Knife", "Cuchillo"},
		{"Money", "Efectivo"},
		{"Plate", "Plato"},
		{"Spoon", "Cuchara"},
		{"Table", "Mesa"},
		//Room 3
		{"Address", "Direccion"},
		{"Bicycle", "Bicicleta"},
		{"Bus", "Autobus"},
		{"Car", "Coche"},
		{"Map", "Mapa"},
		{"Police", "Policia"},
		{"Sign", "Senal"},
		{"Taxi", "Taxi"},
		{"Traffic_Light", "Semaforo"},
		{"Trash", "Basura"},
	};

	/**
	 * "Translates" a word from one language to another by looking up the translation in the wordList
	 * array.
	 * 
	 * @param word - the word to look up
	 * @param language - the language to translate to
	 * @return - the translated word, if found
	 */
	public static String translateToLanguage( String word, int language ) {
		Log.i( "THE LENGTH OF THE ARRAY IS:", " " + wordList.length );
		for ( int i = 0; i < wordList.length; i++ ) {
			if ( wordList[i][0] == word ) {
				return wordList[i][language];
			}
		}
		return null;
	}
	
	/**
	 * Gets the first index of the word in the wordList array.
	 * 
	 * @param word - the word to look up
	 * @return - the first index of the word in the multi-dimensional array.
	 */
	public static int getIndexByWord( String word ) {
		for ( int i = 0; i < wordList.length; i++ ) {
			for ( int j = 0; j < wordList[0].length; j++ ) {
				if ( wordList[i][j] == word ) {
					Log.i( "olsontl", "Word + id: " + wordList[i][j] + " " + i );
					return i;
				}
			}
		}
		return -1;
	}
	
}
