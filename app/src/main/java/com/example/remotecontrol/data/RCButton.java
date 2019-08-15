package com.example.remotecontrol.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RCButton {
    private String buttonType;
    int[] irPattern;
    int frequency;

    public RCButton(String name, String irCode) {
        this.buttonType = name;
        this.frequency = 0;
        parseIRPattern(irCode);
    }

    private void parseIRPattern(String irData) {
        List<String> list = new ArrayList<String>(
                Arrays.asList(irData.split(" ")));
        calculateFrequency(list);
        calculateIRPatternFromFrequency(list);
    }

    private void calculateFrequency(List<String> irList) {
        irList.remove(0); // dummy
        int freq = Integer.parseInt(irList.remove(0), 16); // frequency
        irList.remove(0); // seq1
        irList.remove(0); // seq2

        frequency = (int) (1000000 / (freq * 0.241246));
    }

    public String getButtonType() {
        return buttonType;
    }

    public int[] getIrPattern() {

        return irPattern;
    }

    public int getFrequency() {
        return frequency;
    }

    private void calculateIRPatternFromFrequency(List<String> irList) {
        int pulses = 1000000 / frequency;
        irPattern = new int[irList.size()];

        for (int i = 0; i < irList.size(); i++) {
            int count = Integer.parseInt(irList.get(i), 16);
            irPattern[i] = count * pulses;
        }
    }

}
