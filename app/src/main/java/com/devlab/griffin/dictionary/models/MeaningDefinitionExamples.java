package com.devlab.griffin.dictionary.models;

import java.util.ArrayList;

public class MeaningDefinitionExamples {

    private String definition;
    private ArrayList<String> examples;

    public MeaningDefinitionExamples() {
        examples = new ArrayList<>();
    }

    public MeaningDefinitionExamples(String definition, ArrayList<String> examples) {
        this.definition = definition;
        if(examples != null) {
            this.examples = examples;
        } else {
            this.examples = new ArrayList<>();
        }
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public ArrayList<String> getExamples() {
        return examples;
    }

    public void setExamples(ArrayList<String> examples) {
        this.examples = examples;
    }
}
