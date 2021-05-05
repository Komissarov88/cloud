package my.cloud.client.gui.helper;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;

/**
 * Back and forward opacity cross transition between two javafx panes
 */
public class PaneCrossfade extends AnimationTimer {

    private Pane first;
    private Pane second;
    private int currentFrame;
    private int animationLength;
    private boolean firstToSecond;

    public PaneCrossfade(Pane first, Pane second, int animationLength) {
        this.first = first;
        this.second = second;
        this.animationLength = animationLength;
        firstToSecond = true;
    }

    @Override
    public void start() {
        if (currentFrame != 0) {
            return;
        }
        super.start();
        if (firstToSecond) {
            second.setDisable(false);
        } else {
            first.setDisable(false);
        }
    }

    @Override
    public void stop() {
        currentFrame = 0;
        if (firstToSecond) {
            first.setDisable(true);
        } else {
            second.setDisable(true);
        }
        firstToSecond = !firstToSecond;
        super.stop();
    }

    private double getAnimProgress() {
        return (double) currentFrame / (double) animationLength;
    }

    private double getPaneOpacity(Pane pane) {
        double progress = getAnimProgress();
        if (firstToSecond) {
            return pane.equals(first) ? 1 - progress : progress;
        }
        return pane.equals(first) ? progress : 1 - progress;
    }

    @Override
    public void handle(long now) {
        currentFrame++;
        first.opacityProperty().set(getPaneOpacity(first));
        second.opacityProperty().set(getPaneOpacity(second));
        if (currentFrame >= animationLength) {
            stop();
        }
    }

    public boolean onA() {
        return firstToSecond;
    }
}
