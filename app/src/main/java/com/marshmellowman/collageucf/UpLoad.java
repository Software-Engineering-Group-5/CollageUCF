package com.marshmellowman.collageucf;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Button;



/**
 * A simple {@link Fragment} subclass.
 */
public class UpLoad extends Fragment {


    public UpLoad() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_up_load, container, false);
        Button btn1 = (Button) thisView
                .findViewById(R.id.button);

        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), EditPhoto.class);
                getActivity().startActivity(intent);

            }
        });

        return thisView;
    }


}
