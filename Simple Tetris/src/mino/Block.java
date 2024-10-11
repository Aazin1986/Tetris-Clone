package mino;
//Create a package named mino inside main package
//Then Create a class named Block in mino package
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics2D;

//In Tetris every Tetromino is composed of 4 blocks
//This class represents each block which we will combine to make a tetromino
public class Block extends Rectangle {
    public int x,y;
    public static final int SIZE = 30; //A 30x30 block
    public Color c;

    public Block(Color c){
        this.c = c;
    }

    public void draw(Graphics2D g2){
        int margin = 2;
        g2.setColor(c);
        g2.fillRect(x+margin,y+margin,SIZE-(margin*2),SIZE-(margin*2));
    }

}
