package com.sorezel.andruino_bluetooth.invernadero;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sorezel.andruino_bluetooth.R;

public class LoadingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //instancia la vista
        View view = inflater.inflate(R.layout.dialog_loading,container,false);
        return view;
    }
}
