package com.example.demoscreensplithelper;

import android.content.Context;
import android.transition.ChangeBounds;
import android.transition.ChangeScroll;

import android.transition.Explode;
import android.transition.TransitionSet;
import android.util.AttributeSet;

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
