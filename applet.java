import java.applet.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

/**
 * Class KaileySnake
 * Snake game
 * Kailey Chew
 * 06/07/17
 * Version 1
 * 
 * NOTES:
 * - dimension of player window should be 800x800
 * - also, some lines that were commented out have been left in 
 * so that people can see where I put print lines for debugging
 * 
 */
public class KaileySnake extends JApplet implements KeyListener, ActionListener
{
    int headLastX; //coordinates of where the snake head last
    int headLastY;
    int appleX; //coordinates of apple
    int appleY;
    int snakeLength;
    Timer timer;
    Timer textTimer;
    String direction;
    Boolean outOfBounds;
    Boolean lost;
    Boolean won;
    Boolean moved;
    Boolean started;
    Image[][] area; //the place where the user plays
    Boolean[][] appleOnTile;
    int[][] snakePos; //an array to store positions of squares of snake
    int[][] blankArray; //array for making new arrays
    Image green;
    Image apple;
    Image white;
    Image black;

    public void init()
    {
        //This is the timer I use to call move() again and again every
        //500 milliseconds. I got it from javax.swing.
        timer = new Timer(150, this);
        timer.start(); 

        snakeLength = 1;
        snakePos = new int[snakeLength][2];
        snakePos[0][0] = 2;
        snakePos[0][1] = 2;

        //getting all the pictures I need
        green = getImage(getCodeBase(),"green.jpg");
        white = getImage(getCodeBase(),"white.png");
        apple = getImage(getCodeBase(),"apple.png");
        black = getImage(getCodeBase(),"black.jpg");

        //making sure that the program isn't started when I first open it
        started = false;

        lost = false;

        //making the default direction right, for when the user starts
        direction = "right";

        //making the visible array of images for the user to play on, but with 2 extra rows and columns
        //for the out of bounds areas
        area = new Image[42][42];
        //putting the green square (the head of the snake) on this tile
        area[2][2] = green;

        //I do the same thing, but for the position of the apple
        appleOnTile = new Boolean[42][42];
        for (int a=0;a<42;a++)
        {
            for (int b=0;b<42;b++)
            {
                appleOnTile[a][b]=false;
            }
        }
        //putting the apple in a random slot, but in the visible portion of the array (from 1 to 41)
        appleX=(int)(Math.random()*40+1);
        appleY=(int)(Math.random()*40+1);
        area[appleX][appleY]=apple;
        appleOnTile[appleX][appleY]=true;

        //adding key listener
        addKeyListener(this);
        requestFocus();
    }

    public void paint(Graphics g)
    {        
        //System.out.println("***** Painted ");

        //making the background black
        getContentPane().setBackground(Color.black);
        /*content pane used to hold objects
        created by the Java run time environment*/
        if (lost==false)
        {
            //drawing the visible field, this is done everytime I repaint
            for (int a=1;a<41;a++)
            {
                for (int b=1;b<41;b++)
                {
                    g.drawImage(area[a][b], (b-1)*20, (a-1)*20, 20, 20, this); 
                }
            }

            //when the user hasn't started I want to display this text
            if (started==false)
            {            
                //textTimer = new Timer(500, this);
                //textTimer.start();
                g.setColor(Color.white);
                g.setFont(new Font("Times New Roman", Font.BOLD, 25));
                g.drawString("PRESS SPACE TO START", 250, 400);
            }
            else
            {
                g.setColor(Color.black);
                g.fillRect(250,380,300,25);
            }
        }
        else
        {
            g.setColor(Color.white);
            g.setFont(new Font("Times New Roman", Font.BOLD, 25));
            g.drawString("GAME OVER", 280, 400);
        }

        requestFocus();
    }

    public void move()
    {
        System.out.println("***** move() entered "+direction);  
        if (direction=="right")
        {
            lost=slither(0,1);
        }    
        else if (direction=="left")
        {
            lost=slither(0,-1);
        }
        else if (direction=="up")
        {
            lost=slither(-1,0);
        }
        else
        {
            lost=slither(1,0);
        }
        repaint();
    }

    public Boolean slither(int offsetX, int offsetY)
    {
        int headX, headY;
        headX = snakePos[0][0]+offsetX;
        headY = snakePos[0][1]+offsetY;
        area[headX][headY]=green;
        if (appleOnTile[headX][headY]==true)
        {
            snakeLength+=1;
            newArray();
            replaceApple();
        }
        else
        {
            area[snakePos[snakeLength-1][0]][snakePos[snakeLength-1][1]]=black;
        }   

        for (int i=snakeLength;i>1;)
        {
            i--;
            snakePos[i][0]=snakePos[i-1][0];
            snakePos[i][1]=snakePos[i-1][1];
        }
        snakePos[0][0] = headX;
        snakePos[0][1] = headY;
        return (headX==0||headX==41||headY==0||headY==41); //true if it's out of bounds
    }

    //actionPerformed isn't for buttons this time it is automatically
    //called whenever the timer is done
    public void actionPerformed(ActionEvent e)
    {
        //System.out.println("***** Timer done ");
        if (started==true)
        {
            //call move(), the method I made to move the snake 
            move();
            //restart the timer
            timer.restart();
        }
    }

    public void replaceApple()
    {
        System.out.println("***** replacing apple");
        //make sure all the tiles say there's no apple
        for (int a=0;a<42;a++)
        {
            for (int b=0;b<42;b++)
            {
                appleOnTile[a][b]=false;
            }
        }

        //put an apple in a random place but not under the snake
        Boolean replaced = false;
        while (replaced==false)
        {
            appleX=(int)(Math.random()*40+1);
            appleY=(int)(Math.random()*40+1);
            if (area[appleX][appleY]!=green)
            {
                area[appleX][appleY]=apple;
                appleOnTile[appleX][appleY]=true;
                replaced=true;
            }
        }
    }

    public void newArray()
    {
        blankArray = snakePos;
        snakePos = new int[snakeLength][2];
        for (int i=0;i<blankArray.length;++i)
        {
            snakePos[i][0] = blankArray[i][0];
            snakePos[i][1] = blankArray[i][1];
        }
    }

    public void keyPressed(KeyEvent event)
    {
        int k;
        k=event.getKeyCode();

        System.out.println("***** Keypressed "+k);

        //when a key is pressed, it doesn't actually move the snake but it
        //changes the direction so that move() knows which way to move it
        if (started==true)
        {
            if (k==39) //right
            {
                direction = "right";

            } else if (k==37) //left
            {
                direction = "left";

            } else if (k==38) //up
            {
                direction = "up";

            }else if (k==40) //down
            {
                direction = "down";

            }
        }

        if (k==32&&started==false) //space bar
        {
            //making started true should get rid of the text and will
            //let the program actually do something when the timer finishes
            started = true;
        }

        repaint();
    }

    public void keyReleased(KeyEvent event)
    {

    }

    public void keyTyped(KeyEvent event)
    {

    }
}
