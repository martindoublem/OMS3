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
package lib;

public class CholeskyDecomposition implements java.io.Serializable {
    
    private static final long serialVersionUID = 1;
    private double[][] L;
    private int n;
    private boolean isspd;
    
    public CholeskyDecomposition (Matrix Arg) {
        double[][] A = Arg.getArray();
        n = Arg.getRowDimension();
        L = new double[n][n];
        isspd = (Arg.getColumnDimension() == n);
        
        // Main loop.
        for (int j = 0; j < n; j++) {
            double[] Lrowj = L[j];
            double d = 0.0;
            
            for (int k = 0; k < j; k++) {
                double[] Lrowk = L[k];
                double s = 0.0;
                
                for (int i = 0; i < k; i++) {
                    s += Lrowk[i] * Lrowj[i];
                }
                Lrowj[k] = s = (A[j][k] - s) / L[k][k];
                d = d + s * s;
                isspd = isspd & (A[k][j] == A[j][k]); 
            }
            d = A[j][j] - d;
            isspd = isspd & (d > 0.0);
            L[j][j] = Math.sqrt(Math.max(d,0.0));
            
            for (int k = j + 1; k < n; k++) {
                L[j][k] = 0.0;
            }
        }
    }
   
    public boolean isSPD () {
        return isspd;
    }
    
    /*
     * Return triangular factor.
     * @return     L
     */
    public Matrix getL () {
        return new Matrix(L, n, n);
    }

    /*
     * Solve A * X = B
     * @param  B   A Matrix with as many rows as A and any number of columns.
     * @return     X so that L * L' * X = B
     * @exception  IllegalArgumentException  Matrix row dimensions must agree.
     * @exception  RuntimeException  Matrix is not symmetric positive definite.
     */
    public Matrix solve (Matrix B) {
        if (B.getRowDimension() != n) {
            throw new IllegalArgumentException("Matrix row dimensions must agree.");
        }
        if (!isspd) {
            throw new RuntimeException("Matrix is not symmetric positive definite.");
        }

        // Copy right hand side.
        double[][] X = B.getArrayCopy();
        int nx = B.getColumnDimension();
        // Solve L * Y = B;
	for (int k = 0; k < n; k++) {
            for (int j = 0; j < nx; j++) {
                for (int i = 0; i < k ; i++) {
                    X[k][j] -= X[i][j] * L[k][i];
	        }
	        X[k][j] /= L[k][k];
	    }
	}
        // Solve L' * X = Y;
	for (int k = n - 1; k >= 0; k--) {
            for (int j = 0; j < nx; j++) {
                for (int i = k + 1; i < n ; i++) {
                    X[k][j] -= X[i][j] * L[i][k];
                }
	        X[k][j] /= L[k][k];
	    }
	}
        return new Matrix(X, n, nx);
    }
}