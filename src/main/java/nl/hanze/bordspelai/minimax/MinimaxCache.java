package nl.hanze.bordspelai.minimax;

import nl.hanze.bordspelai.games.BoardState;

import java.util.HashMap;
import java.util.Map;

public class MinimaxCache {

    private final HashMap<Integer, Map<BoardState, Integer>> cache = new HashMap<>();

    public void reset() {
        cache.clear();
    }

    public int getScoreForState(BoardState state, int boardSize) {
        if (!containsState(state, boardSize)) {
            return Integer.MIN_VALUE;
        } else {
            return cache.get(boardSize).get(state);
        }
    }

    public void removeRedundantEntries(int currentBoardSize) {
        for (int size : cache.keySet()) {
            if (size < currentBoardSize) {
                cache.remove(size);
            }
        }
    }

    public boolean containsState(BoardState state, int boardSize) {
        if (cache.containsKey(boardSize)) {
            return cache.get(boardSize).containsKey(state);
        } else {
            return false;
        }
    }

    public void addBoardState(BoardState state, int boardSize, int score) {
        if (cache.containsKey(boardSize)) {
            cache.get(boardSize).put(state, score);
        } else {
            Map<BoardState, Integer> boardCache = new HashMap<>();
            boardCache.put(state, score);
            cache.put(boardSize, boardCache);
        }
    }

    public int getSize() {
        int counter = 0;

        for (Map<BoardState, Integer> map : cache.values()) {
            counter += map.size();
        }

        return counter;
    }
}
