package nl.hanze.bordspelai.minimax;

import nl.hanze.bordspelai.games.BoardState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MinimaxCache {

    private final ConcurrentMap<Integer, ConcurrentMap<BoardState, Integer>> cache = new ConcurrentHashMap<>();

    public void reset() {
        cache.clear();
    }

    // Returns MIN_VALUE when something goes wrong
    public int getScoreForState(BoardState state, int boardSize) {
        if (!containsState(state, boardSize)) {
            return Integer.MIN_VALUE;
        } else {
            Map<BoardState, Integer> sizeCache = cache.get(boardSize);

            if (sizeCache != null) {
                return sizeCache.get(state);
            } else {
                return Integer.MIN_VALUE;
            }
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
        if (score < -100000 || score > 100000) {
            return;
        }

        if (cache.containsKey(boardSize)) {
            cache.get(boardSize).put(state, score);
        } else {
            ConcurrentMap<BoardState, Integer> boardCache = new ConcurrentHashMap<>();
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
