package fileio;

import java.util.Comparator;

final public class SortMoviesRating implements Comparator<MovieInputData> {
    @Override
    public int compare(final MovieInputData o1, final MovieInputData o2) {
        if (o1.getRatingMovie() > o2.getRatingMovie()) {
            return 1;
        }
        if (o1.getRatingMovie() < o2.getRatingMovie()) {
            return -1;
        }
        return o1.getTitle().compareTo(o2.getTitle());
    }
}
