package com.example.aznho.floatyattempt;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.Arrays;

/**
 * Created by aznho on 7/26/2016.
 */
public class PokemonAutoCompleteTextView extends AutoCompleteTextView{

    public PokemonAutoCompleteTextView(Context context) {
        super(context);

//        String pokemon_names = new String(getResources().getString(R.string.pokemon_names_list));
//        String[] pokemon_names_list = pokemon_names.split(",");
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
//        AutoCompleteTextView textView = (AutoCompleteTextView)
//                findViewById(R.id.countries_list);
//        textView.setAdapter(adapter);


    }

    private static final String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain"
    };


}
