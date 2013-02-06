package fr.grea09.android.authentic.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import fr.grea09.android.authentic.ui.activity.AccountChooser;

public class Main extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, AccountChooser.class);
		startActivity(intent);
		//        setContentView(R.layout.main);
    }
}
