package client;

import client.model.*;

import java.util.ArrayList;
import java.util.Random;

public class AI
{

    private Random random = new Random();
    public int[][] go;
    int[] dx = {0, 0, 1, -1};
    int[] dy = {-1, 1, 0, 0};
    public static int m, n;
    public static int inf = (int) 1e9;
    Direction[] dirs =  {Direction.LEFT, Direction.RIGHT, Direction.DOWN, Direction.UP};
    static Cell[] targets = new Cell[4];
    static Cell[] ObjectiveZone = new Cell[4];
    boolean targetOk = false;
    public void preProcess(World world)
    {
        targets = new Cell[4];
        m = world.getMap().getColumnNum();
        n = world.getMap().getRowNum();
        int tot = n * m;
        boolean[] mark = new boolean[tot];
        go = new int[tot + 10][tot + 10];
        int ans = 0;

        for(int i=0; i<tot; i++){
            int row = i/m;
            int col = i%m;
            BFS(row, col, world);
            Cell cell = world.getMap().getCell(row, col);
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
        if (world.getCurrentTurn() < 4)
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
        if(!targetOk){
            targets = new Cell[4];
            ObjectiveZone = new Cell[4];
            targetOk = true;
            int tot = n * m;
            int min_x = inf, min_y = inf;
            int max_x = -inf, max_y = -inf;
            for(int i=0; i<tot; i++){
                int row = i/m;
                int col = i%m;
                Cell cell = world.getMap().getCell(row, col);
                if(cell.isInObjectiveZone()){
                    min_x = Math.min(min_x, row);
                    min_y = Math.min(min_y, col);
                    max_x = Math.max(max_x, row);
                    max_y = Math.max(max_y, col);
                }
            }
            ObjectiveZone[0] = world.getMap().getCell(min_x, (min_y + max_y)/2);
            ObjectiveZone[1] = world.getMap().getCell(max_x, (min_y + max_y)/2);

            ObjectiveZone[2] = world.getMap().getCell((min_x + max_x)/2, max_y);
            ObjectiveZone[3] = world.getMap().getCell((min_x + max_x)/2, min_y);
            System.out.println("*********************************************************");
            System.out.println(min_x);
            System.out.println(min_y);
            System.out.println(max_x);
            System.out.println(max_y);

            boolean[] mark = new boolean[tot];
            Hero[] heroes = world.getMyHeroes();
            for(int i=0; i<tot; i++)
                mark[i] = false;
            for (int heroID = 0; heroID < 4; ++heroID)
            {
                Hero hero = heroes[heroID];
                int best = inf;
                for(int i=0; i<tot; i++){
                    int row = i/m;
                    int col = i%m;
                    int distance = go[getId(hero.getCurrentCell())][i];
                    if(distance < best && mark[i] == false && world.getMap().getCell(row, col).isInObjectiveZone()){
                        best = distance;
                        mark[i] = true;
                        targets[heroID] = world.getMap().getCell(row, col);
                    }
                }
            }
            for(int i=0; i<4; i++){
                System.out.println(targets[i].getRow());
                System.out.println(targets[i].getColumn());
            }
        }
        System.out.println("move started");
        Hero[] heroes = world.getMyHeroes();
        ArrayList<Integer> remaining = new ArrayList<>();
        for (int heroID = 0; heroID < 4; ++heroID)
        {
            Hero hero = heroes[heroID];
            Cell her = hero.getCurrentCell();
            if(her.isInObjectiveZone() == true) {
                remaining.add(heroID);
                continue;
            }

            Cell target = targets[heroID];

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
                            System.out.println("moveeeee" + Integer.toString(her.getRow()) + " " + Integer.toString(her.getColumn()));


                            break;
                        }
                    }
                }
            }
        }
        System.out.println(remaining.size());
        System.out.println("wtttttttf");
        for (int heroID : remaining) {
            Hero hero = heroes[heroID];
            Cell target = ObjectiveZone[heroID];
            Cell her = hero.getCurrentCell();
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
        Hero[] enemies = world.getOppHeroes();
        if (enemies.length > 0) {

            int curAP = world.getAP();
            for (int heroId = 0; heroId < 4; heroId++) {
                Hero hero = heroes[heroId];

                Cell heroCell = hero.getCurrentCell();
                for (Hero enemy : enemies) {
                    Cell enemyCell = enemy.getCurrentCell();
                    if (world.getMap().isInMap(enemyCell.getRow(), enemyCell.getColumn()) && world.getMap().isInMap(heroCell.getRow(), heroCell.getColumn())) {
                        if (go[getId(hero.getCurrentCell())][getId(enemy.getCurrentCell())] < hero.getAbilities()[0].getRange()) {
                            int ability = 0;
                            if (hero.getAbilities()[2].isReady() && hero.getAbilities()[0].getAPCost()*(3-heroId) + hero.getAbilities()[2].getAPCost() <= curAP) {
                                ability = 2;
                            } else {
                                ability = 0;
                            }
                            world.castAbility(hero, hero.getAbilities()[ability], enemy.getCurrentCell());
                            curAP -= hero.getAbilities()[ability].getAPCost();
                            break;
                        }
                    }

                }
                /*
                System.out.println(hero.getAbilities());
                for (Ability ability : hero.getAbilities()) {
                    System.out.println(ability.getName());
                }
                */
            }
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