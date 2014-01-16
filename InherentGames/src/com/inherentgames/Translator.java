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
		{"Backpack", "Mochila"} 
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
}
