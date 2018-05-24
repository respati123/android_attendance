package com.android.victory.schedule.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ExpandableDataPump  {
    public static HashMap<String, List<String>> getData(){
        ArrayList<String> listGroup = new ArrayList<String>();
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();

        listGroup.add("Account");
        listGroup.add("Settings");
        listGroup.add("Logout");

        List<String> account = new ArrayList<String>();

        account.add("photo");
        account.add("password");

        List<String> settings = new ArrayList<String>();

        settings.add("Bahasa");
        settings.add("Umur data local");

        List<String> keluar = new ArrayList<String>();

        expandableListDetail.put(listGroup.get(0), account);
        expandableListDetail.put(listGroup.get(1), settings);
        expandableListDetail.put(listGroup.get(2), keluar);

        return expandableListDetail;
    }
}
