package apertx.keyboardh;
import android.content.*;
import android.inputmethodservice.*;
import android.media.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.view.inputmethod.*;

public class MainActivity extends InputMethodService {
	boolean shift;
	boolean caps;
	boolean lang;
	boolean sound;
	boolean vibrate;
	int vibtime;
	long delay;
	Vibrator vibrator;

	@Override
	public View onCreateInputView() {
		final KeyboardView kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
		final Keyboard en_keyboard = new Keyboard(this, R.layout.en_keys);
		final Keyboard ru_keyboard = new Keyboard(this, R.layout.ru_keys);
		final Keyboard sym_keyboard = new Keyboard(this, R.layout.sym_keys);
		final Keyboard alt_keyboard = new Keyboard(this, R.layout.alt_keys);
		kv.setSoundEffectsEnabled(true);
		final SoundPool snd_def = new SoundPool(1, 1, 0);
		snd_def.load(this, R.raw.kstandard, 1);
		final SoundPool snd_del = new SoundPool(1, 1, 0);
		snd_del.load(this, R.raw.kdelete, 1);
		final SoundPool snd_ret = new SoundPool(1, 1, 0);
		snd_ret.load(this, R.raw.kreturn, 1);
		final SoundPool snd_spc = new SoundPool(1, 1, 0);
		snd_spc.load(this, R.raw.kspacebar, 1);
		kv.setKeyboard(en_keyboard);
		vibrator = (Vibrator) getSystemService("vibrator");
		kv.setPreviewEnabled(false);
		shift = false;
		caps = false;
		lang = true;
		kv.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
				@Override
				public void onKey(int p1, int[] p2) {
					InputConnection ic = getCurrentInputConnection();
					switch (p1) {
						case Keyboard.KEYCODE_DELETE:
							ic.deleteSurroundingText(1, 0);
							if (sound)
								snd_del.play(1, 1, 1, 1, 0, 1);
							break;
						case Keyboard.KEYCODE_SHIFT:
							shift = !shift;
							if (caps)
								caps = false;
							else if (delay + 300 > System.currentTimeMillis()) {
								caps = true;
								shift = true;
							}
							delay = System.currentTimeMillis();
							if (lang)
								en_keyboard.setShifted(shift);
							else
								ru_keyboard.setShifted(shift);
							kv.invalidateAllKeys();
							break;
						case Keyboard.KEYCODE_DONE:
							ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
							if (sound)
								snd_ret.play(1, 1, 1, 1, 0, 1);
							break;
						case -10:
							kv.setKeyboard(sym_keyboard);
							break;
						case Keyboard.KEYCODE_MODE_CHANGE:
							shift = false;
							caps = false;
							if (lang)
								en_keyboard.setShifted(shift);
							else
								ru_keyboard.setShifted(shift);
							kv.invalidateAllKeys();
							lang = !lang;
						case -12:
							if (lang)
								en_keyboard.setShifted(shift);
							else
								ru_keyboard.setShifted(shift);
							kv.invalidateAllKeys();
							if (lang)
								kv.setKeyboard(en_keyboard);
							else
								kv.setKeyboard(ru_keyboard);
							break;
						case -13:
							kv.setKeyboard(alt_keyboard);
							break;
						case 32:
							if (!caps && shift) {
								shift = false;
								if (lang)
									en_keyboard.setShifted(shift);
								else
									ru_keyboard.setShifted(shift);
								kv.invalidateAllKeys();
							}
							ic.commitText(" ", 1);
							if (sound)
								snd_spc.play(1, 1, 1, 1, 0, 1);
							break;
						default:
							char code = (char) p1;
							if (Character.isLetter(code) && shift)
								code = Character.toUpperCase(code);
							if (!caps && shift) {
								shift = false;
								if (lang)
									en_keyboard.setShifted(shift);
								else
									ru_keyboard.setShifted(shift);
								kv.invalidateAllKeys();
							}
							ic.commitText(String.valueOf(code), 1);
							if (sound)
								snd_def.play(1, 1, 1, 1, 0, 1);
					}
				}
				@Override
				public void onText(CharSequence p1) {}
				@Override
				public void swipeLeft() {}
				@Override
				public void swipeRight() {}
				@Override
				public void swipeDown() {}
				@Override
				public void swipeUp() {}
				@Override
				public void onPress(int p1) {
					if (vibrate)
						vibrator.vibrate(vibtime);
				}
				@Override
				public void onRelease(int p1) {}
			});
		return kv;
	}

	@Override
	public void onWindowShown() {
		super.onWindowShown();
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		sound = settings.getBoolean("sound", true);
		vibrate = settings.getBoolean("vibrate", false);
		String str = settings.getString("vibtime", "50");
		if (str.length() <= 4)
			vibtime = Integer.parseInt(str);
		else
			vibtime = 50;
	}

	@Override
	public void onWindowHidden() {
		super.onWindowHidden();
		vibrator.cancel();
	}
}
