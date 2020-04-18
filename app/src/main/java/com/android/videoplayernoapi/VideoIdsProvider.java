package com.android.videoplayernoapi;

import java.util.Random;

/**
 * Created by Surendar D on 18/04/20.
 */
public class VideoIdsProvider {
    private static String[] videoIds = {"6JYIGclVQdw", "LvetJ9U_tVY", "S0Q4gqBUs7c", "zOa-rSM4nms"};
    private static String[] liveVideoIds = {"hHW1oY26kxQ"};
    private static Random random = new Random();

    public static String getNextVideoId() {
        return videoIds[random.nextInt(videoIds.length)];
    }

    public static String getNextLiveVideoId() {
        return liveVideoIds[random.nextInt(liveVideoIds.length)];
    }
}