package com.devlab.griffin.dictionary.models;

public class UdDefinitionExample {

    private String definition;
    private String example;

    public UdDefinitionExample() {
        this.definition = null;
        this.example = null;
    }

    public UdDefinitionExample(String definition, String example) {
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
