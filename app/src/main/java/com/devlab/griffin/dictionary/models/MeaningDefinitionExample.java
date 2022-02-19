package com.devlab.griffin.dictionary.models;

import java.util.ArrayList;

public class MeaningDefinitionExample {

    private String definition;
    private String example;

    public MeaningDefinitionExample() {
        this.definition = null;
        this.example = null;
    }

    public MeaningDefinitionExample(String definition, String example) {
        this.definition = definition;
        this.example = example;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
