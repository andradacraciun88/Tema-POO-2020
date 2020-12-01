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
        //gasesc titlul filmului si userul pentru care fac modificarile

        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("command") && action.getType().equals("favorite")) {
                //pentru fiecare user din input.getUsers
                for (int i = 0; i < input.getUsers().size(); i++) {
                    // daca printre useri se gaseste cel cautat ii adaug filmul la fav
                    if (input.getUsers().get(i).getUsername().equals(action.getUsername())) {
                        // ii adaug ca favorit filmul respectiv, userului gasit si dorit
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
                //vad de care user a fost vazut
                for (int i = 0; i < input.getUsers().size(); i++) {
                    if (input.getUsers().get(i).getUsername().equals(action.getUsername())) {
                        String message = input.getUsers().get(i).markAsViewed(action.getTitle());
                        //System.out.println(message);
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
                // pentru fiecare user aplic metoda de addRating
                // parametrii user si titlul filmului
                // pentru fiecare user trebuie sa ma plimb prin lista de ratinguri
                for (int i = 0; i < input.getUsers().size(); i++) {
                    if (input.getUsers().get(i).getUsername().
                            equals(action.getUsername())) {
                        for (MovieInputData movie : input.getMovies()) {
                            if (movie.getTitle().equals(action.getTitle())) {
                                String message = input.getUsers().get(i).
                                        addRatingMovie(action.getUsername(), movie.
                                                getTitle(), 0, action.getGrade(), movie);
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
                // pentru fiecare user aplic metoda de addRating
                // parametrii user si titlul filmului
                // pentru fiecare user trebuie sa ma plimb prin lista de ratinguri
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
                // iterez prin lista de actori
                for (ActorInputData actor : input.getActors()) {
                    double med = 0;
                    double sum = 0;
                    int number = 0;
                    for (String film : actor.getFilmography()) {
                        //sunt in lista de filmografie
                        for (MovieInputData movie : input.getMovies()) {
                            if (movie.getTitle().equals(film) && movie.
                                    medieRatingMovie() != 0) {
                                sum += movie.medieRatingMovie();
                                number++;
                            }
                        }
                        for (SerialInputData serial : input.getSerials()) {
                            // daca filmografia se gasaeste in movie urile date ca
                            // input in fisierul meu\
                            if (serial.getTitle().equals(film) && serial.
                                    medieRatingSerial() != 0) {
                                sum += serial.medieRatingSerial();
                                number++;
                            }
                        }
                    }
                    if (number != 0) {
                        // media unui actor
                        med = sum / number;
                    } else {
                        med = 0;
                    }
                        // am media pt fiecare actor
                        actor.setMed(med);
                }
                //sortez in ordine crescatoare mediile actorilor
                //med dif de 0
                List<ActorInputData> actors = input.getActors()
                        .stream()
                        .filter(c -> c.getMed() > 0)
                        .collect(Collectors.toList());

                Collections.sort(actors, new Sort());
                if (action.getSortType().equals("desc")) {
                    Collections.reverse(actors);
                }
                String message = "Query result: [";
                if (actors.size() < action.getNumber() && actors.size() != 0) {
                    for (int j = 0; j < actors.size() - 1; j++) {
                        message += actors.get(j).getName() + ", ";
                    }
                    message += actors.get(actors.size() - 1).getName();

                }
                if (actors.size() > action.getNumber()) {
                    for (int j = 0; j < action.getNumber() - 1; j++) {
                        message += actors.get(j).getName() + ", ";
                    }
                    message += actors.get(action.getNumber() - 1).getName();
                }
                message += "]";

                JSONObject jsonObj = fileWriter.writeFile(action.
                        getActionId(), "", message);
                arrayResult.add(jsonObj);
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.getObjectType().
                    equals("users") && action.getCriteria().
                    equals("num_ratings")) {
                List<UserInputData> users = input.getUsers()
                        .stream()
                        .filter(c -> c.getNumberRatings() > 0)
                        .collect(Collectors.toList());
                Collections.sort(users, new SortRatingsUsers());
                if (action.getSortType().equals("desc")) {
                    Collections.reverse(users);
                }
                //System.out.println(users.size());
                String message = "Query result: [";
                if (users.size() < action.getNumber() && users.size() != 0) {
                    for (int j = 0; j < users.size() - 1; j++) {
                        message += users.get(j).getUsername() + ", ";
                    }
                    message += users.get(users.size() - 1).getUsername();

                }
                if (users.size() > action.getNumber()) {
                    for (int j = 0; j < action.getNumber() - 1; j++) {
                        message += users.get(j).getUsername() + ", ";
                    }
                    message += users.get(action.getNumber() - 1).getUsername();
                }
                message += "]";

                JSONObject jsonObj = fileWriter.writeFile(action.getActionId(), "", message);
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
                                    getGenres().contains((action.getFilters().get(1).get(0))))
                            .collect(Collectors.toList());

                    Collections.sort(movies, new SortMoviesDuration());
                    if (action.getSortType().equals("desc")) {
                        Collections.reverse(movies);
                    }
                    String message = "Query result: [";
                    //movie.size() este 2
                    if (movies.size() < action.getNumber() && movies.size() != 0) {
                        for (int j = 0; j < movies.size() - 1; j++) {
                            message += movies.get(j).getTitle() + ", ";

                            System.out.println(movies.get(j).getTitle());
                        }
                        message += movies.get(movies.size() - 1).getTitle();

                    }
                    if (movies.size() > action.getNumber()) {
                        System.out.println("andrada");

                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message += movies.get(j).getTitle() + ", ";
                            System.out.println(movies.get(j).getTitle());
                        }
                        message += movies.get(action.getNumber() - 1).getTitle();
                    }
                    message += "]";

                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message);
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
                            .filter(c -> c.durationSerial() > 0 && c.
                                    getYear() == Integer.parseInt(action.
                                    getFilters().get(0).get(0)) && c.
                                    getGenres().contains((action.getFilters().get(1).get(0))))
                            .collect(Collectors.toList());

                    Collections.sort(serials, new SortSeriesDuration());
                    if (action.getSortType().equals("desc")) {
                        Collections.reverse(serials);
                    }
                    String message = "Query result: [";
                    if (serials.size() < action.getNumber() && serials.size() != 0) {
                        for (int j = 0; j < serials.size() - 1; j++) {
                            message += serials.get(j).getTitle() + ", ";

                            System.out.println(serials.get(j).getTitle());
                        }
                        message += serials.get(serials.size() - 1).getTitle();

                    }
                    if (serials.size() > action.getNumber()) {
                        System.out.println("andrada");

                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message += serials.get(j).getTitle() + ", ";
                            System.out.println(serials.get(j).getTitle());
                        }
                        message += serials.get(action.getNumber() - 1).getTitle();
                    }
                    message += "]";

                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message);
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
                                    getGenres().contains((action.getFilters().get(1).get(0))))
                            .collect(Collectors.toList());

                    Collections.sort(movies, new SortMoviesRating());
                    if (action.getSortType().equals("asc")) {
                        Collections.reverse(movies);
                    }
                    String message = "Query result: [";
                    //movie.size() este 2
                    if (movies.size() < action.getNumber() && movies.size() != 0) {
                        for (int j = 0; j < movies.size() - 1; j++) {
                            message += movies.get(j).getTitle() + ", ";
                            System.out.println(movies.get(j).getTitle());
                        }
                        message += movies.get(movies.size() - 1).getTitle();

                    }
                    if (movies.size() > action.getNumber()) {
                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message += movies.get(j).getTitle() + ", ";
                            System.out.println(movies.get(j).getTitle());
                        }
                        message += movies.get(action.getNumber() - 1).getTitle();
                    }
                    message += "]";

                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message);
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
                                    getGenres().contains((action.getFilters().get(1).get(0))))
                            .collect(Collectors.toList());

                    Collections.sort(serials, new SortSeriesRating());
                    if (action.getSortType().equals("desc")) {
                        Collections.reverse(serials);
                    }
                    String message = "Query result: [";
                    //movie.size() este 2
                    if (serials.size() < action.getNumber() && serials.size() != 0) {
                        for (int j = 0; j < serials.size() - 1; j++) {
                            message += serials.get(j).getTitle() + ", ";
                            System.out.println(serials.get(j).getTitle());
                        }
                        message += serials.get(serials.size() - 1).getTitle();

                    }
                    if (serials.size() > action.getNumber()) {
                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message += serials.get(j).getTitle() + ", ";
                            System.out.println(serials.get(j).getTitle());
                        }
                        message += serials.get(action.getNumber() - 1).getTitle();
                    }
                    message += "]";

                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message);
                    arrayResult.add(jsonObj);
                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("query") && action.getObjectType().
                    equals("actors") && action.
                    getCriteria().equals("awards")) {
                // iau fiecare actor si verific daca are
                // premiile din awards dat ca input
                ArrayList<ActorInputData> actor1 = new ArrayList<>();
                for (ActorInputData actor : input.getActors()) {
                    int ok = 0;
                    for (int i = 0; i < action.getFilters().get(action.getFilters().
                            size() - 1).size(); i++) {
                        // daca nu contine un premiu
                        if (!actor.getAwards().containsKey(ActorsAwards.valueOf(action.
                                getFilters().get(action.getFilters().
                                size() - 1).get(i)))) {
                            ok = 1;
                        }
                    }
                    if (ok == 0) {
                        //contine
                        //bag in array
                        actor1.add(actor);
                    }
                }
                Collections.sort(actor1, new SortActors());
                // acum am lista sortata
                String message = "Query result: [";
                if (actor1.size() < action.getNumber() && actor1.size() != 0) {
                    for (int j = 0; j < actor1.size() - 1; j++) {
                        message += actor1.get(j).getName() + ", ";
                        System.out.println(actor1.get(j).getAwards());
                    }
                    message += actor1.get(actor1.size() - 1).getName();
                }
                if (actor1.size() > action.getNumber()) {
                    for (int j = 0; j < action.getNumber() - 1; j++) {
                        message += actor1.get(j).getName() + ", ";
                        System.out.println(actor1.get(j).getName());
                    }
                    message += actor1.get(action.getNumber() - 1).getName();
                }
                message += "]";

                JSONObject jsonObj = fileWriter.writeFile(action.
                        getActionId(), "", message);
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
                        for (int i = 0; i < action.getFilters().get(action.getFilters().
                                size() - 1).size(); i++) {
                            // caut sa vad daca contine keyword urile dorite
                            //daca nu le contine pe toate, ok = 1
                            if (!actor.getCareerDescription().
                                    contains(action.getFilters().get(action.getFilters().
                                            size() - 1).get(i))) {
                                ok = 1;
                            }
                        }
                        // a gasit toate cuvintele
                        if (ok == 0) {
                            actor1.add(actor);
                        }
                    }
                }
                Collections.sort(actor1, new SortActorsDescription());
                System.out.println(actor1.size());

                // acum am lista sortata
                String message = "Query result: [";
                if (actor1.size() < action.getNumber() && actor1.size() != 0) {
                    for (int j = 0; j < actor1.size() - 1; j++) {
                        message += actor1.get(j).getName() + ", ";
                    }
                    message += actor1.get(actor1.size() - 1).getName();
                }
                if (actor1.size() > action.getNumber()) {
                    for (int j = 0; j < action.getNumber() - 1; j++) {
                        message += actor1.get(j).getName() + ", ";
                    }
                    message += actor1.get(action.getNumber() - 1).getName();
                }
                message += "]";

                JSONObject jsonObj = fileWriter.writeFile(action.
                        getActionId(), "", message);
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
                                    get(1).get(0))))
                            .collect(Collectors.toList());

                    Collections.sort(movies, new SortMoviesFavorite());
                    if (action.getSortType().equals("asc")) {
                        Collections.reverse(movies);
                    }
                    String message = "Query result: [";
                    if (movies.size() < action.getNumber() && movies.size() != 0) {
                        for (int j = 0; j < movies.size() - 1; j++) {
                            message += movies.get(j).getTitle() + ", ";
                        }
                        message += movies.get(movies.size() - 1).getTitle();
                    }
                    if (movies.size() > action.getNumber()) {
                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message += movies.get(j).getTitle() + ", ";
                        }
                        message += movies.get(action.getNumber() - 1).getTitle();
                    }
                    message += "]";
                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message);
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
                    // percurg utilizatorii
                    for (int i = 0; i < input.getUsers().size(); i++) {
                        for (SerialInputData serial : input.getSerials()) {
                            // daca filmul se afla in lista de favorite ale userului
                            if (input.getUsers().get(i).getFavoriteMovies().
                                    contains(serial.getTitle())) {
                                // incrementez nr de favorite
                                serial.setNumberFavoriteSerial(serial.
                                        getNumberFavoriteSerial() + 1);
                            }
                        }
                    }
                    List<SerialInputData> serial = input.getSerials()
                            .stream()
                            .filter(c -> c.getNumberFavoriteSerial() > 0 && c.
                                    getYear() == Integer.parseInt(action.
                                    getFilters().get(0).get(0)) && c.
                                    getGenres().contains((action.getFilters().get(1).get(0))))
                            .collect(Collectors.toList());

                    Collections.sort(serial, new SortSeriesFavorite());
                    if (action.getSortType().equals("asc")) {
                        Collections.reverse(serial);
                    }
                    String message = "Query result: [";
                    if (serial.size() < action.getNumber() && serial.size() != 0) {
                        for (int j = 0; j < serial.size() - 1; j++) {
                            message += serial.get(j).getTitle() + ", ";
                        }
                        message += serial.get(serial.size() - 1).getTitle();
                    }
                    if (serial.size() > action.getNumber()) {
                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message += serial.get(j).getTitle() + ", ";
                        }
                        message += serial.get(action.getNumber() - 1).getTitle();
                    }
                    message += "]";
                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message);
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
                    // ma plimb prin fiecare utilizator
                    for (int i = 0; i < input.getUsers().size(); i++) {
                        //trec prin fiecare film sa verificat de cate ori l a vazut
                        for (MovieInputData movie : input.getMovies()) {
                            //daca a vazut filmul, adun de cate ori l a vazut
                            if (input.getUsers().get(i).getHistory().
                                    containsKey(movie.getTitle())) {
                                System.out.println("Andrada");
                                // movie.getNumberViews() e null
                                movie.setNumberViews(movie.getNumberViews() + input.
                                        getUsers().get(i).getHistory().
                                        get(movie.getTitle()));
                            }
                        }
                    }
                    List<MovieInputData> movies = input.getMovies()
                            .stream()
                            .filter(c -> c.getNumberViews() > 0 && c.
                                    getYear() == Integer.parseInt(action.
                                    getFilters().get(0).get(0)) && c.
                                    getGenres().contains((action.getFilters().get(1).get(0))))
                            .collect(Collectors.toList());

                    Collections.sort(movies, new SortMoviesMostViewed());
                    if (action.getSortType().equals("desc")) {
                        Collections.reverse(movies);
                    }
                    String message = "Query result: [";
                    if (movies.size() < action.getNumber() && movies.size() != 0) {
                        for (int j = 0; j < movies.size() - 1; j++) {
                            message += movies.get(j).getTitle() + ", ";
                        }
                        message += movies.get(movies.size() - 1).getTitle();
                    }
                    if (movies.size() > action.getNumber()) {
                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message += movies.get(j).getTitle() + ", ";
                        }
                        message += movies.get(action.getNumber() - 1).getTitle();
                    }
                    message += "]";
                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message);
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
                    // ma plimb prin fiecare utilizator
                    for (int i = 0; i < input.getUsers().size(); i++) {
                        //trec prin fiecare film sa verificat de cate ori l a vazut
                        for (SerialInputData serial : input.getSerials()) {
                            //daca a vazut filmul, adun de cate ori l a vazut
                            if (input.getUsers().get(i).getHistory().
                                    containsKey(serial.getTitle())) {
                                System.out.println("Andrada");
                                // movie.getNumberViews() e null
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
                                    getGenres().contains((action.getFilters().get(1).get(0))))
                            .collect(Collectors.toList());

                    Collections.sort(movies, new SortSeriesMostViewed());
                    if (action.getSortType().equals("desc")) {
                        Collections.reverse(movies);
                    }
                    String message = "Query result: [";
                    if (movies.size() < action.getNumber() && movies.size() != 0) {
                        for (int j = 0; j < movies.size() - 1; j++) {
                            message += movies.get(j).getTitle() + ", ";
                        }
                        message += movies.get(movies.size() - 1).getTitle();
                    }
                    if (movies.size() > action.getNumber()) {
                        for (int j = 0; j < action.getNumber() - 1; j++) {
                            message += movies.get(j).getTitle() + ", ";
                        }
                        message += movies.get(action.getNumber() - 1).getTitle();
                    }
                    message += "]";
                    JSONObject jsonObj = fileWriter.writeFile(action.
                            getActionId(), "", message);
                    arrayResult.add(jsonObj);
                }
            }
        }
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("recommendation") && action.
                    getType().equals("standard")) {
                for (int i = 0; i < input.getUsers().size(); i++) {
                    int ok = 0;
                    //daca l gasesc pe cel bun, ma uit la filmele lui
                    if (input.getUsers().get(i).getUsername().
                            equals(action.getUsername())) {
                        // ma uit prin lista utilizatorului de baza
                        for (MovieInputData movie : input.getMovies()) {
                            // daca e primul film nevazut
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
                        //daca ok == 0 nu pote fi aplicata ,
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
                            // cauta printre cele nevizualizate pe cel mai bun
                            if (!input.getUsers().get(i).getHistory().
                                    containsKey(movie.getTitle())) {
                                // daca e nevizualizat il adaug in lista
                                movies1.add(movie);
                            }
                        }
                    }
                }
                Collections.sort(movies1, new SortMoviesRating());
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
                // le sortez in lista dupa favorizari
                List<MovieInputData> movies = input.getMovies();
                Collections.sort(movies, new SortMoviesFavorite());
                //System.out.println(movies);
                for (int i = 0; i < input.getUsers().size(); i++) {
                    for (int j = 0; j < movies.size(); j++) {
                        int ok = 0;
                        if (input.getUsers().get(i).getUsername().
                                equals(action.getUsername())) {
                            // iau primul film nevazut din lista sortata
                            if (!input.getUsers().get(i).getHistory().
                                    containsKey(movies.get(j).getTitle())) {
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
        for (ActionInputData action : input.getCommands()) {
            if (action.getActionType().equals("recommendation") && action.
                    getType().equals("search")) {
                // daca nu e niciunul pe care sa l fi vazut
                for (int i = 0; i < input.getUsers().size(); i++) {
                    int ok = 0;
                    if (input.getUsers().get(i).getUsername().
                            equals(action.getUsername())) {
                        for (MovieInputData movie : input.getMovies()) {
                            // cauta printre cele nevizualizate pe cel mai bun
                            if (!input.getUsers().get(i).getHistory().
                                    containsKey(movie.getTitle())) {
                                ok = 1;
                            }
                            if (ok == 0) {
                                // inseamna ca nu s a gasit niciun movie nevizualizat
                                String message = "SearchRecommendation cannot be applied!";
                                JSONObject jsonObj = fileWriter.writeFile(action.
                                        getActionId(), "", message);
                                arrayResult.add(jsonObj);
                            }
                        }
                    }

                }
            }
        }
        fileWriter.closeJSON(arrayResult);
    }
}
// la invalid user verific subscription type
