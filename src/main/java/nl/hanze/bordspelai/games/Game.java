package nl.hanze.bordspelai.games;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.managers.SceneManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Game {
    public final GameManager manager = GameManager.getInstance();

    private final ArrayList<Button> buttons = new ArrayList<>();
    private final char ownChar;
    private final char opponentChar;
    private final int size;
    private final Board board;

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
    }

    public void setupBoard(GridPane grid, HBox scoreBox, Label xPlayer, Label oPlayer) {
        int baseAmount = 12;
        int size = getSize();
        int cardSize = (baseAmount - size) * 10;
        int gap = baseAmount - size;

        // reversi needs 4 filled spots
        if (this instanceof Reversi) {
            addMove(27, 'x');
            addMove(28, 'o');
            addMove(35, 'o');
            addMove(36, 'x');

            scoreBox.setVisible(true);
        } else scoreBox.setVisible(false);

        // own char is You
        if(ownChar == 'o') {
            xPlayer.setText(manager.getUsername());
            xPlayer.setStyle("-fx-font-weight: bold;");

            oPlayer.setText(manager.getOpponent());
        } else {
            oPlayer.setText(manager.getUsername());
            oPlayer.setStyle("-fx-font-weight: bold;");

            xPlayer.setText(manager.getOpponent());
        }
    
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Button btn = new Button("");
                grid.setHgap(gap);
                grid.setVgap(gap);
                btn.setStyle("-fx-background-color: #ECECEC; -fx-background-radius: " + gap + "px;");
                btn.setMinHeight(cardSize);
                btn.setMinWidth(cardSize);
                btn.setPrefSize(cardSize, cardSize);
                grid.add(btn, j, i);
                buttons.add(btn);
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Board getBoard() {
        return board;
    }

    public char getCharByUsername(String username) {
        char playerChar;
        if (manager.getUsername().equals(username)) {
            playerChar = this.ownChar;
        } else {
            playerChar = this.opponentChar;
        }
        return playerChar;
    }

    public void addMove(int position, char charToMove) {
        board.setPosition(position, charToMove);
        updateMove(position);
        System.out.println(Arrays.toString(board.getBoard()));
    }

    public void addMove(int position) {
        addMove(position, getCharByUsername(manager.getCurrentPlayer()));
    }

    public abstract List<Integer> getAvailablePositions(Board board, char playerToCheck);

    public void updateMove(int position) {
        new Thread(() -> Platform.runLater(() -> {
            Button btn = buttons.get(position);

            char move = board.getPosition(position);
            double imageSize = btn.getPrefWidth() * 0.5;

            // might not be the cleanest way of solving the different gamemodes.
            if (this instanceof Reversi) {
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

            // Last but not least, update the scores, for both games if in the
            // future we would want to display TicTacToe scores.
            Label oScore = (Label) SceneManager.getParent().lookup("#oScoreText");
            oScore.setText("" + board.getAmount('o'));

            Label xScore = (Label) SceneManager.getParent().lookup("#xScoreText");
            xScore.setText("" + board.getAmount('x'));
        })).start();

    }

    public abstract int doBestMove();
}
