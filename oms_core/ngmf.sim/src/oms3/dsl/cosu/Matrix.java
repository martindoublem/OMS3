/*
 * $Id$
 * 
 * This file is part of the Object Modeling System (OMS),
 * 2007-2012, Olaf David and others, Colorado State University.
 *
 * OMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1.
 *
 * OMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OMS.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
 */
package oms3.dsl.cosu;

final public class Matrix {

    public final int M;             // number of rows
    public final int N;             // number of columns
    public final double[][] data;   // M-by-N array

    // create M-by-N matrix of 0's
    public Matrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new double[M][N];
    }

    // create matrix based on 2d array
    public Matrix(double[][] data) {
        M = data.length;
        N = data[0].length;
        this.data = new double[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }

    // copy constructor
    private Matrix(Matrix A) {
        this(A.data);
    }

    // create and return a random M-by-N matrix with values between 0 and 1
    public static Matrix random(int M, int N) {
        Matrix A = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                A.data[i][j] = Math.random();
            }
        }
        return A;
    }

    // create and return the N-by-N identity matrix
    public static Matrix identity(int N) {
        Matrix I = new Matrix(N, N);
        for (int i = 0; i < N; i++) {
            I.data[i][i] = 1;
        }
        return I;
    }

    // swap rows i and j
    private void swap(int i, int j) {
        double[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    public Matrix copy() {
        return new Matrix(this);
    }

    // create and return the transpose of the invoking matrix
    public Matrix transpose() {
        Matrix A = new Matrix(N, M);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                A.data[j][i] = data[i][j];
            }
        }
        return A;
    }

    // return C = A + B
    public Matrix plus(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                C.data[i][j] = A.data[i][j] + B.data[i][j];
            }
        }
        return C;
    }

    // return C = A - B
    public Matrix minus(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                C.data[i][j] = A.data[i][j] - B.data[i][j];
            }
        }
        return C;
    }

    // does A = B exactly?
    public boolean eq(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (A.data[i][j] != B.data[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // return C = A * B
    public Matrix times(Matrix B) {
        Matrix A = this;
        if (A.N != B.M) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        Matrix C = new Matrix(A.M, B.N);
        for (int i = 0; i < C.M; i++) {
            for (int j = 0; j < C.N; j++) {
                for (int k = 0; k < A.N; k++) {
                    C.data[i][j] += A.data[i][k] * B.data[k][j];
                }
            }
        }
        return C;
    }

    public Matrix timesbyelement(Matrix B) {
        Matrix A = this;
        if (A.M != B.M && A.N != B.N) {
            throw new RuntimeException("Unable to do element matrix multiplication.");
        }
        Matrix C = new Matrix(A.M, A.N);
        for (int i = 0; i < C.M; i++) {
            for (int j = 0; j < C.N; j++) {
                C.data[i][j] = A.data[i][j] * B.data[i][j];
            }
        }
        return C;
    }

    // return x = A^-1 b, assuming A is square and has full rank
    public Matrix solve(Matrix rhs) {
        if (M != N || rhs.M != N || rhs.N != 1) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }

        // create copies of the data
        Matrix A = new Matrix(this);
        Matrix b = new Matrix(rhs);

        // Gaussian elimination with partial pivoting
        for (int i = 0; i < N; i++) {

            // find pivot row and swap
            int max = i;
            for (int j = i + 1; j < N; j++) {
                if (Math.abs(A.data[j][i]) > Math.abs(A.data[max][i])) {
                    max = j;
                }
            }
            A.swap(i, max);
            b.swap(i, max);

            // singular
            if (A.data[i][i] == 0.0) {
                throw new RuntimeException("Matrix is singular.");
            }

            // pivot within b
            for (int j = i + 1; j < N; j++) {
                b.data[j][0] -= b.data[i][0] * A.data[j][i] / A.data[i][i];
            }

            // pivot within A
            for (int j = i + 1; j < N; j++) {
                double m = A.data[j][i] / A.data[i][i];
                for (int k = i + 1; k < N; k++) {
                    A.data[j][k] -= A.data[i][k] * m;
                }
                A.data[j][i] = 0.0;
            }
        }

        // back substitution
        Matrix x = new Matrix(N, 1);
        for (int j = N - 1; j >= 0; j--) {
            double t = 0.0;
            for (int k = j + 1; k < N; k++) {
                t += A.data[j][k] * x.data[k][0];
            }
            x.data[j][0] = (b.data[j][0] - t) / A.data[j][j];
        }
        return x;

    }

    // print matrix to standard output
    public void print() {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                System.out.printf("%9.4f ", data[i][j]);
            }
            System.out.println();
        }
    }

    // test client
    public static void main(String[] args) {
        double[][] d = {{1, 2, 3}, {4, 5, 6}, {9, 1, 3}};
        Matrix D = new Matrix(d);
        D.print();
        System.out.println();

        Matrix A = Matrix.random(5, 5);
        A.print();
        System.out.println();

        A.swap(1, 2);
        A.print();
        System.out.println();

        Matrix B = A.transpose();
        B.print();
        System.out.println();

        Matrix C = Matrix.identity(5);
        C.print();
        System.out.println();

        A.plus(B).print();
        System.out.println();

        B.times(A).print();
        System.out.println();

        // shouldn't be equal since AB != BA in general
        System.out.println(A.times(B).eq(B.times(A)));
        System.out.println();

        Matrix b = Matrix.random(5, 1);
        b.print();
        System.out.println();

        Matrix x = A.solve(b);
        x.print();
        System.out.println();

        A.times(x).print();

    }
}
