package client;

import client.model.*;

import java.util.Random;

public class AI
{


    private Random random = new Random();

    public void preProcess(World world)
    {
        System.out.println("pre process started");
    }

    public void pickTurn(World world)
    {
        System.out.println("pick started");
        world.pickHero(HeroName.values()[world.getCurrentTurn()]);
    }

    public void moveTurn(World world)
    {
        System.out.println("move started");
        Hero[] heroes = world.getMyHeroes();
        Cell[] targets = world.getMap().getObjectiveZone();
        for (int heroID = 0; heroID < 4; ++heroID)
        {
            Hero hero = heroes[heroID];
            Direction[] directions = BFS(hero, targets[heroID], world);
            if (directions.length > 0)
                world.moveHero(hero, directions[0]);
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

    private Direction[] BFS(Hero hero, Cell target, World world) {

        Cell[] cells = new Cell[world.getMap().getRowNum()*world.getMap().getColumnNum()];
        int[] father = new int[world.getMap().getRowNum()*world.getMap().getColumnNum()];
        Direction[] directions = new Direction[world.getMap().getRowNum()*world.getMap().getColumnNum()];
        boolean[][] isChecked = new boolean[world.getMap().getRowNum()][world.getMap().getColumnNum()];

        for (int i = 0; i < world.getMap().getRowNum(); i++) {
            for (int j = 0; j < world.getMap().getColumnNum(); j++) {
                isChecked[i][j] = false;
            }
        }

        cells[0] = hero.getCurrentCell();
        father[0] = -1;

        int head = 0;
        int tail = 1;
        int[] dx = {0, 0, 1, -1};
        int[] dy = {-1, 1, 0, 0};
        Direction[] dirs =  {Direction.LEFT, Direction.RIGHT, Direction.DOWN, Direction.UP};

        while (head != tail) {
            int x = cells[head].getRow();
            int y = cells[head].getColumn();
            Cell cur = cells[head];
            if (cur == target) break;
            for (int d = 0; d < 4; ++d) {
                int nx = x + dx[d], ny = y + dy[d];
                if (world.getMap().isInMap(nx, ny)) {
                    if (!isChecked[nx][ny]) {
                        Cell cell = world.getMap().getCell(nx, ny);
                        if (!cell.isWall()) {
                            isChecked[nx][ny] = true;
                            cells[tail] = cell;
                            father[tail] = head;
                            directions[tail] = dirs[d];
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