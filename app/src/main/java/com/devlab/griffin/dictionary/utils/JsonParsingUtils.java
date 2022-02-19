package com.devlab.griffin.dictionary.utils;

import android.util.Log;

import com.devlab.griffin.dictionary.models.DictionaryEntry;
import com.devlab.griffin.dictionary.models.MeaningDefinitionExample;
import com.devlab.griffin.dictionary.models.Onyms;
import com.devlab.griffin.dictionary.models.UdDefinitionExample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JsonParsingUtils {

    private static final String TAG = JsonParsingUtils.class.getSimpleName();

    public static HashMap<String, ArrayList<MeaningDefinitionExample>> parseMeanings(String meaningsStr) throws JSONException {
        if(meaningsStr == null) {
            Log.e(TAG, "parseMeanings: Null meaningStr");
            return null;
        }

        final String MEANINGS_KEY = "meanings";
        final String PART_OF_SPEECH_KEY = "partOfSpeech";
        final String DEFINITIONS_KEY = "definitions";
        final String DEFINITION_KEY = "definition";
        final String EXAMPLE_KEY = "example";

        JSONArray responseArray = new JSONArray(meaningsStr);
        JSONObject responseObject = responseArray.getJSONObject(0);
        JSONArray meaningsArray = responseObject.getJSONArray(MEANINGS_KEY);

        HashMap<String, ArrayList<MeaningDefinitionExample>> meanings = new HashMap<>();

        for(int i=0; i<meaningsArray.length(); i++) {
            JSONObject posObject = meaningsArray.getJSONObject(i);
            String partOfSpeech = posObject.getString(PART_OF_SPEECH_KEY);
            JSONArray definitionsArray = posObject.getJSONArray(DEFINITIONS_KEY);

            ArrayList<MeaningDefinitionExample> meaningList = new ArrayList<>();

            for(int j=0; j<definitionsArray.length(); j++) {
                JSONObject definition = definitionsArray.getJSONObject(j);

                MeaningDefinitionExample meaning = new MeaningDefinitionExample(
                        definition.getString(DEFINITION_KEY),
                        definition.getString(EXAMPLE_KEY));

                meaningList.add(meaning);
            }

            if(meaningList.size() > 0) {
                meanings.put(partOfSpeech, meaningList);
            }
        }

        if(meanings.size() > 0) {
            return meanings;
        }

        return null;
    }

    public static HashMap<String, Onyms> parseOnyms(String onymsStr) throws JSONException {
        if(onymsStr.length() == 0) {
            Log.e(TAG, "parseOnyms: Empty onymsStr");
            return null;
        }

        final String SYNONYMS_KEY = "syn";
        final String ANTONYMS_KEY = "ant";

        JSONObject responseObject = new JSONObject(onymsStr);
        Iterator<String> keys = responseObject.keys();

        HashMap<String, Onyms> onymsMap = null;

        if(keys.hasNext()) {
            onymsMap = new HashMap<>();
            Onyms onyms;

            while(keys.hasNext()) {
                String partOfSpeech = keys.next();
                JSONObject onymsObject = responseObject.getJSONObject(partOfSpeech);

                JSONArray synonymsArray = onymsObject.getJSONArray(SYNONYMS_KEY);
                JSONArray antonymsArray = onymsObject.getJSONArray(ANTONYMS_KEY);

                if(synonymsArray != null || antonymsArray != null) {
                    ArrayList<String> synonyms = null;
                    if(synonymsArray != null) {
                        synonyms = new ArrayList<>();
                        for(int i=0; i<synonymsArray.length(); i++)
                            synonyms.add(synonymsArray.getString(i));
                    }

                    ArrayList<String> antonyms = null;
                    if(antonymsArray != null) {
                        antonyms = new ArrayList<>();
                        for(int i=0; i<antonymsArray.length(); i++)
                            antonyms.add(antonymsArray.getString(i));
                    }

                    onyms = new Onyms(synonyms, antonyms);
                    onymsMap.put(partOfSpeech, onyms);
                }
            }
        }

        return onymsMap;
    }

    public static ArrayList<UdDefinitionExample> parseSlangs(String slangsStr) throws JSONException {
        if(slangsStr == null) {
            Log.e(TAG, "parseSlangs: null slangsStr");
            return null;
        }

        final String LIST_KEY = "list";
        final String DEFINITION_KEY = "definition";
        final String EXAMPLE_KEY = "key";

        ArrayList<UdDefinitionExample> slangs = null;

        JSONObject responseObject = new JSONObject(slangsStr);
        JSONArray listObject = responseObject.getJSONArray(LIST_KEY);

        if(listObject.length() > 0) {
            slangs = new ArrayList<>();

            for(int i=0; i<listObject.length(); i++) {
                JSONObject defObject = listObject.getJSONObject(i);

                UdDefinitionExample udDefinitionExample = new UdDefinitionExample(defObject.getString(DEFINITION_KEY), defObject.getString(EXAMPLE_KEY));
                slangs.add(udDefinitionExample);
            }
        }

        return slangs;
    }

    public static DictionaryEntry parseDictionaryEntry(String meaningsStr, String onymsStr, String slangStr) {
        DictionaryEntry dictionaryEntry = new DictionaryEntry();

        try {
            dictionaryEntry.setMeanings(parseMeanings(meaningsStr));
            dictionaryEntry.setOnyms(parseOnyms(onymsStr));
            dictionaryEntry.setSlangs(parseSlangs(slangStr));
        } catch (JSONException jsonException) {
            Log.e(TAG, "parseDictionaryEntry: JSON Exception", jsonException.fillInStackTrace());
        }

        return dictionaryEntry;
    }
}
