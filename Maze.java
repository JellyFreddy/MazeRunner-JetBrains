package maze;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Menu.SetMaze();
    }
}

class Menu {
    public static void SetMaze() {
        Scanner scanner = new Scanner(System.in);
        try (scanner){
            MazeInitializer ourMaze = null;
            int input;
            int flagGeneratedMaze = 0;
            int flagLoadedMaze = 0;
            while(true) {
                System.out.println("=== Menu ===\n1. Generate a new maze\n2. Load a maze");
                if (flagGeneratedMaze != 0 || flagLoadedMaze != 0)
                    System.out.println("3. Save the maze \n4. Display the maze\n5. Find the escape");
                System.out.println("0. Exit");
                input = scanner.nextInt();
                if (input == 0) {
                    System.out.println("Bye!");
                    break;
                }
                if (input == 1) {
                    System.out.println("Enter the size of a new maze");
                    int numberOfLines = scanner.nextInt();
                    ourMaze = new MazeInitializer(numberOfLines, numberOfLines);
                    ourMaze.generate();
                    ourMaze.print();
                    flagGeneratedMaze = 1;
                }
                if (input == 2) {
                    ourMaze = new MazeInitializer(5, 5);
                    scanner.nextLine();
                    String fileName = scanner.nextLine();
                    fileName =  "/Users/bannette/Desktop/Maze Runner/" + fileName;
                    ourMaze.load(fileName);
                    flagLoadedMaze = 1;
                }
                if (input == 3) {
                    if (flagGeneratedMaze == 0 && flagLoadedMaze == 0)
                        System.out.println("Incorrect option. Please try again");
                    else {
                        scanner.nextLine();
                        String fileName = scanner.nextLine();
                        File file = new File("/Users/bannette/Desktop/Maze Runner/" + fileName);
                        try (PrintWriter printWriter = new PrintWriter(file)) {
                            printWriter.print(ourMaze.height + " ");
                            for(int i = 0; i < ourMaze.height; i++) {
                                for(int j = 0; j < ourMaze.height; j++) {
                                    printWriter.println(ourMaze.mazeMatrix[i][j]);
                                }
                            }
                        } catch (IOException e) {
                            System.out.printf("An exception occurs %s", e.getMessage());
                        }
                    }
                }
                if (input == 4) {
                    if (flagGeneratedMaze == 0 && flagLoadedMaze == 0)
                        System.out.println("Incorrect option. Please try again");
                    else
                        ourMaze.print();
                }
                if (input == 5) {
                    if (flagGeneratedMaze == 0 && flagLoadedMaze == 0)
                        System.out.println("Incorrect option. Please try again");
                    else
                        ourMaze.findTheWay();
                }
            }
        }
    }
}

class MazeInitializer {
    int width;
    int height;
    int entry;
    int exit;
    int[][] mazeMatrix;

    public MazeInitializer(int width, int height) {
        this.width = width;
        this.height = height;
        mazeMatrix = new int[height][width];
        for(int i = 1; i < height - 1; i += 2) {
            for(int j = 1; j < width - 1; j += 2) {
                mazeMatrix[i][j] = 1;
            }
        }
        if (width % 2 == 0 && width - 2 > 0) {
            for(int i = 2; i < height - 1; i += 2) {
                mazeMatrix[i][width - 2] = 1;
            }
        }
        if (height % 2 == 0 && height - 2 > 0) {
            for(int i = 2; i < width - 1; i += 2) {
                mazeMatrix[height - 2][i] = 1;
            }
        }
    }

