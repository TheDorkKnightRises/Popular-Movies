package thedorkknightrises.moviespop;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by samri_000 on 4/30/2016.
 */
public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_layout);
    }

    public void more(View view) {
        View v = findViewById(R.id.acc_desc_text);
        if (v != null) {
            if (v.getVisibility() == View.GONE) {
                ((TextView) view).setText(R.string.acc_hide_more);
                v.setVisibility(View.VISIBLE);
            } else {

                ((TextView) view).setText(R.string.acc_more);
                v.setVisibility(View.GONE);
            }
        }
    }
}
