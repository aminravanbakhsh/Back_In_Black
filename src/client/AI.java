package client;

import client.model.*;

import java.util.Random;

public class AI
{

    private Random random = new Random();
    public int[][] go;
    int[] dx = {0, 0, 1, -1};
    int[] dy = {-1, 1, 0, 0};
    public static int m, n;
    Direction[] dirs =  {Direction.LEFT, Direction.RIGHT, Direction.DOWN, Direction.UP};
    public void preProcess(World world)
    {
        m = world.getMap().getColumnNum();
        n = world.getMap().getRowNum();
        int tot = n * m;
        go = new int[tot + 10][tot + 10];
        int ans = 0;
        for(int i=0; i<tot; i++){
            int row = i/m;
            int col = i%m;
            BFS(row, col, world);
            System.out.println("haha");
            ans++;
            System.out.println(ans);
        }
        System.out.println(tot);

    }

    public void pickTurn(World world)
    {
        System.out.println("pick started");
        System.out.println(world.getCurrentTurn());
        if (world.getCurrentTurn() < 3)
            world.pickHero(HeroName.BLASTER);
        else
            world.pickHero(HeroName.SENTRY);
    }
    public static int getId(Cell cell){
        return cell.getRow() * m + cell.getColumn();
    }
    public boolean isFree(Cell cell, World world){
        Hero[] heroes = world.getMyHeroes();
        for (Hero myHero : heroes) {
            if(cell.equals(myHero.getCurrentCell())){
                return false;
            }
        }
        return true;
    }
    public void moveTurn(World world)
    {
        System.out.println("move started");
        Hero[] heroes = world.getMyHeroes();
        Cell[] targets = world.getMap().getObjectiveZone();
        for (int heroID = 0; heroID < 4; ++heroID)
        {
            Hero hero = heroes[heroID];
            Cell her = hero.getCurrentCell();
            Cell target = targets[heroID];
            if (her == target) continue;
            for(int i=0; i<4; i++){
                int nx = her.getRow() + dx[i];
                int ny = her.getColumn() + dy[i];
                if (world.getMap().isInMap(nx, ny)) {
                    Cell cell = world.getMap().getCell(nx, ny);
                    if (!cell.isWall()) {
                        int dis = go[getId(her)][getId(target)];
                        int dis2 = go[getId(cell)][getId(target)];
                        if(dis2 == dis - 1 && isFree(cell, world)){

                            world.moveHero(hero, dirs[i]);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void actionTurn(World world) {
        System.out.println("action started");
        Hero[] heroes = world.getMyHeroes();
        Map map = world.getMap();
        for (Hero hero : heroes)
        {
            int row = random.nextInt(map.getRowNum());
            int column = random.nextInt(map.getColumnNum());

            world.castAbility(hero, hero.getAbilities()[random.nextInt(3)], row, column);
        }
    }

    private Direction[] BFS(int row,int col,World world) {

        Cell[] cells = new Cell[world.getMap().getRowNum()*world.getMap().getColumnNum()];
        int[] father = new int[world.getMap().getRowNum()*world.getMap().getColumnNum()];
        Direction[] directions = new Direction[world.getMap().getRowNum()*world.getMap().getColumnNum()];
        boolean[][] isChecked = new boolean[world.getMap().getRowNum()][world.getMap().getColumnNum()];

        for (int i = 0; i < world.getMap().getRowNum(); i++) {
            for (int j = 0; j < world.getMap().getColumnNum(); j++) {
                isChecked[i][j] = false;
            }
        }

        cells[0] = world.getMap().getCell(row, col);
        go[row * m + col][row * m + col] = 0;
        isChecked[row][col] = true;
        father[0] = -1;

        int head = 0;
        int tail = 1;


        while (head != tail) {
            int x = cells[head].getRow();
            int y = cells[head].getColumn();
            Cell cur = cells[head];

            for (int d = 0; d < 4; ++d) {
                int nx = x + dx[d], ny = y + dy[d];
                if (world.getMap().isInMap(nx, ny)) {

                    if (!isChecked[nx][ny]) {
                        Cell cell = world.getMap().getCell(nx, ny);
                        if (!cell.isWall()) {
                            go[row * m + col][nx * m + ny] = go[row * m + col][x * m + y] + 1;
                            isChecked[nx][ny] = true;
                            cells[tail] = world.getMap().getCell(nx, ny);
                            tail++;
                        }
                    }
                }
            }
            head++;
        }

        Direction[] list = new Direction[tail + 1];
        int size = 0;

        while (father[head] != -1) {
            list[size] = directions[head];
            head = father[head];
            size++;
        }


        Direction[] result = new Direction[size];
        for (int i = 0; i < size; i++) {
            result[i] = list[size-1-i];
        }
        return result;
    }
}