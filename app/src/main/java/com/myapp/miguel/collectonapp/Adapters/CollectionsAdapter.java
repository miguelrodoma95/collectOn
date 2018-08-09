package com.myapp.miguel.collectonapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.myapp.miguel.collectonapp.R;

import java.util.ArrayList;

/**
 * Created by axented on 8/9/2018.
 */

public class CollectionsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> collectionArray;
    private LayoutInflater mInflater;

    public CollectionsAdapter(Context context, ArrayList<String> collectionArray){
        this.context = context;
        this.collectionArray = collectionArray;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return collectionArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.adapter_collections_list,null);

        TextView textView = view.findViewById(R.id.textTema);

        textView.setText(collectionArray.get(position));

        return view;
    }
}
