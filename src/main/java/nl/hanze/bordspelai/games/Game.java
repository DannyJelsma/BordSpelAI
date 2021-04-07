package nl.hanze.bordspelai.games;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import nl.hanze.bordspelai.managers.GameManager;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Game {
    public final GameManager manager = GameManager.getInstance();

    private ArrayList<Button> buttons = new ArrayList<>();
    private char ownChar;
    private char opponentChar;
    private final int size;
    protected Board board;

//    private final String ownUsername;
//    private final String opponentUsername;
//
//    private final char ownChar;
//    private final char opponentChar;

    private ArrayList<Integer> availableMoves;

    public Game(int size, String startingPlayer) {
        this.size = size;
        this.board = new Board(size);

        // set players char
        if (startingPlayer.equals(manager.getUsername())) {
            this.ownChar = 'x';
            this.opponentChar = 'o';
        } else {
            this.ownChar = 'o';
            this.opponentChar = 'x';
        }

//        GameManager manager = GameManager.getInstance();
//        this.ownUsername = manager.getUsername();
//        this.opponentUsername = manager.getOpponent();
//
//        this.ownChar = 'x';
//        this.opponentChar = 'o';
    }

    public void setupBoard(GridPane grid) {
        int baseAmount = 12;
        int size = getSize();
        int cardSize = (baseAmount - size) * 10;
        int gap = baseAmount - size;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                Button btn = new Button("");
                grid.setHgap(gap);
                grid.setVgap(gap);
                btn.setStyle("-fx-background-color: #ECECEC; -fx-background-radius: " + gap + "px;");
                
                btn.setPrefSize(cardSize, cardSize);
        
                int clicked = size * i + j;
                btn.setOnAction((event) -> {
                    addMove(clicked);

                    // this.sendMove(clicked);
                    //this.model.addMove(clicked, manager.getUsername());

                    System.out.println("click " + clicked);
                });
                grid.add(btn, j, i);

                buttons.add(btn);
            }
        }

        // reversi needs 4 filled spots
        if(this instanceof Reversi) {
            addMove(27, 'x');
            addMove(28, 'o');
            addMove(35, 'o');
            addMove(36, 'x');
        }
    }

    // public String getStartingPlayer() {
    //     return startingPlayer;
    // }

    public int getSize() {
        return size;
    }

    public char[] getBoard() {
        return board.getBoard();
    }

    private char getCharByUsername(String username) {
        char playerChar;
        if (manager.getUsername().equals(username)) {
            playerChar = this.ownChar;
        } else {
            playerChar = this.opponentChar;
        }
        return playerChar;
    }

    public void addMove(int position, char charToMove) {
        board.setPosition(1, charToMove);
        
        updateMove(position);

        System.out.println(Arrays.toString(board.getBoard()));
    }

    public void addMove(int position) {
        addMove(position, getCharByUsername(manager.getCurrentPlayer()));
    }

    protected ArrayList<Integer> getAvailablePositions() {
        ArrayList<Integer> availablePositions = new ArrayList<>();
        for (int i = 0; i < this.board.getSize(); i++) {
            if (board.isPositionAvailable(i)) {
                availablePositions.add(i);
            }
        }

        if (availablePositions.size() == 0) {
            board.reset();

            return getAvailablePositions();
        }

        return availablePositions;
    }

    public void updateMove(int position) {
        Platform.runLater(() -> {
            Button btn = buttons.get(position);

            char move = board.getPosition(position);
            double imageSize = btn.getPrefWidth() * 0.5;
            System.out.println(imageSize);

            // test random move
            // int randomNumber = new Random().nextInt(2);
            // if(randomNumber == 0) {
            //     move = 'o';
            // } else move = 'x';

            // might not be the cleanest way of solving the different gamemodes.
            if(this instanceof Reversi) {
                // we can just use circles here instead of changing images.
                Circle circle = new Circle();
                circle.setRadius(imageSize / 2 * 1.05);
                circle.setFill(move == 'o' ? Color.valueOf("2e2b2b") : Color.valueOf("f2f0f0"));
                btn.setStyle("-fx-background-color: #41bf62");

                // red-green scheme
                // circle.setFill(move == 'o' ? Color.valueOf("207041") : Color.valueOf("EA4128"));
                // btn.setStyle("-fx-background-color: #DBCEA1");

                circle.setStyle("-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.2), 0, 1, 2, 2);");
                // btn.setStyle("-fx-background-color: #" +(move == 'o' ? "3fc462" : "226634"));
                // btn.setStyle("-fx-background-color: #" + (move == 'o' ? "ff695e" : "73ff5e"));
                btn.setGraphic(circle);
            } else {
                String source = move == 'o' ? "/images/circle.png" : "/images/cross.png";
                String backgroundColor = move == 'o' ? "73ff5e" : "ff695e";
                Image image = new Image(source, imageSize, imageSize, false, true);
                btn.setStyle("-fx-background-color: #" + backgroundColor + ";  -fx-background-radius: 12px;");
                ImageView view = new ImageView(image);
                btn.setGraphic(view);
            }
        });
    }

    public abstract int doBestMove();
}
