package fileio;

import entertainment.Season;
import java.util.ArrayList;

/**
 * Information about a tv show, retrieved from parsing the input test files
 * <p>
 * DO NOT MODIFY
 */
public final class SerialInputData extends ShowInput {
    /**
     * Number of seasons
     */
    private final int numberOfSeasons;

    private int numberViewsSerial;

    public void setNumberViewsSerial(final int numberViewsSerial) {
        this.numberViewsSerial = numberViewsSerial;
    }

    public int getNumberViewsSerial() {
        return numberViewsSerial;
    }

    private int numberFavoriteSerial;

    public int getNumberFavoriteSerial() {
        return numberFavoriteSerial;
    }

    public void setNumberFavoriteSerial(final int numberFavoriteSerial) {
        this.numberFavoriteSerial = numberFavoriteSerial;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    /**
     * Season list
     */
    private final ArrayList<Season> seasons;

    public SerialInputData(final String title, final ArrayList<String> cast,
                           final ArrayList<String> genres,
                           final int numberOfSeasons, final ArrayList<Season> seasons,
                           final int year) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }

    public int getNumberSeason() {
        return numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    @Override
    public String toString() {
        return "SerialInputData{" + " title= "
                + super.getTitle() + " " + " year= "
                + super.getYear() + " cast {"
                + super.getCast() + " }\n" + " genres {"
                + super.getGenres() + " }\n "
                + " numberSeason= " + numberOfSeasons
                + ", seasons=" + seasons + "\n\n" + '}';
    }
    // ratingurile se dau pentru fiecare sezon in parte

    public ArrayList<Double> getWhatRatingSerial() {
        return whatRatingSerial;
    }

    private ArrayList<Double> whatRatingSerial = new ArrayList<>();

    public double getRatingSerial() {
        return medieRatingSerial();
    }

    /**
     * @return
     */
    public double medieRatingSerial() {
        if (seasons.size() == 0) {
            return 0;
        }
        double sum = 0;
        double med = 0;
        // ratingurile de la un singur sezon
        for (int j = 0; j < seasons.size(); j++) {
            // sezonul curent din for
             Season s = seasons.get(j);
             // iau media ratingrilor dintr un sezon
             sum += s.getRatingOneSeason();
        }
        med = sum / seasons.size();
        return med;
    }

    /**
     * @return
     */
    public int durationSerial() {
        int sumDurationSerial = 0;
        for (int i = 0; i < numberOfSeasons; i++) {
            //pentru fiecare sezon iau durata
            sumDurationSerial += seasons.get(i).getDuration();
        }
        return sumDurationSerial;
    }

}
