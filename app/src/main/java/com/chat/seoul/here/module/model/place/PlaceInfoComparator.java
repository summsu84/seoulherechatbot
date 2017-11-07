package com.chat.seoul.here.module.model.place;

import java.util.Comparator;

/**
 * Created by JJW on 2016-10-22.
 */
public class PlaceInfoComparator implements Comparator<PlaceModel> {

    @Override
    public int compare(PlaceModel lhs, PlaceModel rhs) {
        String firstName = lhs.getPLACE_NAME();
        String secondName = rhs.getPLACE_NAME();

        if(firstName.compareTo(secondName) > 0)
        {
            return -1;
        }else if(firstName.compareTo(secondName) < 0)
        {
            return 1;
        }else {
            return 0;
        }
    }
}
