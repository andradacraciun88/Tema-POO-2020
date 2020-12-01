package fileio;

import java.util.Comparator;

final public class SortSeriesMostViewed implements Comparator<SerialInputData> {
    @Override
    public int compare(final SerialInputData o1, final SerialInputData o2) {
        if (o1.getNumberViewsSerial() > o2.getNumberViewsSerial()) {
            return 1;
        }
        if (o1.getNumberViewsSerial() < o2.getNumberViewsSerial()) {
            return -1;
        }
        return o1.getTitle().compareTo(o2.getTitle());
    }
}
