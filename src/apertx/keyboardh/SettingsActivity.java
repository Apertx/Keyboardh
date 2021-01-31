package apertx.keyboardh;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.preference.*;

public class SettingsActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
	}

}
