package com.example.shoppingwithfriends;

import java.util.ArrayList;
import java.util.Locale;

public class SearchFriends {

    static ArrayList<String> oldItems;
    static String oldQuery;

    /**
     *
     * @param items The objects to search
     * @param query The search parameter
     * @return The filtered objects
     */
    public static ArrayList<String> search(ArrayList<String> items, String query) {
        query = query.toLowerCase(Locale.ENGLISH);
        if ((oldQuery != null) && query.contains(oldQuery)) {
            items = oldItems;
        }
        oldItems = new ArrayList<String>();
        for (String item : items) {
            if ((item != null) && item.toLowerCase(Locale.ENGLISH).contains(query)) {
                oldItems.add(item);
                break;
            }
        }
        oldQuery = query;
        return oldItems;
    }
}
