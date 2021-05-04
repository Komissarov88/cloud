package my.cloud.client.gui.helper;

import javafx.animation.AnimationTimer;
import javafx.scene.control.ProgressBar;

public class AnimatedProgressBar extends ProgressBar {

    final double ANIM_SPEED = 5d / 60d;

    private final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            opacityHandle();
        }
    };

    public void reset() {
        setProgress(0);
        opacityProperty().set(0);
        animationTimer.stop();
    }

    public void startAnimation() {
        animationTimer.start();
    }

    private void opacityHandle() {
        double opacity = opacityProperty().get();
        double progress = progressProperty().get();
        if (progress >= 0 && progress < 1) {
            if (opacity < 1) {
                opacityProperty().set(opacity + ANIM_SPEED);
            }
        } else {
            if (opacity > 0) {
                opacityProperty().set(opacity - ANIM_SPEED);
            } else {
                setProgress(0);
                animationTimer.stop();
            }
        }
    }
}
