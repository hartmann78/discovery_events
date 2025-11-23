package com.practice.stats_dto;

import java.util.Comparator;

public class ViewStatsComparator implements Comparator<ViewStats> {
    @Override
    public int compare(ViewStats o1, ViewStats o2) {
        return o2.getHits().compareTo(o1.getHits());
    }
}
