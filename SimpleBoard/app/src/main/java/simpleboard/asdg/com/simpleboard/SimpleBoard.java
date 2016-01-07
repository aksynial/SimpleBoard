package simpleboard.asdg.com.simpleboard;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Terrence on 1/6/2016.
 */
public class SimpleBoard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {
    private KeyboardView kv;
    private Keyboard keyboard;
    private Keyboard normalKeyboard;
    private Keyboard symKeyboard;
    private Keyboard asciiKeyboard;
    private static final int ASCII = 5000;
    private static final int SYM = 5001;
    private int keyboardState;
    int shiftORCAPS;
    public ArrayList<String> liste;
    public ArrayAdapter<String> adapter;
    private boolean caps = false;

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        final InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch(primaryCode){
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                shiftORCAPS = (shiftORCAPS + 1) % 3;
                System.out.println(shiftORCAPS);
                if(shiftORCAPS == 2){

                }
                else {
                    caps = !caps;
                    keyboard.setShifted(caps);
                    kv.invalidateAllKeys();
                }
                break;
            case Keyboard.KEYCODE_DONE:

                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                System.out.println("HAHAHGOOT");
                if(keyboardState == R.integer.keyboard_normal){
                    keyboardState = R.integer.keyboard_symbol;
                    keyboard = symKeyboard;
                    kv.setKeyboard(keyboard);
                    keyboard.setShifted(false);
                }
                else{
                    keyboardState = R.integer.keyboard_symbol;
                    keyboardState = R.integer.keyboard_normal;
                    keyboard = normalKeyboard;
                    kv.setKeyboard(keyboard);
                    keyboard.setShifted(false);
                }
                break;
            case ASCII:
                /*
                System.out.println("INSIDe!!!");
                if(kv != null) {
                    System.out.println("wtfff");
                    if(keyboardState == this.getResources().getInteger(R.integer.keyboard_normal)){
                        //change to symbol keyboard
                        if(asciiKeyboard== null){
                            asciiKeyboard = new Keyboard(this, R.xml.qwerty, R.integer.keyboard_ascii);
                        }
                        System.out.println("AHAHAH");
                        kv.setKeyboard(asciiKeyboard);
                        kv.setOnKeyboardActionListener(this);
                        keyboardState = R.integer.keyboard_symbol;

                    //no shifting
                    kv.setShifted(false);
                    }
                }
                */
                System.out.println("IN");
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.asciiboard_layout, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(keyboard.getHeight());
                popupWindow.setWidth(keyboard.getMinWidth());
                popupWindow.showAsDropDown(kv, 0, -(keyboard.getHeight()));
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                String[] values = getResources().getStringArray(R.array.asciiArray);
                liste = new ArrayList<String>();
                Collections.addAll(liste, values);
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,liste);
                ListView newView = (ListView)popupView.findViewById(R.id.listView);
                newView.setBackgroundColor(Color.BLACK);
                newView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                newView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                        String asciiClicked = (String) parent.getItemAtPosition(position);
                        ic.commitText(asciiClicked, asciiClicked.length());
                        popupWindow.dismiss();
                    }
                });
            case SYM:
                break;
            default:

                char code = (char)primaryCode;
                if(Character.isLetter(code) && caps){
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1);

                if(shiftORCAPS == 1){
                    shiftORCAPS = 0;
                    caps = !caps;
                    keyboard.setShifted(caps);
                    kv.invalidateAllKeys();
                }
        }
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }
    @Override
    public View onCreateInputView() {
        keyboardState = this.getResources().getInteger(R.integer.keyboard_normal);
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        normalKeyboard = new Keyboard(this, R.xml.keyboard_layout, R.integer.keyboard_normal);
        symKeyboard = new Keyboard(this, R.xml.keyboard_layout, R.integer.keyboard_symbol);
        keyboard = normalKeyboard;
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        shiftORCAPS = 0;
        return kv;
    }

    private void playClick(int keyCode){
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(keyCode){
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
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }
}

