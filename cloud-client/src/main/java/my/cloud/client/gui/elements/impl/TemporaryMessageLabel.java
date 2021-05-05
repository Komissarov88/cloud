package my.cloud.client.gui.elements.impl;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;

public class TemporaryMessageLabel extends Label {

    private final AnimationTimer animationTimer;
    private final long SHOW_FOR_NANO = 3_000_000_000L;
    private long timeLeft = SHOW_FOR_NANO;
    private final float ANIM_SPEED = 1/60f;
    private long oldNow;

    public TemporaryMessageLabel() {
        animationTimer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (oldNow < 0) {
                    oldNow = now;
                    return;
                }
                if (timeLeft <= 0) {
                    if (getOpacity() > 0) {
                        setOpacity(getOpacity() - ANIM_SPEED);
                    } else {
                        stop();
                    }
                } else {
                    timeLeft -= now - oldNow;
                    oldNow = now;
                }
            }
        };
    }

    public void updateText(String text) {
        setText(text);
        setOpacity(1);
        oldNow = -1;
        timeLeft = SHOW_FOR_NANO;
        animationTimer.start();
    }
}
