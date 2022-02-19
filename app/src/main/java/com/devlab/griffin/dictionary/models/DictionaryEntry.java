package com.devlab.griffin.dictionary.models;

import java.util.ArrayList;
import java.util.HashMap;

public class DictionaryEntry {

    private static final String TAG = DictionaryEntry.class.getSimpleName();

    private HashMap<String, ArrayList<MeaningDefinitionExample>> meanings;
    private HashMap<String, Onyms> onyms;
    private ArrayList<UdDefinitionExample> slangs;

    public HashMap<String, ArrayList<MeaningDefinitionExample>> getMeanings() {
        return meanings;
    }

    public void setMeanings(HashMap<String, ArrayList<MeaningDefinitionExample>> meanings) {
        this.meanings = meanings;
    }

    public HashMap<String, Onyms> getOnyms() {
        return onyms;
    }

    public void setOnyms(HashMap<String, Onyms> onyms) {
        this.onyms = onyms;
    }

    public ArrayList<UdDefinitionExample> getSlangs() {
        return slangs;
    }

    public void setSlangs(ArrayList<UdDefinitionExample> slangs) {
        this.slangs = slangs;
    }

    public void addMeaning(String partOfSpeech, MeaningDefinitionExample meaning) {
        if(this.meanings == null)
            this.meanings = new HashMap<>();

        if(meaning != null) {
            if(this.meanings.containsKey(partOfSpeech)) {
                ArrayList<MeaningDefinitionExample> existingMeanings = this.meanings.get(partOfSpeech);
                existingMeanings.add(meaning);
                this.meanings.put(partOfSpeech, existingMeanings);
            }
            else {
                ArrayList<MeaningDefinitionExample> newMeanings = new ArrayList<>();
                newMeanings.add(meaning);
                this.meanings.put(partOfSpeech, newMeanings);
            }
        }
    }

    public void setOnymsForPartOfSpeech(String partOfSpeech, Onyms synAntonyms) {
        if(onyms == null) {
            onyms = new HashMap<>();
        }
        onyms.put(partOfSpeech, synAntonyms);
    }

    public void addSlang(UdDefinitionExample slang) {
        if(slangs == null)
            slangs = new ArrayList<>();
        slangs.add(slang);
    }
}
