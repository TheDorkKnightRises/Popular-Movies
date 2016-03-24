package thedorkknightrises.moviespop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;

/**
 * Created by samri_000 on 3/20/2016.
 */
public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Spinner sp= (Spinner) findViewById(R.id.spinner_thumb);
        sp.setOnItemSelectedListener(this);
        Spinner sp_bg= (Spinner) findViewById(R.id.spinner_bg);
        sp_bg.setOnItemSelectedListener(this);

        ArrayList<String> elements= new ArrayList<>();
        elements.add(getString(R.string.low_res));
        elements.add(getString(R.string.med_res));
        elements.add(getString(R.string.hi_res));
        elements.add(getString(R.string.max_res));

        ArrayAdapter<String> spAdapter= new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, elements);
        sp.setAdapter(spAdapter);

        ArrayAdapter<String> spAdapter_bg= new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, elements);
        sp_bg.setAdapter(spAdapter_bg);


    }

    @Override
    protected void onResume() {
        super.onResume();

        Spinner sp= (Spinner) findViewById(R.id.spinner_thumb);
        Spinner sp_bg= (Spinner) findViewById(R.id.spinner_bg);
        Switch ch= (Switch) findViewById(R.id.bg_check);

        SharedPreferences pref= getSharedPreferences("Prefs", MODE_PRIVATE);

        int pos= pref.getInt("pos", 1);
        int pos_bg= pref.getInt("pos_bg", 1);
        ch.setChecked(pref.getBoolean("bg_enabled", true));
        sp.setSelection(pos);
        sp_bg.setSelection(pos_bg);
        sp_bg.setEnabled(pref.getBoolean("bg_enabled", true));
    }

    public void onCheckedChange(View v)
    {
        SharedPreferences pref= getSharedPreferences("Prefs", MODE_PRIVATE);
        Spinner sp_bg= (Spinner) findViewById(R.id.spinner_bg);
        Boolean b= pref.getBoolean("bg_enabled", true);
        SharedPreferences.Editor e = pref.edit();
        e.putBoolean("bg_enabled", !b);
        e.commit();
        sp_bg.setEnabled(!b);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

        SharedPreferences pref= getSharedPreferences("Prefs", MODE_PRIVATE);
        String item = parent.getItemAtPosition(position).toString();
        SharedPreferences.Editor e = pref.edit();

        if(parent.equals(findViewById(R.id.spinner_thumb))) {
            if (item.equals(getString(R.string.low_res)))
                e.putString("res", "w92");
            else if (item.equals(getString(R.string.med_res)))
                e.putString("res", "w185");
            else if (item.equals(getString(R.string.hi_res)))
                e.putString("res", "w342");
            else if (item.equals(getString(R.string.max_res)))
                e.putString("res", "original");
            e.putInt("pos", position);
        }
        else if(parent.equals(findViewById(R.id.spinner_bg)))
        {
            if (item.equals(getString(R.string.low_res)))
                e.putString("res_bg", "w342");
            else if (item.equals(getString(R.string.med_res)))
                e.putString("res_bg", "w500");
            else if (item.equals(getString(R.string.hi_res)))
                e.putString("res_bg", "w780");
            else if (item.equals(getString(R.string.max_res)))
                e.putString("res_bg", "original");
            e.putInt("pos_bg", position);
        }
            e.commit();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
