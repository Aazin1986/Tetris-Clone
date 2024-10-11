package main;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;

import mino.*;

public class PlayManager {

    //Main play area
    public static final int WIDTH = 360;
    public static final int HEIGHT = 600;

    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    //Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;

    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    //Others
    public static int dropInterval = 60;//Meaning the tetromino will drop 1 block every 60 frames
    boolean gameOver;
    //Effect
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    //Score
    int level = 1;
    int lines;
    int score;

    public PlayManager(){
        //Main Play Area Frame
        left_x = (GamePanel.WIDTH/2)-(WIDTH/2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y+HEIGHT;

        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

        //Set the starting mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);

        //Set the next mino
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }

    private Mino pickMino(){
        //pick a random Mino
        Mino mino = null;
        int i = new Random().nextInt(7);
        switch (i){
            case 0:mino = new Mino_L1();break;
            case 1:mino = new Mino_L2();break;
            case 2:mino = new Mino_Square();break;
            case 3:mino = new Mino_T();break;
            case 4:mino = new Mino_Bar();break;
            case 5:mino = new Mino_Z1();break;
            case 6:mino = new Mino_Z2();break;
        }
        return mino;
    }

    public void update(){
        //check if the currentMino is active
        if(currentMino.active == false){

            //if the current mino is not active put it into the staticBlocks
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            //check if game is over
            if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y){
                //this means the currentMino immediatly collided a block and couldn't move at all
                //so it's xy are the same with the nextMino's
                gameOver = true;
            }

            currentMino.deactivating = false;

            //replace the current mino with next mino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X,MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X,NEXTMINO_Y);

            //when a mino becomes inactive check if line can be deleted
            checkDelete();
        }
        else{
            currentMino.update();
        }
    }

    private void checkDelete(){
        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;


        while(x < right_x && y < bottom_y){
            for(int i = 0; i < staticBlocks.size(); i++){
                // increase the count if there is a static block
               if (staticBlocks.get(i).x == x && staticBlocks.get(i).y == y){
                   blockCount++;
               }
            }
            x += Block.SIZE;

            if(x == right_x){
                //if the blockCount hits 12 that means the current y line is all filled with blocks
                //so we can delete the line
                if(blockCount == 12){

                    effectCounterOn = true;
                    effectY.add(y);
                    for (int i = staticBlocks.size()-1; i > -1; i--){
                        //remove all the blocks in the current y line
                        if(staticBlocks.get(i).y == y){
                            staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    lines++;
                    //Drop Speed
                    //if line score hits a certain number, increase the drop speed
                    //1 is the fastest
                    if(lines % 10 == 0 && dropInterval > 1){
                        level++;
                        if(dropInterval > 10){
                            dropInterval -= 10;
                        }
                        else{
                            dropInterval -= 1;
                        }
                    }

                    //a line has been deleted so need to slide down blocks that are above it
                    for(int i = 0;i < staticBlocks.size(); i++){
                        //if a block is above the current y, move it down by the block size
                        if (staticBlocks.get(i).y < y){
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }
                }
                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }

        //Add Score
        if(lineCount > 0){
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
        }
    }

    public void draw(Graphics2D g2){
        //Drawing Play Area Frame
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-4, top_y-4, WIDTH+8, HEIGHT+8);

        //Drawing next tetromino frame
        int x = right_x+100;
        int y = bottom_y-200;

        g2.drawRect(x,y,200,200);
        g2.setFont(new Font("Arial",Font.PLAIN,30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2.drawString("NEXT",x+60,y+60);

        //Draw Score frame
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL: " + level, x, y); y+= 70;
        g2.drawString("LINES: " + lines, x, y); y+= 70;
        g2.drawString("SCORE: " + score, x, y);

        //Drawing the current tetromoinos
        if(currentMino != null){
            currentMino.draw(g2);
        }
        //Drawing the next mino
        nextMino.draw(g2);

        //Draw static blocks
        for(int i = 0; i<staticBlocks.size(); i++){
            staticBlocks.get(i).draw(g2);
        }

        //Draw effect
        if(effectCounterOn){
            effectCounter++;

            g2.setColor(Color.RED);
            for(int i = 0; i < effectY.size(); i++){
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }
            if(effectCounter == 10){
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        //Draw Pause or Game Over
        g2.setColor(Color.YELLOW);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(gameOver){
            x = left_x + 25;
            y = top_y + 320;
            g2.drawString("GAME OVER", x, y);
        }
        else if(KeyHandler.pausePressed){
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("PAUSED", x, y);
        }

        //Draw the game title
        x = 35;  // Starting x coordinate
        y = top_y + 320;  // Starting y coordinate

// Set font style
        g2.setFont(new Font("Segoe UI", Font.BOLD, 60));

// Array of Tetromino colors (T, E, T, R, I, S)
        Color[] tetrominoColors = {
                new Color(0, 0, 255),  // T - Purple
                new Color(255, 0, 0),    // E - Blue
                new Color(255, 255, 0),  // T - Yellow
                new Color(255, 165, 0),  // R - Orange
                new Color(0, 255, 255),  // I - Cyan
                new Color(0, 255, 0)     // S - Green
        };

// The string "TETRIS"
        String tetris = "TETRIS";

// Loop through each character, set the color, and draw it
        for (int i = 0; i < tetris.length(); i++) {
            g2.setColor(tetrominoColors[i]);  // Set color for each letter
            g2.drawString(String.valueOf(tetris.charAt(i)), x, y);  // Draw each letter
            x += g2.getFontMetrics().charWidth(tetris.charAt(i));  // Move x for the next letter
        }

    }
}
