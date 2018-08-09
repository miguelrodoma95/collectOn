package com.myapp.miguel.collectonapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.miguel.collectonapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Miguel on 8/9/2018.
 */
public class CollectionThemesAdapter extends BaseAdapter {

    private ArrayList<String> collectionArray, collectionImages;
    private LayoutInflater mInflater;
    private Context context;

    public CollectionThemesAdapter(Context context, ArrayList<String> collectionArray, ArrayList<String> collectionImages){
        this.context = context;
        this.collectionArray = collectionArray;
        this.collectionImages = collectionImages;
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
        view = inflater.inflate(R.layout.adapter_theme_list,null);

        ImageView themeLogo = (ImageView)view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textTema);

        Picasso.get().load(collectionImages.get(position)).into(themeLogo); //load image form URL arrays.
        textView.setText(collectionArray.get(position));
        return view;
    }
}
