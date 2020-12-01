package fileio;

import java.util.Comparator;

public final class SortMoviesFavorite implements Comparator<MovieInputData> {
    @Override
    public int compare(final MovieInputData o1, final MovieInputData o2) {
        if (o1.getNumberFavorite() > o2.getNumberFavorite()) {
            return 1;
        }
        if (o1.getNumberFavorite() < o2.getNumberFavorite()) {
            return -1;
        }
        return o1.getTitle().compareTo(o2.getTitle());
    }
}