    public void load(String pathToFile){
        File file = new File(pathToFile);
        try (Scanner localScanner = new Scanner(file)) {
            this.height = localScanner.nextInt();
            this.width = this.height;
            mazeMatrix = new int[this.height][this.width];
            for(int i = 0; i < this.height; i++) {
                for(int j = 0; j < this.height; j++) {
                    mazeMatrix[i][j] = localScanner.nextInt();
                }
            }
            for(int i = 0; i < height; i++) {
                if (mazeMatrix[i][0] == 2){
                    this.entry = i;
                    break;
                }
            }
            for(int i = 0; i < height; i++) {
                if (mazeMatrix[i][width - 1] == 2) {
                    this.exit = i;
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("The file " + pathToFile + "does not exist");
        } catch (Exception ex) {
            System.out.println("Cannot load the maze. It has an invalid format");
        }
    }

    public void generate() {
        int x;
        int y;
        Deque<Movements> stack;
        Random random;
        ArrayList<Movements> available;
        Movements currentMovement;

        random = new Random(System.currentTimeMillis());
        x = 1;
        y = 1;
        stack = new ArrayDeque<>();


        while(true) {
            mazeMatrix[y][x] = 2;

            //проверили, сколько есть доступных клеток
            available = checkCell(y, x);


            if (available.size() == 0) {
                //если клеток нет и стэк пуст, мы разобрали все варианты
                if (stack.isEmpty())
                    break;
                //иначе откатываемся назад
                currentMovement = stack.pollLast();
                y -= currentMovement.y;
                x -= currentMovement.x;
                continue;
            }
            //рандомно выбираем одну из доступных клеток и перемещаемся в нее
            int randomNumber = random.nextInt(available.size());
            currentMovement = available.get(randomNumber);
            switch (currentMovement) {
                case UP: mazeMatrix[y - 1][x] = 2;
                    break;
                case DOWN: mazeMatrix[y + 1][x] = 2;
                    break;
                case LEFT: mazeMatrix[y][x - 1] = 2;
                    break;
                case RIGHT: mazeMatrix[y][x + 1] = 2;
                    break;
                case DR: mazeMatrix[y + 1][x] = 2;
                    break;
                case DL: mazeMatrix[y + 1][x] = 2;
                    break;
                case UR: mazeMatrix[y][x + 1] = 2;
                    break;
                case UL: mazeMatrix[y][x - 1] = 2;
                    break;
            }
            y += currentMovement.y;
            x += currentMovement.x;
            //не забываем добавить данное движение в наш стэк
            stack.add(currentMovement);
        }
        while(true) {
            y = random.nextInt(height - 2) + 1;
            if (mazeMatrix[y][1] != 0) {
                mazeMatrix[y][0] = 2;
                {
                    this.entry = y;
                    break ;
                }
            }
        }
        while(true) {
            y = random.nextInt(height - 2) + 1;
            if (mazeMatrix[y][width - 2] != 0) {
                mazeMatrix[y][width - 1] = 2;
                {
                    this.exit = y;
                    break ;
                }
            }
        }
    }

    public ArrayList<Movements> checkCell(int y, int x) {
        ArrayList<Movements> available = new ArrayList<>();
        int count;

        count = 0;
        if(y > 1)
            if (mazeMatrix[y - 2][x] == 1)
                available.add(Movements.UP);
        if(y < height - 2)
            if (mazeMatrix[y + 2][x] == 1)
                available.add(Movements.DOWN);
        if(x > 1)
            if (mazeMatrix[y][x - 2] == 1)
                available.add(Movements.LEFT);
        if(x < width - 2)
            if (mazeMatrix[y][x + 2] == 1)
                available.add(Movements.RIGHT);
        if(mazeMatrix[y + 1][x + 1] == 1)
            available.add(Movements.DR);
        if(mazeMatrix[y + 1][x - 1] == 1)
            available.add(Movements.DL);
        if(mazeMatrix[y - 1][x + 1] == 1)
            available.add(Movements.UR);
        if(mazeMatrix[y - 1][x - 1] == 1)
            available.add(Movements.UL);
        return (available);
    }

    public void print() {
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if (mazeMatrix[i][j] == 0)
                    System.out.print("\u2588\u2588");
                else
                    System.out.print("  ");
            }
            System.out.println();
        }
    }

    public void checkCellForExit(Deque<position> deque,int[][] exitMaze, int[][] mazeMatrix, int y, int x)
    {
        if (mazeMatrix[y][x + 1] != 0 && exitMaze[y][x + 1] == 0)
            deque.offerLast(new position(y, x + 1));
        if (mazeMatrix[y][x - 1] != 0 && x - 1 != 0 && exitMaze[y][x - 1] == 0)
            deque.offerLast(new position(y, x - 1));
        if (mazeMatrix[y + 1][x] != 0 && exitMaze[y + 1][x] == 0)
            deque.offerLast(new position(y + 1, x));
        if (mazeMatrix[y - 1][x] != 0 && exitMaze[y - 1][x] == 0)
            deque.offerLast(new position(y - 1, x));
    }

    public void  findTheWay() {
        Deque<position> deque = new ArrayDeque<>();
        int y;
        int x;
        position currentPos;
        int min;

        int[][] exitMaze = new int[height][width];
        for(int j = 0; j < height; j++) {
            for(int i = 0; i < width; i++) {
                exitMaze[j][i] = 0;
            }
        }

        exitMaze[entry][0] = 1;

        int count = 2;
        x = 1;
        y = entry;

        while(!(x == width - 1 && y == exit)) {
            exitMaze[y][x] = count;

            count++;

            checkCellForExit(deque,exitMaze, mazeMatrix, y, x);

            if (deque.isEmpty())
                break ;
            currentPos = deque.pollFirst();
            x = currentPos.x;
            y = currentPos.y;
        }



        int nextX;
        int nextY;
        x = width - 2;
        y = exit;
        min = Integer.MAX_VALUE;
        while(true) {
            exitMaze[y][x] = -1;
            if (y == entry && x == 0) {
                break ;
            }
            nextX = x - 1;
            nextY = y;
            if (exitMaze[y][x - 1] > 0 && exitMaze[y][x - 1] < min)
                min = exitMaze[y][x - 1];
            if (exitMaze[y][x + 1] > 0 && exitMaze[y][x + 1] < min) {
                min = exitMaze[y][x + 1];
                nextX = x + 1;
                nextY = y;
            }
            if (exitMaze[y - 1][x] > 0 && exitMaze[y - 1][x] < min) {
                min = exitMaze[y - 1][x];
                nextX = x;
                nextY = y - 1;
            }
            if (exitMaze[y + 1][x] > 0 && exitMaze[y + 1][x] < min) {
                min = exitMaze[y + 1][x];
                nextX = x;
                nextY = y + 1;
            }
            x = nextX;
            y = nextY;
        }

        exitMaze[entry][0] = -1;
        exitMaze[exit][width - 1] = -1;

        for(int j = 0; j < height; j++) {
            for(int i = 0; i < width; i++) {
                if (mazeMatrix[j][i] != 2)
                    System.out.print("\u2588\u2588");
                else if (exitMaze[j][i] < 0)
                    System.out.print("//");
                else
                    System.out.print("  ");
            }
            System.out.println();
        }
    }

}

class position {
    int y;
    int x;

    public position(int y, int x) {
        this.x = x;
        this.y = y;
    }
}


enum Movements {
    UP(-2, 0), DOWN(2, 0), LEFT(0, -2), RIGHT(0, 2),
    DR(1, 1), DL(1, -1), UR(-1, 1), UL(-1, -1);
    int x;
    int y;

    Movements(int y, int x) {
        this.x = x;
        this.y = y;
    }
}
