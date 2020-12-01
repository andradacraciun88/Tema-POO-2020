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

    //numarul de ratinguri pe care le au dat
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
    //pentru fiecare utilizator am o lista cu filmele preferate
    private final ArrayList<String> favoriteMovies;

    // voi crea un map pentru a retine toate filmele carora utilizatorul le a dat rating
    private final HashMap<String, ArrayList<Double>>
            ratedMovie = new HashMap<String, ArrayList<Double>>();


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
            // daca gasesc filmul in istoric
            if (history.containsKey(movie)) {
                //ma uit si in favorite sa vad daca e
                if (favoriteMovies.contains(movie)) {
                        return "error -> " + movie + " is already in favourite list";
                }
                //il adaug la favorite
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
        // daca se afla in lista de vizualizate
        if (history.containsKey(video)) {
            history.put(video, history.get(video) + 1);
        }
        //daca nu se afla in lista de vizualiazare, o adaug
        history.put(video, 0);
        return "success -> " + video + " was viewed with total views of 1";
    }

    /**
     * @param username
     * @param movie
     * @param numberSeasons
     * @param rating
     * @param movieObj
     * @return
     */
    public String addRatingMovie(final String username,
                                 final String movie, final int numberSeasons,
                                 final double rating, final MovieInputData movieObj) {
        // daca l a vazut
        if (history.containsKey(movie)) {
            // daca i a mai dat rating, il caut prin Hashmap
            if (ratedMovie.containsKey(movie)) {
                return "error -> " + movie + " has been already rated";
            }
            //daca nu i a mai dat rating il adaug in Hashmap
            ArrayList<Double> ratingList = new ArrayList<>();
            ratingList.add(rating);
            ratedMovie.put(movie, ratingList);
            //ot obiectul movie ii aplic metoda de bagare in lista de ratinguri
            //media unui film
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
        // daca l a vazut
        if (history.containsKey(movie)) {
            // daca i a mai dat rating, il caut prin Hashmap
            if (ratedMovie.containsKey(movie)) {
                if (ratedMovie.get(movie).contains((numberSeasons))) {
                    return "error -> " + movie + " has been already rated";
                }
                // nu a dat rating inca
                ratedMovie.get(movie).add(rating);
                serialObj.getSeasons().get(numberSeasons - 1).getRatings().add(rating);
                numberRatings++;
                return "success -> " + movie + " was rated with " + rating + " by " + username;
            }
            //nu cont cheia
            ArrayList<Double> ratingList = new ArrayList<>();
            ratingList.add(rating);
            ratedMovie.put(movie, ratingList);
            // am ratingul pt fiecare sezon
            serialObj.getSeasons().get(numberSeasons - 1).getRatings().add(rating);
            numberRatings++;
            return "success -> " + movie + " was rated with " + rating + " by " + username;
        }
        return "error -> " + movie + " is not seen";
    }

}

