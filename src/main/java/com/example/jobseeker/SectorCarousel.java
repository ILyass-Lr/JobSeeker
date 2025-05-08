package com.example.jobseeker;

import javafx.animation.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;

public class SectorCarousel {
    private final HBox container;
    private final List<StackPane> allSectors = new ArrayList<>();
    private final Duration transitionDuration = Duration.seconds(1.1);
    private final Duration displayDuration = Duration.seconds(1.9);
    private int currentIndex = 0;
    private Timeline timeline;
    private final double fadeStartPoint = 0.7; // Point where fade starts (70% of the way to the sidebar)

    public SectorCarousel(HBox container) {
        this.container = container;
        // Store initial sectors
        container.getChildren().forEach(node -> allSectors.add((StackPane) node));
    }

    public void addSector(StackPane sector) {
        allSectors.add(sector);
    }

    public void startAnimation() {
        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(
                new KeyFrame(displayDuration, event -> animateTransition())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stopAnimation() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void animateTransition() {
        List<Transition> transitions = new ArrayList<>();

        // Handle existing sectors
        for (int i = 0; i < container.getChildren().size(); i++) {
            StackPane sector = (StackPane) container.getChildren().get(i);

            // Create transition group for this sector
            ParallelTransition sectorTransition = new ParallelTransition();

            // Movement transition
            TranslateTransition translate = new TranslateTransition(transitionDuration, sector);
            double startX = sector.getTranslateX();
            double endX = startX - (sector.getBoundsInParent().getWidth() + 15);
            translate.setFromX(startX);
            translate.setToX(endX);

            // Fade transition for the leftmost sector
            if (i == 0) {
                FadeTransition fade = new FadeTransition(transitionDuration, sector);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                // Start fading earlier in the animation
                fade.setDelay(Duration.seconds(0));
                sectorTransition.getChildren().add(fade);
            }

            sectorTransition.getChildren().add(translate);
            transitions.add(sectorTransition);
        }

        // Prepare the new sector
        int nextIndex = (currentIndex + container.getChildren().size()) % allSectors.size();
        StackPane newSector = allSectors.get(nextIndex);

        // Position and set up the new sector
        double startX = container.getBoundsInParent().getWidth();
        newSector.setTranslateX(startX);
        newSector.setOpacity(0); // Start invisible

        // Create transitions for the new sector
        ParallelTransition newSectorTransition = new ParallelTransition();

        // Movement
        TranslateTransition newTranslate = new TranslateTransition(transitionDuration, newSector);
        newTranslate.setFromX(startX);
        newTranslate.setToX(0);

        // Fade in
        FadeTransition newFade = new FadeTransition(transitionDuration.multiply(0.5), newSector);
        newFade.setFromValue(0.0);
        newFade.setToValue(1.0);

        newSectorTransition.getChildren().addAll(newTranslate, newFade);
        transitions.add(newSectorTransition);

        // Create and play parallel transition
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(transitions);

        // Handle completion
        parallelTransition.setOnFinished(event -> {
            container.getChildren().removeFirst();
            if(!container.getChildren().contains(newSector)) {
                container.getChildren().add(newSector);
            }

            container.getChildren().forEach(node -> {
                ((Node) node).setTranslateX(0);
                ((Node) node).setOpacity(1.0);
            });
            currentIndex = (currentIndex + 1) % allSectors.size();
        });

        parallelTransition.play();
    }
}