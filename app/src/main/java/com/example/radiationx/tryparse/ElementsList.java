package com.example.radiationx.tryparse;

import java.util.Arrays;

public class ElementsList {
    private Element[] array = {};
    private int count = 0;

    public ElementsList() {}

    public void add(Element item) {
        count++;
        if (count > array.length)
            array = Arrays.copyOf(array, count);
        array[count - 1] = item;
    }

    public int size() {
        return count;
    }

    public Element get(int index) {
        return array[index];
    }

    public Element[] toArray() {
        return Arrays.copyOf(array, count);
    }

    public void remove(int index) {
        array[index] = null;
        count--;
    }
}
