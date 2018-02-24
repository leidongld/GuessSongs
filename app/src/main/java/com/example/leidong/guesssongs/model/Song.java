package com.example.leidong.guesssongs.model;

/**
 * Created by leidong on 2018/2/10
 */

public class Song {
    private String songName;
    private String songFileName;
    private int songLength;

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
        this.songLength = songName.length();
    }

    public String getSongFileName() {
        return songFileName;
    }

    public void setSongFileName(String songFileName) {
        this.songFileName = songFileName;
    }

    public int getSongLength() {
        return songLength;
    }

    public void setSongLength(int songLength) {
        this.songLength = songLength;
    }

    public char[] getNameCharacters(){
        return songName.toCharArray();
    }
}
