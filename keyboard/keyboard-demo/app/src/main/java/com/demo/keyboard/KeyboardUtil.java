package com.demo.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import static android.content.Context.AUDIO_SERVICE;

/**
 * The type Keyboard util.
 */
public class KeyboardUtil {
    private EditText currentEditText;
    private KeyboardView mKeyboardView;
    private Context context;

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {
            //do nothing
        }

        @Override
        public void onRelease(int primaryCode) {
            //do nothing
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = currentEditText.getText();
            int start = editable.length();
            playClick(primaryCode);

            if (primaryCode == Keyboard.KEYCODE_CANCEL) {// cancel
                hideKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DONE) {// done
                hideKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// delete
                if (editable.length() > 0 && start > 0) {
                    editable.delete(start - 1, start);
                }
            } else if (0x0 <= primaryCode && primaryCode <= 0x7f) {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onText(CharSequence text) {
            //do nothing
        }

        @Override
        public void swipeLeft() {
            //do nothing
        }

        @Override
        public void swipeRight() {
            //do nothing
        }

        @Override
        public void swipeDown() {
            //do nothing
        }

        @Override
        public void swipeUp() {
            //do nothing
        }
    };

    /**
     * Instantiates a new Keyboard util.
     *
     * @param context      the context
     * @param keyboardView the keyboard view
     */
    public KeyboardUtil(Context context, KeyboardView keyboardView) {
        this.context = context;
        Keyboard keyboardLayout = new Keyboard(context, R.xml.keyboard_numbers);
        this.mKeyboardView = keyboardView;
        this.mKeyboardView.setKeyboard(keyboardLayout);
        this.mKeyboardView.setEnabled(true);
        this.mKeyboardView.setPreviewEnabled(false);
        this.mKeyboardView.setOnKeyboardActionListener(listener);
    }

    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    /**
     * Show keyboard.
     */
    public void showKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            mKeyboardView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide keyboard.
     */
    public void hideKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            mKeyboardView.setVisibility(View.GONE);
        }
    }

    /**
     * Sets current edit text.
     *
     * @param currentEditText the current edit text
     */
    public void setCurrentEditText(EditText currentEditText) {
        this.currentEditText = currentEditText;
    }
}
