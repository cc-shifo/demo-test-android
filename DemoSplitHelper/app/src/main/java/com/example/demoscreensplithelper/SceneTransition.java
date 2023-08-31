package com.example.demoscreensplithelper;

import android.content.Context;
import android.util.AttributeSet;

import androidx.transition.ChangeBounds;
import androidx.transition.Explode;
import androidx.transition.TransitionSet;


public class SceneTransition extends TransitionSet {
    public SceneTransition() {
        init();
    }
    public SceneTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        addTransition(new Explode())
                // .addTransition(new ChangeScroll())
                // .addTransition(new ChangeBackgroundColorTransition())
                .addTransition(new ChangeBounds());
    }
}
