package com.amazonaws;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshmellowman.collageucf.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Password_Reset extends Fragment {


    public Password_Reset() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password__reset, container, false);
    }

}
