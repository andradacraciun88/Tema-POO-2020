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
    private double ratingMovie;

    private ArrayList<Double> whatRatingMovie = new ArrayList<>();

    public double getRatingMovie() {
        return medieRatingMovie();
    }

    /**
     *
     * @return
     */
    public double medieRatingMovie() {
        double sumaMovie = 0;
        double medieMovie;
        if (whatRatingMovie.size() == 0) {
            return 0;
        }
        for (int i = 0; i < whatRatingMovie.size(); i++) {
            sumaMovie += whatRatingMovie.get(i);
        }
        medieMovie = sumaMovie / whatRatingMovie.size();
        this.ratingMovie = medieMovie;
        return medieMovie;
    }
    //caut filmul meu in listele de favorite alea utilizatorilor

}
