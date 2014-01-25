
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.android.controller;

import net.usikkert.kouchat.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Test controller.
 *
 * @author Christian Ihle
 */
public class TestController extends Activity {

    private EditText mainChatInput;
    private TextView mainChatView;
    private ScrollView mainChatScroll;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_chat);

        mainChatInput = (EditText) findViewById(R.id.mainChatInput);
        mainChatView = (TextView) findViewById(R.id.mainChatView);
        mainChatScroll = (ScrollView) findViewById(R.id.mainChatScroll);

        registerMainChatInputListener();
//        registerMainChatScrollLayoutListener();
//        registerMainChatTextListener();
        openKeyboard();
    }

    private void registerMainChatInputListener() {
        mainChatInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    appendToChat(mainChatInput.getText().toString());
                    mainChatInput.setText("");

                    return true;
                }

                return false;
            }
        });
    }

//    private void registerMainChatScrollLayoutListener() {
//        mainChatScroll.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(final View v, final int left, final int top, final int right,
//                                       final int bottom, final int oldLeft, final int oldTop, final int oldRight,
//                                       final int oldBottom) {
//                scroll();
//            }
//        });
//    }
//
//    private void registerMainChatTextListener() {
//        mainChatView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) { }
//
//            @Override
//            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) { }
//
//            @Override
//            public void afterTextChanged(final Editable s) {
//                scroll();
//            }
//        });
//    }

    private void openKeyboard() {
        mainChatInput.requestFocus();
    }

    public void appendToChat(final CharSequence message) {
        runOnUiThread(new Runnable() {
            public void run() {
                mainChatView.append(message);
                scroll();
            }
        });
    }

    private void scroll() {
        mainChatScroll.post(new Runnable() {
            @Override
            public void run() {
                mainChatScroll.smoothScrollTo(0, mainChatScroll.getBottom() + mainChatView.getHeight());
//                mainChatScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

//    @Override
//    public boolean dispatchKeyEvent(final KeyEvent event) {
//        if (!mainChatInput.hasFocus()) {
//            mainChatInput.requestFocus();
//        }
//
//        return super.dispatchKeyEvent(event);
//    }
}
