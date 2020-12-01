package fileio;

import java.util.Comparator;

final public class SortMoviesDuration implements Comparator<MovieInputData> {
    @Override
    public int compare(final MovieInputData o1, final MovieInputData o2) {
        if (o1.getDuration() > o2.getDuration()) {
            return 1;
        }
        if (o1.getDuration() < o2.getDuration()) {
            return -1;
        }
        return o1.getTitle().compareTo(o2.getTitle());
    }
}
