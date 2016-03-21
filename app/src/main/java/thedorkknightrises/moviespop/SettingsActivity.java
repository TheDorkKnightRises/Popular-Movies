package thedorkknightrises.moviespop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by samri_000 on 3/20/2016.
 */
public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Spinner sp= (Spinner) findViewById(R.id.spinner);
        sp.setOnItemSelectedListener(this);

        ArrayList<String> elements= new ArrayList<>();
        elements.add(getString(R.string.low_res));
        elements.add(getString(R.string.med_res));
        elements.add(getString(R.string.hi_res));
        elements.add(getString(R.string.max_res));


        ArrayAdapter<String> spAdapter= new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, elements);
        sp.setAdapter(spAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Spinner sp= (Spinner) findViewById(R.id.spinner);

        SharedPreferences pref= getSharedPreferences("Prefs", MODE_PRIVATE);
        int pos= pref.getInt("pos", 1);
        sp.setSelection(pos);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

            String item = parent.getItemAtPosition(position).toString();
            SharedPreferences pref = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor e = pref.edit();
            if (item.equals(getString(R.string.low_res)))
                e.putString("res", "w92");
            else if (item.equals(getString(R.string.med_res)))
                e.putString("res", "w185");
            else if (item.equals(getString(R.string.hi_res)))
                e.putString("res", "w342");
            else if (item.equals(getString(R.string.max_res)))
                e.putString("res", "original");
            e.putInt("pos", position);
            e.commit();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
