package my.cloud.client.gui.elements.impl;

import javafx.animation.AnimationTimer;
import javafx.scene.control.ProgressBar;

public class AnimatedProgressBar extends ProgressBar {

    private final double ANIM_SPEED = 5d / 60d;

    private final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            opacityHandle();
        }
    };

    public void startAnimation() {
        animationTimer.start();
    }

    private void opacityHandle() {
        double opacity = opacityProperty().get();

        if (Math.abs(getProgress()) < 1) {
            if (opacityProperty().get() < 1) {
                opacityProperty().set(opacity + ANIM_SPEED);
            }
        } else {
            if (opacityProperty().get() > 0) {
                opacityProperty().set(opacity - ANIM_SPEED);
            } else {
                animationTimer.stop();
                reset();
            }
        }
    }

    public void reset() {
        opacityProperty().set(0);
        progressProperty().set(0);
    }
}
