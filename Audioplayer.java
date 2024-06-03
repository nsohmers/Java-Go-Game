import java.io.*;
import java.util.concurrent.CompletableFuture;

import javax.sound.sampled.*;

public class Audioplayer {
    private Clip plop, whoosh, remove, music;

    public Audioplayer() {
        File filePathPlop = new File("audios/plop.wav");
        File filePathWhoosh = new File("audios/whoosh.wav");
        File filePathRemove = new File("audios/remove.wav");
        File filePathMusic = new File("audios/music.wav");

        try {
            AudioInputStream plopAudio = AudioSystem.getAudioInputStream(filePathPlop);
            this.plop = AudioSystem.getClip();
            this.plop.open(plopAudio);
        } catch (Exception e) {
            System.err.println("Error loading plop audio: " + e.getMessage());
        }

        try {
            AudioInputStream whooshAudio = AudioSystem.getAudioInputStream(filePathWhoosh);
            this.whoosh = AudioSystem.getClip();
            this.whoosh.open(whooshAudio);
        } catch (Exception e) {
            System.err.println("Error loading whoosh audio: " + e.getMessage());
        }

        try {
            AudioInputStream removeAudio = AudioSystem.getAudioInputStream(filePathRemove);
            this.remove = AudioSystem.getClip();
            this.remove.open(removeAudio);
        } catch (Exception e) {
            System.err.println("Error loading remove audio: " + e.getMessage());
        }

        try {
            AudioInputStream musicAudio = AudioSystem.getAudioInputStream(filePathMusic);
            this.music = AudioSystem.getClip();
            this.music.open(musicAudio);
        } catch (Exception e) {
            System.err.println("Error loading music audio: " + e.getMessage());
        }
    }

    public void playPlop() {
        try {
            synchronized (plop) {
                if (plop.isRunning()) {
                    plop.stop();
                }
                plop.setFramePosition(0);
                CompletableFuture.runAsync(() -> {
                    synchronized (plop) {
                        plop.start();
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Error playing plop audio: " + e.getMessage());
        }
    }

    public void playWhoosh() {
        try {
            synchronized (whoosh) {
                if (whoosh.isRunning()) {
                    whoosh.stop();
                }
                whoosh.setFramePosition(0);
                CompletableFuture.runAsync(() -> {
                    synchronized (whoosh) {
                        whoosh.start();
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Error playing whoosh audio: " + e.getMessage());
        }
    }

    public void playRemove() {
        try {
            synchronized (remove) {
                if (remove.isRunning()) {
                    remove.stop();
                }
                remove.setFramePosition(0);
                CompletableFuture.runAsync(() -> {
                    synchronized (remove) {
                        remove.start();
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Error playing remove audio: " + e.getMessage());
        }
    }

    public void playGameMusic() {
        try {
            synchronized (music) {
                if (music.isRunning()) {
                    music.stop();
                }
                music.setFramePosition(0);
                music.loop(Clip.LOOP_CONTINUOUSLY);
                CompletableFuture.runAsync(() -> {
                    synchronized (music) {
                        music.start();
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Error playing game music: " + e.getMessage());
        }
    }

    public void stopGameMusic() {
        try {
            synchronized (music) {
                if (music.isRunning()) {
                    music.stop();
                }
            }
        } catch (Exception e) {
            System.err.println("Error stopping game music: " + e.getMessage());
        }
    }
}