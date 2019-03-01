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

        for (Hero hero : heroes)
        {
            Direction[] directions = BFS(hero, world);
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

        private Direction[] BFS(Hero hero, World world) {

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

        int p = 0;
        int q =1;


        while (!cells[p].isInObjectiveZone()) {
            int x = cells[p].getRow();
            int y = cells[p].getColumn();

            if (world.getMap().isInMap(x,y-1)) {
                if (!isChecked[x][y-1]) {
                    Cell cell = world.getMap().getCell(x, y-1);
                    if (!cell.isWall()) {
                        isChecked[x][y-1] = true;
                        cells[q] = cell;
                        father[q] = p;
                        directions[q] = Direction.LEFT;
                        q++;
                    }
                }
            }

            if (world.getMap().isInMap(x,y+1)) {
                if (!isChecked[x][y+1]) {
                    Cell cell = world.getMap().getCell(x, y+1);
                    if (!cell.isWall()) {
                        isChecked[x][y+1] = true;
                        cells[q] = cell;
                        father[q] = p;
                        directions[q] = Direction.RIGHT;
                        q++;
                    }
                }
            }

            if (world.getMap().isInMap(x+1,y)) {
                if (!isChecked[x+1][y]) {
                    Cell cell = world.getMap().getCell(x+1, y);
                    if (!cell.isWall()) {
                        isChecked[x+1][y] = true;
                        cells[q] = cell;
                        father[q] = p;
                        directions[q] = Direction.DOWN;
                        q++;
                    }
                }
            }

            if (world.getMap().isInMap(x-1,y)) {
                if (!isChecked[x-1][y]) {
                    Cell cell = world.getMap().getCell(x-1, y);
                    if (!cell.isWall()) {
                        isChecked[x-1][y] = true;
                        cells[q] = cell;
                        father[q] = p;
                        directions[q] = Direction.UP;
                        q++;
                    }
                }
            }
            p++;
        }

        Direction[] list = new Direction[p];
        int size = 0;
//        int[][] map = new int[world.getMap().getRowNum()][world.getMap().getColumnNum()];
//        System.out.println("first Map: ");
//        for (int i = 0; i < world.getMap().getRowNum(); i++) {
//            for (int j = 0; j < world.getMap().getColumnNum(); j++) {
//                if (world.getMap().getCell(i, j).isWall()) {
//                    map[i][j] = 3;
//                } else if(world.getMap().getCell(i,j).isInObjectiveZone()) {
//                    map[i][j] = 2;
//                } else {
//                    map[i][j] = 0;
//                }
//                System.out.print(map[i][j]);
//            }
//            System.out.println();
//        }

        while (father[p] != -1) {
//            map[cells[p].getRow()][cells[p].getColumn()] = 1;
            list[size] = directions[p];
            p = father[p];
            size++;
        }

//        System.out.println("map: ");
//        for (int i = 0; i < world.getMap().getRowNum(); i++) {
//            System.out.print((i+1) + ": ");
//            for (int j = 0; j < world.getMap().getColumnNum(); j++) {
//                System.out.print(map[i][j]);
//            }
//            System.out.println();
//        }

        Direction[] result = new Direction[size];
        for (int i = 0; i < size; i++) {
            result[i] = list[size-1-i];
        }
        return result;
    }
}
