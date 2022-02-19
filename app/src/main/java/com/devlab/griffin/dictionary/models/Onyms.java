package com.devlab.griffin.dictionary.models;

import java.sql.Array;
import java.util.ArrayList;

public class Onyms {

    private ArrayList<String> synonyms;
    private ArrayList<String> antonyms;

    public Onyms(ArrayList<String> synonyms, ArrayList<String> antonyms) {
        this.synonyms = synonyms;
        this.antonyms = antonyms;
    }

    public ArrayList<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(ArrayList<String> synonyms) {
        this.synonyms = synonyms;
    }

    public ArrayList<String> getAntonyms() {
        return antonyms;
    }

    public void setAntonyms(ArrayList<String> antonyms) {
        this.antonyms = antonyms;
    }

    public String getSynonymAt(int position) {
        if(synonyms == null || synonyms.size() <= position)
            return null;
        return synonyms.get(position);
    }

    public String getAntonymAt(int position) {
        if(antonyms == null || antonyms.size() <= position)
            return null;
        return antonyms.get(position);
    }

    public void addSynonym(String synonym) {
        if(synonyms == null) {
            synonyms = new ArrayList<>();
        }
        synonyms.add(synonym);
    }

    public void addAntonym(String antonym) {
        if(antonyms != null) {
            antonyms = new ArrayList<>();
        }
        antonyms.add(antonym);
    }
}
