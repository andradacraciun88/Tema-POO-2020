package fileio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Information about an user, retrieved from parsing the input test files
 * <p>
 * DO NOT MODIFY
 */
public final class UserInputData {
    /**
     * User's username
     */
    private final String username;
    /**
     * Subscription Type
     */
    private final String subscriptionType;

    /* the number of ratings */
    private int numberRatings;

    public void setNumberRatings(final int numberRatings) {
        this.numberRatings = numberRatings;
    }

    public int getNumberRatings() {
        return numberRatings;
    }

    /**
     * The history of the movies seen
     */
    private final Map<String, Integer> history;
    /**
     * Movies added to favorites
     */
    private final ArrayList<String> favoriteMovies;

    /* a HaspMap that contains the name of the movie and every rate that it has
     from every user */
    private final HashMap<String, ArrayList<Double>>
            ratedMovie = new HashMap<>();


    public UserInputData(final String username, final String subscriptionType,
                         final Map<String, Integer> history,
                         final ArrayList<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.favoriteMovies = favoriteMovies;
        this.history = history;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }


    @Override
    public String toString() {
        return "UserInputData{" + "username='"
                + username + '\'' + ", subscriptionType='"
                + subscriptionType + '\'' + ", history="
                + history + ", favoriteMovies="
                + favoriteMovies + '}';
    }
    // ADD FAVOURITE

    /**
     * @param movie
     * @return
     */
    public String addFavoriteMovie(final String movie) {
            /* find the movie from history */
            if (history.containsKey(movie)) {
                /* if the movie is already in favorite list */
                if (favoriteMovies.contains(movie)) {
                        return "error -> " + movie + " is already in favourite list";
                }
                /* add to favorite */
                favoriteMovies.add(movie);
                return "success -> " + movie + " was added as favourite";
            } else {
                return "error -> " + movie + " is not seen";
            }
    }

    /**
     * @param video
     * @return
     */
    public String markAsViewed(final String video) {
        /* see if the movie is already seen and incresase the numbers of views if it is */
        if (history.containsKey(video)) {
            history.put(video, history.get(video) + 1);
        }
        return "success -> " + video + " was viewed with total views of 1";
    }

    /**
     * @param username
     * @param movie
     * @param rating
     * @param movieObj
     * @return
     */
    public String addRatingMovie(final String username,
                                 final String movie,
                                 final double rating, final MovieInputData movieObj) {
        if (history.containsKey(movie)) {
            /* if the movie was already rated */
            if (ratedMovie.containsKey(movie)) {
                return "error -> " + movie + " has been already rated";
            }
            /* if the movie wasn't already rated, add it to HashMap */
            ArrayList<Double> ratingList = new ArrayList<>();
            ratingList.add(rating);
            ratedMovie.put(movie, ratingList);
            /* get the rating from the movie */
            movieObj.getWhatRatingMovie().add(rating);
            numberRatings++;
            return "success -> " + movie + " was rated with " + rating + " by " + username;

        }
        return "error -> " + movie + " is not seen";
    }

    /**
     * @param username
     * @param movie
     * @param numberSeasons
     * @param rating
     * @param serialObj
     * @return
     */
    public String addRatingSerial(final String username,
                                  final String movie, final int numberSeasons,
                                  final double rating, final SerialInputData serialObj) {
        if (history.containsKey(movie)) {
            /* if the show was already rated */
            if (ratedMovie.containsKey(movie)) {
                if (ratedMovie.get(movie).contains((numberSeasons))) {
                    return "error -> " + movie + " has been already rated";
                }
                /* if the show wasn't already rated, add it to HashMap */
                ratedMovie.get(movie).add(rating);
                serialObj.getSeasons().get(numberSeasons - 1).getRatings().add(rating);
                numberRatings++;
                return "success -> " + movie + " was rated with " + rating + " by " + username;
            }
            ArrayList<Double> ratingList = new ArrayList<>();
            ratingList.add(rating);
            ratedMovie.put(movie, ratingList);
            /* get the rating from every season from the show */
            serialObj.getSeasons().get(numberSeasons - 1).getRatings().add(rating);
            numberRatings++;
            return "success -> " + movie + " was rated with " + rating + " by " + username;
        }
        return "error -> " + movie + " is not seen";
    }

}

