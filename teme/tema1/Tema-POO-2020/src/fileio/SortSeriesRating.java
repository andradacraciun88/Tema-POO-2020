package fileio;

import java.util.Comparator;

public final class SortSeriesRating implements Comparator<SerialInputData> {

    @Override
    public int compare(final SerialInputData o1, final SerialInputData o2) {
        if (o1.getRatingSerial() > o2.getRatingSerial()) {
            return 1;
        }
        if (o1.getRatingSerial() < o2.getRatingSerial()) {
            return -1;
        }
        return o1.getTitle().compareTo(o2.getTitle());
    }
}
