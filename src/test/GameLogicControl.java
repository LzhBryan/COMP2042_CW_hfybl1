package test;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class GameLogicControl {

    private Random rnd;
    private Rectangle area;
    Ball ball;
    Player player;
    private Point startPoint;
    private int ballCount;
    private boolean ballLost;

    private GameLevels gameLevels;

    Brick[] bricks;
    private Brick[][] levels;
    private int level, brickCount;

    public GameLogicControl(Rectangle drawArea, int brickCount, int lineCount, double brickDimensionRatio,Point ballPos){
        startPoint = new Point(ballPos);
        ballCount = 3;
        ballLost = false;
        rnd = new Random();

        //create ball
        makeBall(ballPos);
        int speedX,speedY;

        do{
            // intial value = 5
            speedX = rnd.nextInt(12) - 2;
            // velocity
        }while(speedX == 0);
        do{
            //initial value = 3
            speedY = -rnd.nextInt(9);
        }while(speedY == 0);

        ball.setSpeed(speedX,speedY);

        player = new Player((Point) ballPos.clone(),150,10, drawArea);
        this.area = drawArea;

        gameLevels = new GameLevels(this);
        levels = gameLevels.makeLevels(drawArea,brickCount,lineCount, brickDimensionRatio);
        level = 0;
    }

    public void findImpacts(GameLevels gameLevels){
        if(player.impact(ball)){
            ball.reverseY();
            // touches the bar?
        }
        else if(impactWall(getBricks())){
            /*for efficiency reverse is done into method impactWall
             * because for every brick program checks for horizontal and vertical impacts
             */
            ReduceBrickCount();
        }
        else if(impactBorder()) {
            ball.reverseX();
            // touches the horizontal side of the border?
        }
        else if(ball.getPosition().getY() < area.getY()){
            ball.reverseY();
            // touches the ceiling?
        }
        else if(ball.getPosition().getY() > area.getY() + area.getHeight()){
            ballCount--;
            ballLost = true;
            // ball falls?
        }
    }

    public boolean impactWall(Brick[] bricks){
        for(Brick b : bricks){
            switch(b.findImpact(ball)) {
                //Vertical Impact
                case Brick.UP_IMPACT:
                    ball.reverseY();
                    return b.setImpact(ball.down, Crack.UP);
                case Brick.DOWN_IMPACT:
                    ball.reverseY();
                    return b.setImpact(ball.up, Crack.DOWN);

                //Horizontal Impact
                case Brick.LEFT_IMPACT:
                    ball.reverseX();
                    return b.setImpact(ball.right, Crack.RIGHT);
                case Brick.RIGHT_IMPACT:
                    ball.reverseX();
                    return b.setImpact(ball.left, Crack.LEFT);
            }
        }
        return false;
    }

    private boolean impactBorder(){
        Point2D p = ball.getPosition();
        return ((p.getX() < area.getX()) ||(p.getX() > (area.getX() + area.getWidth())));
    }

    private void makeBall(Point2D ballPos){
        ball = new RubberBall(ballPos);
    }

    public int getBallCount(){
        return ballCount;
    }

    public void setBallCount(int ballCount) {
        this.ballCount = ballCount;
    }

    public boolean isBallLost(){
        return ballLost;
    }

    public void ballReset(){
        player.moveTo(startPoint);
        ball.moveTo(startPoint);
        int speedX,speedY;
        do{
            speedX = rnd.nextInt(5) - 2;
        }while(speedX == 0);
        do{
            speedY = -rnd.nextInt(3);
        }while(speedY == 0);

        ball.setSpeed(speedX,speedY);
        ballLost = false;
    }

    public void move(){
        player.move();
        ball.move();
    }

    public boolean ballEnd(){
        return ballCount == 0;
    }

    public void setBallXSpeed(int s){
        ball.setXSpeed(s);
    }

    public void setBallYSpeed(int s){
        ball.setYSpeed(s);
    }

    public void resetBallCount(){
        ballCount = 3;
    }

    public Brick[] getBricks() {
        return bricks;
    }

    public int getBrickCount(){
        return brickCount;
    }

    public void ReduceBrickCount() {
        this.brickCount--;
    }

    public void wallReset(){
        for(Brick b : bricks)
            b.repair();
        brickCount = bricks.length;
        setBallCount(3);
    }

    public Brick makeBrick(Point point, Dimension size, String type){
        BrickFactory brickFactory = new BrickFactory();
        return brickFactory.getBricks(type, size, point);
    }

    public boolean isDone(){
        return brickCount == 0;
    }

    public void nextLevel(){
        bricks = levels[level++];
        this.brickCount = bricks.length;
    }

    public boolean hasLevel(){
        return level < levels.length;
    }
}
