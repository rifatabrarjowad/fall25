package tictactoe;

public class Main {
    public static void main(String[] args) {
        // tictactoe.BIQI demo
        BIQI q = new BIQI(3);
        q.enqueue(10); q.enqueue(20); System.out.println(q);
        System.out.println(q.dequeue()); q.enqueue(30); q.enqueue(40);
        System.out.println(q);

        // TicTacToe demo
        TicTacToeBoard t = new TicTacToeBoardImpl_Jowad();
        t.setMark(0,0); t.setMark(1,1); t.setMark(0,1); t.setMark(2,2); t.setMark(0,2);
        System.out.println("\n" + t);
        System.out.println("\nGame over: " + t.isGameOver());
        System.out.println("Winner   : " + t.getWinner());
    }
}
