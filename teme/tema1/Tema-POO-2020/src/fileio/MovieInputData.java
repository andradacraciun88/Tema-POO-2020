package fileio;

import java.util.ArrayList;

/**
 * Information about a movie, retrieved from parsing the input test files
 * <p>
 * DO NOT MODIFY
 */
public final class MovieInputData extends ShowInput {
    /**
     * Duration in minutes of a season
     */
    private final int duration;

    private int numberViews;

    /**
     *
     * @param numberViews
     */
    public void setNumberViews(final int numberViews) {
        this.numberViews = numberViews;
    }

    public int getNumberViews() {
        return numberViews;
    }

    private int numberFavorite;

    /**
     *
     * @param numberFavorite
     */
    public void setNumberFavorite(final int numberFavorite) {
        this.numberFavorite = numberFavorite;
    }
    public int getNumberFavorite() {
        return numberFavorite;
    }

    public MovieInputData(final String title, final ArrayList<String> cast,
                          final ArrayList<String> genres, final int year,
                          final int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "MovieInputData{" + "title= "
                + super.getTitle() + "year= "
                + super.getYear() + "duration= "
                + duration + "cast {"
                + super.getCast() + " }\n"
                + "genres {" + super.getGenres() + " }\n ";
    }
    // un array pt fiecare film in care sa i salvez ratingurile de la fiecare utilizator
    // fac media ratingurilor pe care le au dat diversi utilizatori

    public ArrayList<Double> getWhatRatingMovie() {
        return whatRatingMovie;
    }

    private final ArrayList<Double> whatRatingMovie = new ArrayList<>();

    public double getRatingMovie() {
        return medieRatingMovie();
    }

    /**
     *
     * @return med of a movie
     */
    public double medieRatingMovie() {
        double sumaMovie = 0;
        double medieMovie;
        if (whatRatingMovie.size() == 0) {
            return 0;
        }
        for (Double aDouble : whatRatingMovie) {
            /* store in sumaMovie the sum of every rating given to the movie */
            sumaMovie += aDouble;
        }
        /* store in medieMovie the final rating of a movie */
        medieMovie = sumaMovie / whatRatingMovie.size();
        return medieMovie;
    }
}
