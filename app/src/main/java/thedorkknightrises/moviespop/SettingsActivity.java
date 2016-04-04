package thedorkknightrises.moviespop;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
        SwitchCompat ch_bg= (SwitchCompat) findViewById(R.id.bg_check);
        SwitchCompat ch_anim = (SwitchCompat) findViewById(R.id.anim_check);
        SwitchCompat ch_palette = (SwitchCompat) findViewById(R.id.palette_check);

        SharedPreferences pref= getSharedPreferences("Prefs", MODE_PRIVATE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            SharedPreferences.Editor e = pref.edit();
            e.putBoolean("anim_enabled", false);
            e.commit();
            findViewById(R.id.anim_check_box).setEnabled(false);
            findViewById(R.id.anim_setting).setEnabled(false);
            ((TextView) findViewById(R.id.def_text_anim)).setText(R.string.sdk_low);
        }

        int pos= pref.getInt("pos", 1);
        int pos_bg= pref.getInt("pos_bg", 1);

        sp.setSelection(pos);
        sp_bg.setSelection(pos_bg);
        sp_bg.setEnabled(pref.getBoolean("bg_enabled", true));

        ch_bg.setChecked(pref.getBoolean("bg_enabled", true));
        ch_anim.setChecked(pref.getBoolean("anim_enabled", true));
        ch_palette.setChecked(pref.getBoolean("palette_enabled", true));
    }

    public void onCheckedChange(View v)
    {
        SharedPreferences pref= getSharedPreferences("Prefs", MODE_PRIVATE);
        if (v.equals(findViewById(R.id.bg_check)) || v.equals(findViewById(R.id.bg_check_box))) {
            Spinner sp_bg = (Spinner) findViewById(R.id.spinner_bg);
            SwitchCompat ch_bg = (SwitchCompat) findViewById(R.id.bg_check);
            Boolean b = pref.getBoolean("bg_enabled", true);
            SharedPreferences.Editor e = pref.edit();
            e.putBoolean("bg_enabled", !b);
            e.commit();
            if (!v.equals(ch_bg))
                ch_bg.toggle();
            sp_bg.setEnabled(!b);
        } else if (v.equals(findViewById(R.id.anim_check)) || v.equals(findViewById(R.id.anim_check_box))) {
            SwitchCompat ch_anim = (SwitchCompat) findViewById(R.id.anim_check);
            Boolean b = pref.getBoolean("anim_enabled", true);
            SharedPreferences.Editor e = pref.edit();
            e.putBoolean("anim_enabled", !b);
            e.commit();
            if (!v.equals(ch_anim))
                ch_anim.toggle();
        } else if (v.equals(findViewById(R.id.palette_check)) || v.equals(findViewById(R.id.palette_check_box))) {
            SwitchCompat ch_palette = (SwitchCompat) findViewById(R.id.palette_check);
            Boolean b = pref.getBoolean("palette_enabled", true);
            SharedPreferences.Editor e = pref.edit();
            e.putBoolean("palette_enabled", !b);
            e.commit();
            if (!v.equals(ch_palette))
                ch_palette.toggle();
        }
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
