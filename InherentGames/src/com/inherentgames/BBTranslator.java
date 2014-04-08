package com.inherentgames;

import android.util.Log;

public class BBTranslator {
	
	// Define valid language values
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
		{"Bus", "Autobus"},
		{"Car", "Coche"},
		{"Map", "Mapa"},
		{"Sign", "Senal"},
		{"Taxi", "Taxi"},
		{"Traffic_Light", "Semaforo"},
	};

	/**
	 * @param word
	 * @param language
	 * @return
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
	 * @param word
	 * @return
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
