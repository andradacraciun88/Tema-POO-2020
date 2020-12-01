package main;

import actor.ActorsAwards;
import checker.Checker;
import checker.Checkstyle;
import common.Constants;
import fileio.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getAbsolutePath(), filepath);
            }
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();

        //TODO add here the entry point to your implementation

        for (ActionInputData action : input.getCommands()) {
            /* looking in input after ActionType */
            if (action.getActionType().equals("command") && action.getType().equals("favorite")) {
                /* iterate from input users */
                for (int i = 0; i < input.getUsers().size(); i++) {
                    if (input.getUsers().get(i).getUsername().equals(action.getUsername())) {
                        /* add the movie to favorite */
                        String message = input.getUsers()
                                .get(i).addFavoriteMovie(action.getTitle());
                        JSONObject jsonObj = fileWriter.
                                writeFile(action.getActionId(), " ", message);
                        arrayResult.add(jsonObj);
                    }
                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("command") && action.getType().equals("view")) {
                for (int i = 0; i < input.getUsers().size(); i++) {
                    /* if I found the user from the input users */
                    if (input.getUsers().get(i).getUsername().equals(action.getUsername())) {
                        /* marked as viewd */
                        String message = input.getUsers().get(i).markAsViewed(action.getTitle());
                        JSONObject jsonObj = fileWriter.
                                writeFile(action.getActionId(), " ", message);
                        arrayResult.add(jsonObj);
                    }
                }
            }
        }
        // daca e film
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("command") && action.getType().equals("rating")) {
                for (int i = 0; i < input.getUsers().size(); i++) {
                    if (input.getUsers().get(i).getUsername().
                            equals(action.getUsername())) {
                        for (MovieInputData movie : input.getMovies()) {
                            if (movie.getTitle().equals(action.getTitle())) {
                                /* add rating for every user */
                                String message = input.getUsers().get(i).
                                        addRatingMovie(action.getUsername(), movie.
                                                getTitle(), action.getGrade(), movie);
                                JSONObject jsonObj = fileWriter.writeFile(action.
                                        getActionId(), "", message);
                                arrayResult.add(jsonObj);
                            }
                        }
                    }
                }
            }
        }
        // daca e serial
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("command") && action.
                    getType().equals("rating")) {
                for (int i = 0; i < input.getUsers().size(); i++) {
                    if (input.getUsers().get(i).getUsername().
                            equals(action.getUsername())) {
                        for (SerialInputData serial : input.getSerials()) {
                            if (serial.getTitle().equals(action.getTitle())) {
                                String message = input.getUsers().get(i).
                                        addRatingSerial(action.getUsername(), serial.
                                                getTitle(), action.getSeasonNumber(), action.
                                                getGrade(), serial);
                                JSONObject jsonObj = fileWriter.
                                        writeFile(action.getActionId(), " ", message);
                                arrayResult.add(jsonObj);
                            }
                        }
                    }

                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.getObjectType().
                    equals("actors") && action.getCriteria().equals("average")) {
                /* iterate in actor list */
                for (ActorInputData actor : input.getActors()) {
                    double med;
                    double sum = 0;
                    int number = 0;
                    /* iterate in filmography of every actor */
                    for (String film : actor.getFilmography()) {
                        for (MovieInputData movie : input.getMovies()) {
                            /* if the movie is in filmography add it s rating to sum */
                            if (movie.getTitle().equals(film) && movie.
                                    medieRatingMovie() != 0) {
                                sum += movie.medieRatingMovie();
                                number++;
                            }
                        }
                        for (SerialInputData serial : input.getSerials()) {
                            /* if the show is in filmography add it s rating to sum */
                            if (serial.getTitle().equals(film) && serial.
                                    medieRatingSerial() != 0) {
                                sum += serial.medieRatingSerial();
                                number++;
                            }
                        }
                    }
                    if (number != 0) {
                        /* med of ratings for an actor */
                        med = sum / number;
                    } else {
                        med = 0;
                    }
                        /* add the med to the list */
                        actor.setMed(med);
                }
                /* sort actors after their med of rating*/
                /* the med must be different of 0*/
                List<ActorInputData> actors = input.getActors()
                        .stream()
                        .filter(c -> c.getMed() > 0).sorted(new Sort()).
                                collect(Collectors.toList());

                if (action.getSortType().equals("desc")) {
                    Collections.reverse(actors);
                }
                StringBuilder message = new StringBuilder("Query result: [");
                if (actors.size() < action.getNumber() && actors.size() != 0) {
                    for (int j = 0; j < actors.size() - 1; j++) {
                        message.append(actors.get(j).getName()).append(", ");
                    }
                    message.append(actors.get(actors.size() - 1).getName());

                }
                if (actors.size() > action.getNumber()) {
                    for (int j = 0; j < action.getNumber() - 1; j++) {
                        message.append(actors.get(j).getName()).append(", ");
                    }
                    message.append(actors.get(action.getNumber() - 1).getName());
                }
                message.append("]");

                JSONObject jsonObj = fileWriter.writeFile(action.
                        getActionId(), "", message.toString());
                arrayResult.add(jsonObj);
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.getObjectType().
                    equals("users") && action.getCriteria().
                    equals("num_ratings")) {
                List<UserInputData> users = input.getUsers()
                        .stream()
                        .filter(c -> c.getNumberRatings() > 0).sorted(new SortRatingsUsers()).
                                collect(Collectors.toList());
                if (action.getSortType().equals("desc")) {
                    Collections.reverse(users);
                }
                StringBuilder message = new StringBuilder("Query result: [");
                if (users.size() < action.getNumber() && users.size() != 0) {
                    for (int j = 0; j < users.size() - 1; j++) {
                        message.append(users.get(j).getUsername()).append(", ");
                    }
                    message.append(users.get(users.size() - 1).getUsername());

                }
                if (users.size() > action.getNumber()) {
                    for (int j = 0; j < action.getNumber() - 1; j++) {
                        message.append(users.get(j).getUsername()).append(", ");
                    }
                    message.append(users.get(action.getNumber() - 1).getUsername());
                }
                message.append("]");

                JSONObject jsonObj = fileWriter.
                        writeFile(action.getActionId(), "", message.toString());
                arrayResult.add(jsonObj);
            }
        }

        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.getObjectType().
                    equals("movies") && action.getCriteria().equals("longest")) {
                if (action.getFilters().get(0).get(0) != null && action.
                        getFilters().get(1).get(0) != null) {
                    List<MovieInputData> movies = input.getMovies()
                            .stream()
                            .filter(c -> c.getDuration() > 0 && c.
                                    getYear() == Integer.parseInt(action.getFilters().
                                    get(0).get(0)) && c.
                                    getGenres().contains((action.getFilters().get(1).get(0)))).
                                    sorted(new SortMoviesDuration()).collect(Collectors.toList());

                    if (action.getSortType().equals("desc")) {
                        Collections.reverse(movies);
                    }
                    StringBuilder message = new StringBuilder("Query result: [");
                    if (movies.size() < action.getNumber() && movies.size() != 0) {
                        for (int j = 0; j < movies.size() - 1; j++) {
                            message.append(movies.get(j).getTitle()).append(", ");

                        }
                        message.append(movies.get(movies.size() - 1).getTitle());

                    }
                    if (movies.size() > action.getNumber()) {

                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message.append(movies.get(j).getTitle()).append(", ");
                        }
                        message.append(movies.get(action.getNumber() - 1).getTitle());
                    }
                    message.append("]");

                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message.toString());
                    arrayResult.add(jsonObj);
                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.
                    getObjectType().equals("shows") && action.
                    getCriteria().equals("longest")) {
                if (action.getFilters().get(0).get(0) != null && action.
                        getFilters().get(1).get(0) != null) {
                    List<SerialInputData> serials = input.getSerials()
                            .stream()
                            /* sort after filters needed*/
                            .filter(c -> c.durationSerial() > 0 && c.
                                    getYear() == Integer.parseInt(action.
                                    getFilters().get(0).get(0)) && c.
                                    getGenres().contains((action.getFilters().get(1).get(0)))).
                                    sorted(new SortSeriesDuration()).collect(Collectors.toList());

                    if (action.getSortType().equals("desc")) {
                        Collections.reverse(serials);
                    }
                    StringBuilder message = new StringBuilder("Query result: [");
                    if (serials.size() < action.getNumber() && serials.size() != 0) {
                        for (int j = 0; j < serials.size() - 1; j++) {
                            message.append(serials.get(j).getTitle()).append(", ");

                        }
                        message.append(serials.get(serials.size() - 1).getTitle());

                    }
                    if (serials.size() > action.getNumber()) {

                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message.append(serials.get(j).getTitle()).append(", ");
                        }
                        message.append(serials.get(action.getNumber() - 1).getTitle());
                    }
                    message.append("]");

                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message.toString());
                    arrayResult.add(jsonObj);
                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.
                    getObjectType().equals("movies") && action.getCriteria().
                    equals("ratings")) {
                if (action.getFilters().get(0).get(0) != null && action.
                        getFilters().get(1).get(0) != null) {
                    List<MovieInputData> movies = input.getMovies()
                            .stream()
                            .filter(c -> c.getRatingMovie() > 0 && c.
                                    getYear() == Integer.parseInt(action.
                                    getFilters().get(0).get(0)) && c.
                                    getGenres().contains((action.getFilters().get(1).get(0)))).
                                    sorted(new SortMoviesRating()).collect(Collectors.toList());

                    if (action.getSortType().equals("asc")) {
                        Collections.reverse(movies);
                    }
                    StringBuilder message = new StringBuilder("Query result: [");
                    if (movies.size() < action.getNumber() && movies.size() != 0) {
                        for (int j = 0; j < movies.size() - 1; j++) {
                            message.append(movies.get(j).getTitle()).append(", ");
                        }
                        message.append(movies.get(movies.size() - 1).getTitle());

                    }
                    if (movies.size() > action.getNumber()) {
                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message.append(movies.get(j).getTitle()).append(", ");
                        }
                        message.append(movies.get(action.getNumber() - 1).getTitle());
                    }
                    message.append("]");

                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message.toString());
                    arrayResult.add(jsonObj);
                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.
                    getObjectType().equals("shows") && action.
                    getCriteria().equals("ratings")) {
                if (action.getFilters().get(0).get(0) != null && action.
                        getFilters().get(1).get(0) != null) {
                    List<SerialInputData> serials = input.getSerials()
                            .stream()
                            .filter(c -> c.getRatingSerial() > 0 && c.
                                    getYear() == Integer.parseInt(action.
                                    getFilters().get(0).get(0)) && c.
                                    getGenres().contains((action.getFilters().get(1).get(0)))).
                                    sorted(new SortSeriesRating()).collect(Collectors.toList());

                    if (action.getSortType().equals("desc")) {
                        Collections.reverse(serials);
                    }
                    StringBuilder message = new StringBuilder("Query result: [");
                    if (serials.size() < action.getNumber() && serials.size() != 0) {
                        for (int j = 0; j < serials.size() - 1; j++) {
                            message.append(serials.get(j).getTitle()).append(", ");
                        }
                        message.append(serials.get(serials.size() - 1).getTitle());

                    }
                    if (serials.size() > action.getNumber()) {
                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message.append(serials.get(j).getTitle()).append(", ");
                        }
                        message.append(serials.get(action.getNumber() - 1).getTitle());
                    }
                    message.append("]");

                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message.toString());
                    arrayResult.add(jsonObj);
                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.getObjectType().
                    equals("actors") && action.
                    getCriteria().equals("awards")) {
                /* iterate in actors and see if they have every award needed */
                ArrayList<ActorInputData> actor1 = new ArrayList<>();
                for (ActorInputData actor : input.getActors()) {
                    int ok = 0;
                    for (int i = 0; i < action.getFilters().get(action.getFilters().
                            size() - 1).size(); i++) {
                        /* if the actor doesn t have one required award */
                        if (!actor.getAwards().containsKey(ActorsAwards.valueOf(action.
                                getFilters().get(action.getFilters().
                                size() - 1).get(i)))) {
                            ok = 1;
                        }
                    }
                    if (ok == 0) {
                        /* if the actor has every award needed */
                        /* add the actor in the array of actors */
                        actor1.add(actor);
                    }
                }
                actor1.sort(new SortActors());
                /* sort the list afer specific criteria */
                StringBuilder message = new StringBuilder("Query result: [");
                if (actor1.size() < action.getNumber() && actor1.size() != 0) {
                    for (int j = 0; j < actor1.size() - 1; j++) {
                        message.append(actor1.get(j).getName()).append(", ");
                    }
                    message.append(actor1.get(actor1.size() - 1).getName());
                }
                if (actor1.size() > action.getNumber()) {
                    for (int j = 0; j < action.getNumber() - 1; j++) {
                        message.append(actor1.get(j).getName()).append(", ");
                    }
                    message.append(actor1.get(action.getNumber() - 1).getName());
                }
                message.append("]");

                JSONObject jsonObj = fileWriter.writeFile(action.
                        getActionId(), "", message.toString());
                arrayResult.add(jsonObj);
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.getObjectType().
                    equals("actors") && action.
                    getCriteria().equals("filter_description")) {
                ArrayList<ActorInputData> actor1 = new ArrayList<>();
                for (ActorInputData actor : input.getActors()) {
                    int ok = 0;
                    if (action.getFilters().get(action.getFilters().size() - 1) != null) {
                        /* iterate in actors to see if they have every keyword */
                        for (int i = 0; i < action.getFilters().get(action.getFilters().
                                size() - 1).size(); i++) {
                            /* if they miss one keyword */
                            if (!actor.getCareerDescription().
                                    contains(action.getFilters().get(action.getFilters().
                                            size() - 1).get(i))) {
                                ok = 1;
                                break;
                            }
                        }
                        /* has every keyword */
                        if (ok == 0) {
                            actor1.add(actor);
                        }
                    }
                }
                /* sort the list */
                actor1.sort(new SortActorsDescription());
                StringBuilder message = new StringBuilder("Query result: [");
                if (actor1.size() < action.getNumber() && actor1.size() != 0) {
                    for (int j = 0; j < actor1.size() - 1; j++) {
                        message.append(actor1.get(j).getName()).append(", ");
                    }
                    message.append(actor1.get(actor1.size() - 1).getName());
                }
                if (actor1.size() > action.getNumber()) {
                    for (int j = 0; j < action.getNumber() - 1; j++) {
                        message.append(actor1.get(j).getName()).append(", ");
                    }
                    message.append(actor1.get(action.getNumber() - 1).getName());
                }
                message.append("]");

                JSONObject jsonObj = fileWriter.writeFile(action.
                        getActionId(), "", message.toString());
                arrayResult.add(jsonObj);
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.
                    getObjectType().equals("movies") && action.
                    getCriteria().equals("favorite")) {
                if (action.getFilters().get(0).get(0) != null && action.
                        getFilters().get(1).get(0) != null) {
                    // percurg utilizatorii
                    for (UserInputData user : input.getUsers()) {
                        for (MovieInputData movie : input.getMovies()) {
                            // daca filmul se afla in lista de favorite ale userului
                            if (user.getFavoriteMovies().contains(movie.getTitle())) {
                                // incrementez nr de favorite
                                movie.setNumberFavorite(movie.getNumberFavorite() + 1);
                            }
                        }
                    }
                    List<MovieInputData> movies = input.getMovies()
                            .stream()
                            .filter(c -> c.
                                    getYear() == Integer.parseInt(action.
                                    getFilters().get(0).get(0)) && c.
                                    getGenres().contains((action.getFilters().
                                    get(1).get(0)))).sorted(new SortMoviesFavorite()).
                                    collect(Collectors.toList());

                    if (action.getSortType().equals("asc")) {
                        Collections.reverse(movies);
                    }
                    StringBuilder message = new StringBuilder("Query result: [");
                    if (movies.size() < action.getNumber() && movies.size() != 0) {
                        for (int j = 0; j < movies.size() - 1; j++) {
                            message.append(movies.get(j).getTitle()).append(", ");
                        }
                        message.append(movies.get(movies.size() - 1).getTitle());
                    }
                    if (movies.size() > action.getNumber()) {
                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message.append(movies.get(j).getTitle()).append(", ");
                        }
                        message.append(movies.get(action.getNumber() - 1).getTitle());
                    }
                    message.append("]");
                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message.toString());
                    arrayResult.add(jsonObj);
                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.
                    getObjectType().equals("shows") && action.
                    getCriteria().equals("favorite")) {
                if (action.getFilters().get(0).get(0) != null && action.
                        getFilters().get(1).get(0) != null) {
                    for (int i = 0; i < input.getUsers().size(); i++) {
                        for (SerialInputData serial : input.getSerials()) {
                            /* if the movie is in favorite list */
                            if (input.getUsers().get(i).getFavoriteMovies().
                                    contains(serial.getTitle())) {
                                /* increment the number of favorite */
                                serial.setNumberFavoriteSerial(serial.
                                        getNumberFavoriteSerial() + 1);
                            }
                        }
                    }
                    List<SerialInputData> serial = input.getSerials()
                            .stream()
                            /* sort after needed filters */
                            .filter(c -> c.getNumberFavoriteSerial() > 0 && c.
                                    getYear() == Integer.parseInt(action.
                                    getFilters().get(0).get(0)) && c.
                                    getGenres().contains((action.getFilters().get(1).get(0)))).
                                    sorted(new SortSeriesFavorite()).collect(Collectors.toList());

                    if (action.getSortType().equals("asc")) {
                        Collections.reverse(serial);
                    }
                    StringBuilder message = new StringBuilder("Query result: [");
                    if (serial.size() < action.getNumber() && serial.size() != 0) {
                        for (int j = 0; j < serial.size() - 1; j++) {
                            message.append(serial.get(j).getTitle()).append(", ");
                        }
                        message.append(serial.get(serial.size() - 1).getTitle());
                    }
                    if (serial.size() > action.getNumber()) {
                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message.append(serial.get(j).getTitle()).append(", ");
                        }
                        message.append(serial.get(action.getNumber() - 1).getTitle());
                    }
                    message.append("]");
                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message.toString());
                    arrayResult.add(jsonObj);
                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.
                    getObjectType().equals("movies") && action.
                    getCriteria().equals("most_viewed")) {
                if (action.getFilters().get(0).get(0) != null && action.
                        getFilters().get(1).get(0) != null) {
                    for (int i = 0; i < input.getUsers().size(); i++) {
                        /* iterate from movie to see how many times it was seen */
                        for (MovieInputData movie : input.getMovies()) {
                            /* if it was seen add in HashMap to the value of the movie
                             how many times it was viewed */
                            if (input.getUsers().get(i).getHistory().
                                    containsKey(movie.getTitle())) {
                                movie.setNumberViews(movie.getNumberViews() + input.
                                        getUsers().get(i).getHistory().
                                        get(movie.getTitle()));
                            }
                        }
                    }
                    List<MovieInputData> movies = input.getMovies()
                            .stream()
                            /* sort after needed filters */
                            .filter(c -> c.getNumberViews() > 0 && c.
                                    getYear() == Integer.parseInt(action.
                                    getFilters().get(0).get(0)) && c.
                                    getGenres().contains((action.getFilters().get(1).get(0)))).
                                    sorted(new SortMoviesMostViewed()).collect(Collectors.toList());

                    if (action.getSortType().equals("desc")) {
                        Collections.reverse(movies);
                    }
                    StringBuilder message = new StringBuilder("Query result: [");
                    if (movies.size() < action.getNumber() && movies.size() != 0) {
                        for (int j = 0; j < movies.size() - 1; j++) {
                            message.append(movies.get(j).getTitle()).append(", ");
                        }
                        message.append(movies.get(movies.size() - 1).getTitle());
                    }
                    if (movies.size() > action.getNumber()) {
                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message.append(movies.get(j).getTitle()).append(", ");
                        }
                        message.append(movies.get(action.getNumber() - 1).getTitle());
                    }
                    message.append("]");
                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message.toString());
                    arrayResult.add(jsonObj);
                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.
                    getObjectType().equals("shows") && action.getCriteria().
                    equals("most_viewed")) {
                if (action.getFilters().get(0).get(0) != null && action.
                        getFilters().get(1).get(0) != null) {
                    /* iterate from users */
                    for (int i = 0; i < input.getUsers().size(); i++) {
                        /* iterate from show to see how many times it was seen */
                        for (SerialInputData serial : input.getSerials()) {
                            /* if it was seen add in HashMap to the value of the show
                             how many times it was viewed */
                            if (input.getUsers().get(i).getHistory().
                                    containsKey(serial.getTitle())) {
                                serial.setNumberViewsSerial(serial.
                                        getNumberViewsSerial() + input.getUsers().get(i).
                                        getHistory().get(serial.getTitle()));
                            }
                        }
                    }
                    List<SerialInputData> movies = input.getSerials()
                            .stream()
                            .filter(c -> c.getNumberViewsSerial() > 0 && c.
                                    getYear() == Integer.parseInt(action.
                                    getFilters().get(0).get(0)) && c.
                                    getGenres().contains((action.getFilters().get(1).get(0)))).
                                    sorted(new SortSeriesMostViewed()).collect(Collectors.toList());

                    if (action.getSortType().equals("desc")) {
                        Collections.reverse(movies);
                    }
                    StringBuilder message = new StringBuilder("Query result: [");
                    if (movies.size() < action.getNumber() && movies.size() != 0) {
                        for (int j = 0; j < movies.size() - 1; j++) {
                            message.append(movies.get(j).getTitle()).append(", ");
                        }
                        message.append(movies.get(movies.size() - 1).getTitle());
                    }
                    if (movies.size() > action.getNumber()) {
                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message.append(movies.get(j).getTitle()).append(", ");
                        }
                        message.append(movies.get(action.getNumber() - 1).getTitle());
                    }
                    message.append("]");
                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message.toString());
                    arrayResult.add(jsonObj);
                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("recommendation") && action.
                    getType().equals("standard")) {
                for (int i = 0; i < input.getUsers().size(); i++) {
                    int ok = 0;
                    /* find the user */
                    if (input.getUsers().get(i).getUsername().
                            equals(action.getUsername())) {
                        /* iterate from the list of the user */
                        for (MovieInputData movie : input.getMovies()) {
                            /* if it s the first movie unseen */
                            if (!input.getUsers().get(i).getHistory().
                                    containsKey(movie.getTitle()) && ok == 0) {
                                ok = 1;
                                String message = "StandardRecommendation result: " + movie.
                                        getTitle();
                                JSONObject jsonObj = fileWriter.
                                        writeFile(action.getActionId(), "", message);
                                arrayResult.add(jsonObj);
                            }
                        }
                    }
                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("recommendation") && action.
                    getType().equals("best_unseen")) {
                ArrayList<MovieInputData> movies1 = new ArrayList<>();
                for (int i = 0; i < input.getUsers().size(); i++) {
                    if (input.getUsers().get(i).getUsername().
                            equals(action.getUsername())) {
                        for (MovieInputData movie : input.getMovies()) {
                            /* iterate from unseen movies */
                            if (!input.getUsers().get(i).getHistory().
                                    containsKey(movie.getTitle())) {
                                /* if it s unseen add to the list */
                                movies1.add(movie);
                            }
                        }
                    }
                }
                movies1.sort(new SortMoviesRating());
                if (movies1.size() != 0) {
                    String message = "BestRatedUnseenRecommendation result: " + movies1.
                            get(1).getTitle();
                    JSONObject jsonObj = fileWriter.
                            writeFile(action.getActionId(), "", message);
                    arrayResult.add(jsonObj);
                }
            }
        }

        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("recommendation") && action.
                    getType().equals("favorite")) {
                /* sort the list */
                List<MovieInputData> movies = input.getMovies();
                movies.sort(new SortMoviesFavorite());
                for (int i = 0; i < input.getUsers().size(); i++) {
                    for (MovieInputData movie : movies) {
                        int ok = 0;
                        if (input.getUsers().get(i).getUsername().
                                equals(action.getUsername())) {
                            /* get the first movie unseen from the sorted list */
                            if (!input.getUsers().get(i).getHistory().
                                    containsKey(movie.getTitle())) {
                                ok = 1;
                            }
                            if (ok == 1) {
                                break;
                            }
                        }
                    }
                }
                if (movies.size() != 0) {
                    String message = "FavoriteRecommendation result: " + movies.
                            get(1).getTitle();
                    JSONObject jsonObj = fileWriter.
                            writeFile(action.getActionId(), "", message);
                    arrayResult.add(jsonObj);
                }
            }
        }
        fileWriter.closeJSON(arrayResult);
    }
}
