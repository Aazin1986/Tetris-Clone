package mino;
//So this will be the super class for mino where tetrominos are created by combining individual blocks
//In Tetris there are seven different tetrominos
//We will create different classes for every tetrominos
import main.KeyHandler;
import main.PlayManager;
import java.awt.Color;
import java.awt.Graphics2D;

public class Mino {
    public Block b[] = new Block[4];
    public Block tempB[] = new Block[4];
    int autoDropCounter = 0;
    public int direction = 1;// Every mino has 4 direction 1/2/3/4
    boolean leftCollision, rightCollision, bottomCollision;
    public boolean active = true;
    public boolean deactivating;
    int deactivateCounter = 0;

    public void create(Color c){
        b[0] = new Block(c);
        b[1] = new Block(c);
        b[2] = new Block(c);
        b[3] = new Block(c);
        tempB[0] = new Block(c);
        tempB[1] = new Block(c);
        tempB[2] = new Block(c);
        tempB[3] = new Block(c);
    }
    public void setXY(int x,int y){}
    public void updateXY(int direction){

        checkRotationCollision();

        if(leftCollision == false && rightCollision == false && bottomCollision == false){
            this.direction = direction;
            b[0].x = tempB[0].x;
            b[0].y = tempB[0].y;
            b[1].x = tempB[1].x;
            b[1].y = tempB[1].y;
            b[2].x = tempB[2].x;
            b[2].y = tempB[2].y;
            b[3].x = tempB[3].x;
            b[3].y = tempB[3].y;
        }
    }
    public void getDirection_1(){}
    public void getDirection_2(){}
    public void getDirection_3(){}
    public void getDirection_4(){}

    public void checkRotationCollision(){
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        checkStaticBlockCollision();

        //check frame collision

        for(int i = 0; i < b.length ; i++){
            if(tempB[i].x < PlayManager.left_x){
                leftCollision = true ;
            }
        }
        for(int i = 0; i < b.length ; i++){
            if(tempB[i].x + Block.SIZE > PlayManager.right_x){
                rightCollision = true ;
            }
        }
        for(int i = 0; i < b.length ; i++){
            if(tempB[i].y + Block.SIZE > PlayManager.bottom_y){
                bottomCollision = true ;
            }
        }
    }

    public void checkMovementCollision(){
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        //check static block collision
        checkStaticBlockCollision();

        //check frame collision
        //left wall
        for(int i = 0; i < b.length ; i++){
            if(b[i].x == PlayManager.left_x){
                leftCollision = true ;
            }
        }
        //right wall
        for(int i = 0; i < b.length ; i++){
            if(b[i].x + Block.SIZE == PlayManager.right_x){
                rightCollision = true ;
            }
        }
        //bottom wall
        for(int i = 0; i < b.length ; i++){
            if(b[i].y + Block.SIZE == PlayManager.bottom_y){
                bottomCollision = true ;
            }
        }
    }

    public void update(){

        if(deactivating){
            deactivating();
        }

        //Move the mino
        if(KeyHandler.upPressed){
            switch (direction){
                case 1: getDirection_2();break;
                case 2: getDirection_3();break;
                case 3: getDirection_4();break;
                case 4: getDirection_1();break;
            }
            KeyHandler.upPressed = false;
        }

        checkMovementCollision();

        if(KeyHandler.downPressed){
            if(bottomCollision == false){
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;

                //When moved down reset the autoDropCounter
                autoDropCounter = 0;
            }
            KeyHandler.downPressed = false;
        }
        if(KeyHandler.leftPressed){
            if(leftCollision == false){
                b[0].x -= Block.SIZE;
                b[1].x -= Block.SIZE;
                b[2].x -= Block.SIZE;
                b[3].x -= Block.SIZE;
            }
            KeyHandler.leftPressed = false;
        }
        if(KeyHandler.rightPressed){
            if(rightCollision == false){
                b[0].x += Block.SIZE;
                b[1].x += Block.SIZE;
                b[2].x += Block.SIZE;
                b[3].x += Block.SIZE;

            }
            KeyHandler.rightPressed = false;
        }

        if(bottomCollision){
            deactivating = true;
        }
        else{
            autoDropCounter++;
            if(autoDropCounter == PlayManager.dropInterval){
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                autoDropCounter = 0;
            }
        }

    }

    private void checkStaticBlockCollision(){

        for(int i = 0; i < PlayManager.staticBlocks.size(); i++){
            int targetX = PlayManager.staticBlocks.get(i).x;
            int targetY = PlayManager.staticBlocks.get(i).y;

            // check down
            for(int ii = 0; ii < b.length; ii++){
                if(b[ii].y + Block.SIZE == targetY && b[ii].x == targetX){
                    bottomCollision = true;
                }
            }

            //check left
            for(int ii = 0; ii < b.length; ii++){
                if(b[ii].x - Block.SIZE == targetX && b[ii].y == targetY){
                    leftCollision = true;
                }
            }
            for(int ii = 0; ii < b.length; ii++){
                if(b[ii].x + Block.SIZE == targetX && b[ii].y == targetY){
                    rightCollision = true;
                }
            }

        }
    }

    private void deactivating(){
        // Increment counter each frame
        deactivateCounter++;

        // Wait 45 frames until deactivation
        if (deactivateCounter == 45) {
            // Reset the counter for future use
            deactivateCounter = 0;

            // Re-check if bottom collision persists after 45 frames
            checkMovementCollision();

            // Deactivate the Mino if it's still colliding at the bottom
            if (bottomCollision) {
                active = false;
            }
        }
    }


    public void draw(Graphics2D g2){
        int margin = 2;
        g2.setColor(b[0].c);
        g2.fillRect(b[0].x+margin, b[0].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
        g2.fillRect(b[1].x+margin, b[1].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
        g2.fillRect(b[2].x+margin, b[2].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
        g2.fillRect(b[3].x+margin, b[3].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
    }
}