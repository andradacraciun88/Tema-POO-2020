package fileio;

import java.util.Comparator;

public final class SortRatingsUsers implements Comparator<UserInputData> {
    @Override
    public int compare(final UserInputData o1, final UserInputData o2) {
        if (o1.getNumberRatings() > o2.getNumberRatings()) {
            return 1;
        }
        if (o1.getNumberRatings() < o2.getNumberRatings()) {
            return -1;
        }
        return o1.getUsername().compareTo(o2.getUsername());
    }
}
