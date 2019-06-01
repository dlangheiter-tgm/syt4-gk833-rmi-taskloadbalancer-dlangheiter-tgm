package client;

import compute.Task;

import java.io.Serializable;

public class Fibonacci implements Task<Integer>, Serializable {

    private int n;

    /**
     * Creates an Task to calculate the nth fibonacci number
     * @param n nth number of the Fibonacci sequence
     */
    public Fibonacci(int n) {
        this.n = n;
    }

    /**
     * Source: https://en.wikibooks.org/wiki/Algorithm_Implementation/Mathematics/Fibonacci_Number_Program#Java
     * returns the Nth number in the Fibonacci sequence
     */
    public int fibonacci(int N) {
        int lo = 0;
        int hi = 1;
        for (int i = 0; i < N; i++) {
            hi = lo + hi;
            lo = hi - lo;
        }
        return lo;
    }

    @Override
    public Integer execute() {
        return this.fibonacci(this.n);
    }
}
