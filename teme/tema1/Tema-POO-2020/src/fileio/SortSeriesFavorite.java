package fileio;

import java.util.Comparator;

final public class SortSeriesFavorite implements Comparator<SerialInputData> {
    @Override
    public int compare(final SerialInputData o1, final SerialInputData o2) {
        if (o1.getNumberFavoriteSerial() > o2.getNumberFavoriteSerial()) {
            return 1;
        }
        if (o1.getNumberFavoriteSerial() < o2.getNumberFavoriteSerial()) {
            return -1;
        }
        return o1.getTitle().compareTo(o2.getTitle());

    }
}
