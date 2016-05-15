package com.botpy.demo.ui.model;

import java.util.ArrayList;
import java.util.List;

public class Row {

    public List<String> cells;

    public Row() {
        cells = new ArrayList<>();
    }

    public Row(List<String> cells) {
        this.cells = cells;
    }

    public String getRowContent(int index) {
        return cells.get(index);
    }
}