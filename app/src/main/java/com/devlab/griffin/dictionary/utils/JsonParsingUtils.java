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
        if(meaningsStr == null || meaningsStr.length()<=2) {
            Log.e(TAG, "parseMeanings: Null meaningStr");
            return null;
        }

        final String MEANINGS_KEY = "meanings";
        final String PART_OF_SPEECH_KEY = "partOfSpeech";
        final String DEFINITIONS_KEY = "definitions";
        final String DEFINITION_KEY = "definition";
        final String EXAMPLE_KEY = "example";
        JSONArray responseArray;

        try {
            responseArray = new JSONArray(meaningsStr);
        } catch (Exception e) {
            Log.e(TAG, "parseMeanings: Exception in parsing JSON array. object not found", e.fillInStackTrace());
            return null;
        }

        JSONObject responseObject = responseArray.optJSONObject(0);
        if(responseObject == null)
            return null;

        JSONArray meaningsArray = responseObject.optJSONArray(MEANINGS_KEY);
        if(meaningsArray == null || meaningsArray.length() == 0)
            return null;

        HashMap<String, ArrayList<MeaningDefinitionExample>> meanings = new HashMap<>();

        for(int i=0; i<meaningsArray.length(); i++) {
            JSONObject posObject = meaningsArray.optJSONObject(i);

            if(posObject != null) {
                String partOfSpeech = posObject.optString(PART_OF_SPEECH_KEY, "");
                JSONArray definitionsArray = posObject.optJSONArray(DEFINITIONS_KEY);

                if(definitionsArray != null) {
                    ArrayList<MeaningDefinitionExample> meaningList = new ArrayList<>();

                    for(int j=0; j<definitionsArray.length(); j++) {
                        JSONObject definition = definitionsArray.optJSONObject(j);

                        if(definition != null) {
                            MeaningDefinitionExample meaning = new MeaningDefinitionExample(
                                    definition.optString(DEFINITION_KEY, ""),
                                    definition.optString(EXAMPLE_KEY, ""));

                            meaningList.add(meaning);
                        }
                    }

                    if(meaningList.size() > 0) {
                        meanings.put(partOfSpeech, meaningList);
                    }
                }
            }
        }

        if(meanings.size() > 0) {
            return meanings;
        }

        return null;
    }

    public static HashMap<String, Onyms> parseOnyms(String onymsStr) throws JSONException {
        if(onymsStr == null || onymsStr.length() == 0) {
            Log.e(TAG, "parseOnyms: Empty onymsStr");
            return null;
        }

        final String SYNONYMS_KEY = "syn";
        final String ANTONYMS_KEY = "ant";

        JSONObject responseObject;
        try {
            responseObject = new JSONObject(onymsStr);
        } catch (Exception e) {
            Log.e(TAG, "parseOnyms: Exception in parsing JSON object. object not found", e.fillInStackTrace());
            return null;
        }

        Iterator<String> keys = responseObject.keys();

        HashMap<String, Onyms> onymsMap = null;

        if(keys.hasNext()) {
            onymsMap = new HashMap<>();
            Onyms onyms;

            while(keys.hasNext()) {
                String partOfSpeech = keys.next();
                JSONObject onymsObject = responseObject.optJSONObject(partOfSpeech);

                if(onymsObject != null) {
                    JSONArray synonymsArray = onymsObject.optJSONArray(SYNONYMS_KEY);
                    JSONArray antonymsArray = onymsObject.optJSONArray(ANTONYMS_KEY);

                    if(synonymsArray != null || antonymsArray != null) {
                        ArrayList<String> synonyms = null;
                        if(synonymsArray != null) {
                            synonyms = new ArrayList<>();
                            for(int i=0; i<synonymsArray.length(); i++)
                                synonyms.add(synonymsArray.optString(i, ""));
                        }

                        ArrayList<String> antonyms = null;
                        if(antonymsArray != null) {
                            antonyms = new ArrayList<>();
                            for(int i=0; i<antonymsArray.length(); i++)
                                antonyms.add(antonymsArray.optString(i, ""));
                        }

                        onyms = new Onyms(synonyms, antonyms);
                        onymsMap.put(partOfSpeech, onyms);
                    }
                }
            }
        }

        return onymsMap;
    }

    public static ArrayList<UdDefinitionExample> parseSlangs(String slangsStr) throws JSONException {
        if(slangsStr == null || slangsStr.length() == 0) {
            Log.e(TAG, "parseSlangs: null slangsStr");
            return null;
        }

        final String LIST_KEY = "list";
        final String DEFINITION_KEY = "definition";
        final String EXAMPLE_KEY = "example";

        ArrayList<UdDefinitionExample> slangs = null;

        JSONObject responseObject;
        try {
            responseObject = new JSONObject(slangsStr);
        } catch (Exception e) {
            Log.e(TAG, "parseSlangs: Exception in parsing JSON object. object not found", e.fillInStackTrace());
            return null;
        }

        JSONArray listObject = responseObject.optJSONArray(LIST_KEY);

        if(listObject != null && listObject.length() > 0) {
            slangs = new ArrayList<>();

            for(int i=0; i<listObject.length(); i++) {
                JSONObject defObject = listObject.optJSONObject(i);

                if(defObject != null) {
                    UdDefinitionExample udDefinitionExample = new UdDefinitionExample(defObject.optString(DEFINITION_KEY, ""), defObject.optString(EXAMPLE_KEY, ""));
                    slangs.add(udDefinitionExample);
                }
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
