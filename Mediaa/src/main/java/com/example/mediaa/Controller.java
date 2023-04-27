package com.example.mediaa;

import java.io.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

public class Controller {


    @FXML
    private MediaView viewer;

    @FXML
    private ImageView play;

    @FXML
    private ImageView pause;

    @FXML
    private ImageView stop;

    @FXML
    private Slider volume;

    @FXML
    private Slider file;

    @FXML
    private ImageView slow;

    @FXML
    private ImageView fast;

    @FXML
    private ImageView open;

    @FXML
    private Label playing;

    @FXML
    private Label total;
    @FXML
    private ImageView audi;

    private MediaPlayer mediaPlayer;

    private boolean isSliderChanging;

    private String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private final ChangeListener<Duration> currentTimeListener = (observable, oldValue, newValue) -> {
        playing.setText(formatDuration(newValue));
        file.setValue((newValue.toMillis() / mediaPlayer.getTotalDuration().toMillis()) * 100.0);
    };

    @FXML
    void initialize() {
        // Set up the event handlers
        play.setOnMouseClicked(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        });

        pause.setOnMouseClicked(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });

        stop.setOnMouseClicked(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });

        file.setOnMouseClicked(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(file.getValue() / 100.0));
            }
        });

        file.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (mediaPlayer != null && !isSliderChanging) {
                    mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(file.getValue() / 100.0));
                }
            }
        });

        file.setOnMousePressed(event -> {
            isSliderChanging = true;
        });

        file.setOnMouseReleased(event -> {
            isSliderChanging = false;
        });

        slow.setOnMouseClicked(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.setRate(mediaPlayer.getRate() * 0.5);
            }
        });

        fast.setOnMouseClicked(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.setRate(mediaPlayer.getRate() * 2.0);
            }
        });

        open.setOnMouseClicked(event -> {
            openFile();

        });

        volume.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(volume.getValue() / 100.0);
                }
            }
        });

        if (mediaPlayer != null) {
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration current) {
                    playing.setText(formatDuration(current));
                }
            });

            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    total.setText(formatDuration(mediaPlayer.getMedia().getDuration()));
                }
            });
        }
    }
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(null);
        fileChooser.setInitialFileName(null);
        fileChooser.setTitle("Open Media File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Media Files","*.m4a", "*.mp3", "*.mp4", "*.wav", "*.aiff", "*.au"));
        File selectedFile = fileChooser.showOpenDialog(open.getScene().getWindow());

        if (selectedFile != null) {
            Media media = new Media(selectedFile.toURI().toString());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            audi.setFitWidth(0);
            audi.setFitHeight(0);
            viewer.setFitHeight(620);
            viewer.setFitWidth(1280);
            viewer.setMediaPlayer(mediaPlayer);

            mediaPlayer.setAudioSpectrumNumBands(64);
            mediaPlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
                // update your UI here based on the magnitudes of each frequency band
            });

            mediaPlayer.bufferProgressTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!isSliderChanging) {
                    double currentTime = mediaPlayer.getCurrentTime().toMillis();
                    double totalTime = mediaPlayer.getTotalDuration().toMillis();
                    file.setValue((currentTime / totalTime) * 100.0);
                }
            });

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!isSliderChanging) {
                    playing.setText(formatDuration(newValue));
                }
            });

            mediaPlayer.setOnReady(() -> {
                total.setText(formatDuration(mediaPlayer.getMedia().getDuration()));


                if (selectedFile.getName().endsWith(".mp3") || selectedFile.getName().endsWith(".wav"))
                {

                    Image image = new Image("file:music.gif");
                    audi.setImage(image);
                    audi.setFitHeight(220);
                    audi.setFitWidth(700);
                    viewer.setFitHeight(0);
                    viewer.setFitWidth(0);

                    play.setOnMouseClicked(event -> {
                        if (mediaPlayer != null && selectedFile.getName().endsWith(".mp3") || selectedFile.getName().endsWith(".wav")  ) {
                            mediaPlayer.play();
                        }
                    });
                }
                else if (selectedFile.getName().endsWith(".mp4") || selectedFile.getName().endsWith(".mkv"))
                {
                    audi.setImage(null);
                }
            });

    }
}}