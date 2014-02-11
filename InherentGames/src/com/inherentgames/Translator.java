package com.inherentgames;

import android.util.Log;

public class Translator {
	//Language values
	public static final int ENGLISH = 0;
	public static final int SPANISH = 1;
	
	private static String wordList[][] = {	
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
		{"Bill", "Cuenta"},
		{"Bread", "Pan"},
		{"Cake", "Pastel"},
		{"Cup", "Copa"},
		{"Knife", "Cuchillo"},
		{"Money", "Efectivo"},
		{"Plate", "Plato"},
		{"Spoon", "Cuchara"},
		{"Table", "Mesa"},
	};

	public static String translateToLanguage(String word, int language){
		Log.i("THE LENGTH OF THE ARRAY IS:", " " + wordList.length);
		for(int i = 0; i < wordList.length; i++){
			if(wordList[i][0] == word){
				return wordList[i][language];
			}
		}
		return null;
	}
	
	public static int getIndexByWord(String word){
		for(int i = 0; i < wordList.length; i++){
			for(int j = 0; j < wordList[0].length; j++){
				if(wordList[i][j] == word){
					Log.i("olsontl", "Word + id: " + wordList[i][j] + " " + i);
					return i;
				}
			}
		}
		return -1;
	}
	
}
