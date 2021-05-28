package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private int countClosedTiles = SIDE*SIDE;
    private static final String MINE = "\uD83D\uDCA3";
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int score;

    private int rescore = score;
    private int recountMinesOnField = countMinesOnField;
    private int recountClosedTiles = countClosedTiles;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped){
            restart();
        } else {
            openTile(x,y);
        }
    }

    private void markTile(int x, int y){
        if (isGameStopped)return;
        if (countFlags == 0 && !gameField[y][x].isFlag)return;
        if (gameField[y][x].isOpen) return;
            if (!gameField[y][x].isFlag) {
                gameField[y][x].isFlag = true;
                countFlags--;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.AQUA);
            }else {
                gameField[y][x].isFlag = false;
                countFlags++;
                setCellValue(x, y, "");
                setCellColor(x, y, Color.GREY);
            }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x,y);
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.BLACK,"not bad :DDD",Color.KHAKI,40);
    }

    private void restart(){
        isGameStopped = false;
        score = rescore;
        setScore(score);
        countClosedTiles = recountClosedTiles;
        countMinesOnField = recountMinesOnField;
        createGame();
    }

    private void openTile(int x, int y){
        if (gameField[y][x].isOpen)return;
        if (gameField[y][x].isFlag)return;
        if (isGameStopped)return;
        gameField[y][x].isOpen = true;
        countClosedTiles--;
        setCellColor(x,y,Color.ANTIQUEWHITE);
        if (gameField[y][x].isMine){
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        }else {
            score+=5;
            setScore(score);
            if (countClosedTiles == countMinesOnField) {
                win();
            }
            if (gameField[y][x].countMineNeighbors==0){
                setCellValue(x,y,"");
                List<GameObject> result = getNeighbors(gameField[y][x]);
                for (GameObject g : result){
                    if (!g.isOpen) {
                        int xx = g.x;
                        int yy = g.y;
                        openTile(xx, yy);
                    }
                }
            }else {
                setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                if (countClosedTiles == countMinesOnField) {
                    win();
                }
            }
        }
    }


    private void countMineNeighbors(){
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
            if (!gameField[y][x].isMine){
                List<GameObject> result = getNeighbors(gameField[y][x]);
                for (GameObject g : result){
                    if (g.isMine){
                        gameField[y][x].countMineNeighbors++;
                    }
                }
            }
            }
        }
    }

    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.BLACK,"ti proebal :DDD",Color.KHAKI,40);
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x,y,"");
            }
        }

        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.GREY);
            }
        }
                countMineNeighbors();
        countFlags = countMinesOnField;

    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
}