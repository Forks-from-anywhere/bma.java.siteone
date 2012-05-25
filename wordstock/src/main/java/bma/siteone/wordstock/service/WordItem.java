package bma.siteone.wordstock.service;

import java.util.List;
import java.util.regex.Pattern;

import bma.siteone.wordstock.po.WordInfo;

public class WordItem {

	private WordInfo info;
	private List<Pattern> patternList;
	private List<String> wordList;

	public WordInfo getInfo() {
		return info;
	}

	public void setInfo(WordInfo info) {
		this.info = info;
	}

	public List<Pattern> getPatternList() {
		return patternList;
	}

	public void setPatternList(List<Pattern> patternList) {
		this.patternList = patternList;
	}

	public List<String> getWordList() {
		return wordList;
	}

	public void setWordList(List<String> wordList) {
		this.wordList = wordList;
	}

	@Override
	public String toString() {
		return info.toString();
	}
}
