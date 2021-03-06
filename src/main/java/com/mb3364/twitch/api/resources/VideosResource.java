package com.mb3364.twitch.api.resources;

import com.mb3364.http.RequestParams;
import com.mb3364.twitch.api.auth.Scopes;
import com.mb3364.twitch.api.handlers.TopVideosResponseHandler;
import com.mb3364.twitch.api.handlers.VideoResponseHandler;
import com.mb3364.twitch.api.handlers.VideosResponseHandler;
import com.mb3364.twitch.api.models.Video;
import com.mb3364.twitch.api.models.Videos;
import com.mb3364.twitch.api.models.VideosTop;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The {@link VideosResource} provides the functionality
 * to access the <code>/videos</code> endpoints of the Twitch API.
 *
 * @author Matthew Bell
 */
public class VideosResource extends AbstractResource {

    /**
     * Construct the resource using the Twitch API base URL and specified API version.
     *
     * @param baseUrl    the base URL of the Twitch API
     * @param apiVersion the requested version of the Twitch API
     */
    public VideosResource(String baseUrl, int apiVersion) {
        super(baseUrl, apiVersion);
    }

    /**
     * Gets a specified {@link Video} object.
     *
     * Note that in prior versions of the API, the specified video ID required a “v” prefix. That prefix is deprecated.
     *
     * @param id      the ID of the Video
     * @param handler the response handler
     */
    public void get(final String id, final VideoResponseHandler handler) {
        String url = String.format("%s/videos/%s", getBaseUrl(), id);

        http.get(url, new TwitchHttpResponseHandler(handler) {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    Video value = objectMapper.readValue(content, Video.class);
                    handler.onSuccess(value);
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }

    /**
     * Gets the top videos based on viewcount, optionally filtered by game or time period.
     *
     * @param params  the optional request parameters:
     *                <ul>
     *                <li><code>limit</code>:  the maximum number of objects in array. Maximum is 100.</li>
     *                <li><code>offset</code>: the object offset for pagination. Default is 0.</li>
     *                <li><code>game</code>: Returns only videos from game.</li>
     *                <li><code>period</code>: Returns only videos created in time period.
     *                Valid values are <code>week</code>, <code>month</code>,
     *                or <code>all</code>. Default is <code>week</code>.</li>
     *                <li><code>broadcast_type</code>: Comma-Separated List: Constrains the type of videos returned.
     *                Valid values: (any combination of) <code>archive</code>, <code>highlight</code>, <code>upload</code>.
     *                Default: <code>highlight</code>.</li>
     *                </ul>
     * @param handler the response handler
     */
    public void getTop(final RequestParams params, final TopVideosResponseHandler handler) {
        String url = String.format("%s/videos/top", getBaseUrl());

        http.get(url, params, new TwitchHttpResponseHandler(handler) {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    VideosTop value = objectMapper.readValue(content, VideosTop.class);
                    handler.onSuccess(value.getVods().size(), value.getVods());
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }

    /**
     * Returns a list of {@link Video}'s created in a given time period sorted by number of views, most popular first.
     *
     * @param handler the response handler
     */
    public void getTop(final TopVideosResponseHandler handler) {
        getTop(null, handler);
    }

    /**
     * Gets the videos from channels followed by a user, based on a specified OAuth token.
     *
     * Authenticated, required scope: {@link Scopes#USER_READ}
     *
     * @param params  the optional request parameters:
     *                <ul>
     *                <li><code>limit</code>:  the maximum number of objects in array. Maximum is 100.</li>
     *                <li><code>offset</code>: the object offset for pagination. Default is 0.</li>
     *                <li><code>broadcast_type</code>: Comma-Separated List: Constrains the type of videos returned.
     *                Valid values: (any combination of) <code>archive</code>, <code>highlight</code>, <code>upload</code>.
     *                Default: <code>highlight</code>.</li>
     *                </ul>
     * @param handler the response handler
     */
    public void getFollowed(final RequestParams params, final VideosResponseHandler handler) {
        String url = String.format("%s/videos/followed", getBaseUrl());

        http.get(url, params, new TwitchHttpResponseHandler(handler) {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    Videos value = objectMapper.readValue(content, Videos.class);
                    handler.onSuccess(value.getVideos().size(), value.getVideos());
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }

    /**
     * Returns a list of {@link Video}'s from channels that the authenticated user is following.
     * Authenticated, required scope: {@link Scopes#USER_READ}
     *
     * @param handler the response handler
     */
    public void getFollowed(final VideosResponseHandler handler) {
        getFollowed(new RequestParams(), handler);
    }
}
