package go;

import javafx.util.Pair;

import java.util.ArrayList;

public class GameHandler {
    int whoseTurn;
    int[][] stoneLogicTable;
    int boardSize;
    int moveX, moveY;
    boolean isMoveAllowed;

    ArrayList<StoneChain> stoneChainList = new ArrayList<StoneChain>();
    ArrayList<StoneChain> fakeStoneChainList = new ArrayList<StoneChain>();

    public GameHandler(int boardSize) {
        this.boardSize = boardSize;
    }

    public int move (int moveX, int moveY, int whoseTurn, int[][] table) {
        this.moveX = moveX;
        this.moveY = moveY;
        this.whoseTurn = whoseTurn;
        stoneLogicTable = table;
        isMoveAllowed = true;

        isFieldEmpty();

        if(isMoveAllowed == true) {
            fakeStoneChainList = stoneChainList;
            if (isLibertyLeft(isPartOfChain(fakeStoneChainList)) || doesItKill(fakeStoneChainList)) {
                isPartOfChain(stoneChainList);
                stoneLogicTable[moveX][moveY] = whoseTurn;
                removeDead(stoneChainList);
                if (whoseTurn == 1) whoseTurn = 2;
                else whoseTurn = 1;
            }
        }

        GameServer.gameServer.setTable(stoneLogicTable);
        return whoseTurn;
    }

    private void isFieldEmpty() {
        if (stoneLogicTable[moveX][moveY] != 0) {
            isMoveAllowed = false;
        }
    }
    private StoneChain isPartOfChain(ArrayList<StoneChain> list) {
        StoneChain lastFoundIn = null;
        for (StoneChain stoneChain: list) {
            if (stoneChain.owner == whoseTurn) {
                if (stoneChain.isPartOfThisChain(moveX, moveY)) {
                    if (lastFoundIn == null) {
                        lastFoundIn = stoneChain;
                    }
                    else {
                        stoneChain.mergeChains(lastFoundIn);
                        list.remove(lastFoundIn);
                        lastFoundIn = stoneChain;
                    }
                }
            }
            if (stoneChain.owner != whoseTurn) {
                stoneChain.removeLiberty(moveX, moveY);
            }
        }
        if (lastFoundIn == null) {
            lastFoundIn = new StoneChain(whoseTurn, moveX, moveY);
            list.add(lastFoundIn);
        }
        return lastFoundIn;
    }

    private boolean isLibertyLeft (StoneChain chain) {
        if (chain.liberties.size() > 0 ) {
            return true;
        }
        else return false;
    }

    private boolean doesItKill(ArrayList<StoneChain> fakeList) {
        for (StoneChain chain: fakeList) {
            if (chain.owner != whoseTurn && chain.liberties.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void removeDead (ArrayList<StoneChain> stoneChainList) {
        for (StoneChain chain: stoneChainList) {
            if (chain.owner != whoseTurn && chain.liberties.isEmpty()) {
                for(Pair<Integer, Integer> pair: chain.stoneChain) {
                    stoneLogicTable[pair.getKey()][pair.getValue()] = 0;
                }
                //trzeba dodać odzyskane oddechy
                stoneChainList.remove(chain);
            }
        }
    }
}
