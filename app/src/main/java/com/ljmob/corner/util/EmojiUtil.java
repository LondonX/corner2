package com.ljmob.corner.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import android.content.Context;

public class EmojiUtil {
	public static ArrayList<String> getEmojis(Context context) {
		ArrayList<String> emojis = new ArrayList<String>();
		String[] tempEmojis = getEmojiLine(context).split("");
		for (String emo : tempEmojis) {
			if (emo != null && emo.length() != 0 && emo.equals("") == false
					&& emo.equals(" ") == false) {
				emojis.add(emo);
			}
		}
		return emojis;
	}

	public static String getEmojiLine(Context context) {
		String emoji = "";
		try {
			Scanner scanner = new Scanner(context.getAssets().open(
					"emoji-utf8.txt"));
			emoji = scanner.nextLine();
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return emoji;
	}
}
