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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Matrix implements Cloneable, Serializable {
    
    private static final long serialVersionUID = 1;
    private double[][] data;
    private int M;
    private int N;
    
    public Matrix( int m, int n ) {
        this.M = m;
        this.N = n;
        data = new double[ m ][ n ];
    }
    
    public Matrix( int m, int n, double s ) {
        this.M = m;
        this.N = n;
        data = new double[ m ][ n ];
        for ( int i = 0; i < m; i++ ) {
            for ( int j = 0; j < n; j++ ) {
                data[i][j] = s;
            }
        }
    }
    
    public Matrix( double[][] A ) {
        M = A.length;
        N = A[ 0 ].length;
        for( int i = 0; i < M; i++ ) {
            if( A[ i ].length != N ) {
                throw new IllegalArgumentException( "All rows must have the same length." );
            }
        }
        this.data = A;
    }
    
    public Matrix( double[][] A, int m, int n ) {
        this.data = A;
        this.M = m;
        this.N = n;
    }
    
    public Matrix( double vals[], int m ) {
        this.M = m;
        N = ( m != 0 ? vals.length / m : 0 );
        if( m * N != vals.length ) {
            throw new IllegalArgumentException( "Array length must be a multiple of m." );
        }
        data = new double[ m ][ N ];
        for( int i = 0; i < m; i++ ) {
            for( int j = 0; j < N; j++ ) {
                data[ i ][ j ] = vals[ i + j * m ];
            }
        }
    }

    public static Matrix constructWithCopy( double[][] A ) {
        int m = A.length;
        int n = A[ 0 ].length;
        Matrix X = new Matrix( m, n );
        double[][] C = X.getArray();
        for( int i = 0; i < m; i++ ) {
            if( A[ i ].length != n ) {
                throw new IllegalArgumentException( "All rows must have the same length." );
            }
            for( int j = 0; j < n; j++ ) {
                C[ i ][ j ] = A[ i ][ j ];
            }
        }
        return X;
    }

    public Matrix copy() {
        Matrix X = new Matrix( M, N );
        double[][] C = X.getArray();
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                C[ i ][ j ] = data[ i ][ j ];
            }
        }
        return X;
    }

    @Override
    public Object clone() {
        return this.copy();
    }

    public double[][] getArray() {
        return data;
    }

    public double[][] getArrayCopy() {
        double[][] C = new double[ M ][ N ];
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                C[ i ][ j ] = data[ i ][ j ];
            }
        }
        return C;
    }

    public double[] getColumnPackedCopy() {
        double[] vals = new double[ M * N ];
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                vals[ i + j * M ] = data[ i ][ j ];
            }
        }
        return vals;
    }

    public double[] getRowPackedCopy() {
        double[] vals = new double[ M * N ];
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                vals[ i * N + j ] = data[ i ][ j ];
            }
        }
        return vals;
    }

    public int getRowDimension() {
        return M;
    }

    public int getColumnDimension() {
        return N;
    }

    public double get( int i, int j ) {
        return data[ i ][ j ];
    }

    public Matrix getMatrix( int i0, int i1, int j0, int j1 ) {
        Matrix X = new Matrix( i1 - i0 + 1, j1 - j0 + 1 );
        double[][] B = X.getArray();
        try {
            for( int i = i0; i <= i1; i++ ) {
                for( int j = j0; j <= j1; j++ ) {
                    B[ i - i0 ][ j - j0 ] = data[ i ][ j ];
                }
            }
        } catch( ArrayIndexOutOfBoundsException e ) {
            throw new ArrayIndexOutOfBoundsException( "Submatrix indices out of bounds : " + e.getMessage() );
        }
        return X;
    }

    public Matrix getMatrix( int[] r, int[] c ) {
        Matrix X = new Matrix( r.length,c.length );
        double[][] B = X.getArray();
        try {
            for ( int i = 0; i < r.length; i++ ) {
                for ( int j = 0; j < c.length; j++) {
                    B[ i ][ j ] = data[ r[ i ] ][ c[ j ] ];
                }
            }
        } catch( ArrayIndexOutOfBoundsException e ) {
            throw new ArrayIndexOutOfBoundsException( "Submatrix indices out of bounds : " + e.getMessage() );
        }
        return X;
    }

    public Matrix getMatrix( int i0, int i1, int[] c ) {
        Matrix X = new Matrix( i1 - i0 + 1, c.length );
        double[][] B = X.getArray();
        try {
            for ( int i = i0; i <= i1; i++ ) {
                for ( int j = 0; j < c.length; j++ ) {
                    B[ i - i0 ][ j ] = data[ i ][ c[ j ] ];
                }
            }
        } catch( ArrayIndexOutOfBoundsException e ) {
            throw new ArrayIndexOutOfBoundsException( "Submatrix indices out of bounds : " + e.getMessage() );
        }
        return X;
    }

    public Matrix getMatrix( int[] r, int j0, int j1 ) {
        Matrix X = new Matrix( r.length,j1 - j0 + 1 );
        double[][] B = X.getArray();
        try {
            for( int i = 0; i < r.length; i++ ) {
                for( int j = j0; j <= j1; j++ ) {
                    B[ i ][ j - j0 ] = data[ r[ i ] ][ j ];
                }
            }
        } catch( ArrayIndexOutOfBoundsException e ) {
            throw new ArrayIndexOutOfBoundsException( "Submatrix indices out of bounds : " + e.getMessage() );
        }
        return X;
    }

    public void set( int i, int j, double s ) {
        data[ i ][ j ] = s;
    }

    public void setMatrix( int i0, int i1, int j0, int j1, Matrix X ) {
        try {
            for( int i = i0; i <= i1; i++ ) {
                for( int j = j0; j <= j1; j++ ) {
                    data[ i ][ j ] = X.get( i - i0, j - j0 );
                }
            }
        } catch( ArrayIndexOutOfBoundsException e ) {
            throw new ArrayIndexOutOfBoundsException( "Submatrix indices out of bounds : " + e.getMessage() );
        }
    }

    public void setMatrix( int[] r, int[] c, Matrix X ) {
        try {
            for( int i = 0; i < r.length; i++ ) {
                for( int j = 0; j < c.length; j++ ) {
                    data[ r[ i ] ][ c[ j ] ] = X.get( i, j );
                }
            }
        } catch( ArrayIndexOutOfBoundsException e ) {
            throw new ArrayIndexOutOfBoundsException( "Submatrix indices out of bounds : " + e.getMessage() );
        }
    }

    public void setMatrix( int[] r, int j0, int j1, Matrix X ) {
        try {
            for( int i = 0; i < r.length; i++ ) {
                for( int j = j0; j <= j1; j++ ) {
                     data[ r[ i ] ][ j ] = X.get( i, j - j0 );
                }
            }
        } catch( ArrayIndexOutOfBoundsException e ) {
            throw new ArrayIndexOutOfBoundsException( "Submatrix indices out of bounds : " + e.getMessage() );
        }
    }

    public void setMatrix( int i0, int i1, int[] c, Matrix X ) {
        try {
            for( int i = i0; i <= i1; i++ ) {
                for( int j = 0; j < c.length; j++ ) {
                    data[ i ][ c[ j ] ] = X.get( i - i0, j );
                }
            }
        } catch( ArrayIndexOutOfBoundsException e ) {
            throw new ArrayIndexOutOfBoundsException( "Submatrix indices out of bounds : " + e.getMessage() );
        }
    }

    public Matrix transpose() {
        Matrix X = new Matrix( N, M );
        double[][] C = X.getArray();
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                C[ j ][ i ] = data[ i ][ j ];
            }
        }
        return X;
    }

    public double norm1() {
        double f = 0;
        for( int j = 0; j < N; j++ ) {
            double s = 0;
            for( int i = 0; i < M; i++ ) {
                s += Math.abs( data[ i ][ j ] );
            }
            f = Math.max( f, s );
        }
        return f;
    }

    public double norm2() {
        return( new SingularValueDecomposition( this ).norm2() );
    }

    public double normInf () {
        double f = 0;
        for( int i = 0; i < M; i++ ) {
            double s = 0;
            for( int j = 0; j < N; j++ ) {
                s += Math.abs( data[ i ][ j ] );
            }
            f = Math.max( f, s );
        }
        return f;
    }

    public double normF() {
        double f = 0;
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                f = Maths.hypot( f, data[ i ][ j ] );
            }
        }
        return f;
    }

    public Matrix uminus() {
        Matrix X = new Matrix( M, N );
        double[][] C = X.getArray();
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                C[ i ][ j ] = -data[ i ][ j ];
            }
        }
        return X;
    }

    public Matrix plus( Matrix B ) {
        checkMatrixDimensions( B );
        Matrix X = new Matrix( M, N );
        double[][] C = X.getArray();
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                C[ i ][ j ] = data[ i ][ j ] + B.data[ i ][ j ];
            }
        }
        return X;
    }

    public Matrix plusEquals( Matrix B ) {
        checkMatrixDimensions( B );
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                data[ i ][ j ] += B.data[ i ][ j ];
            }
        }
        return this;
    }

    public Matrix minus( Matrix B ) {
        checkMatrixDimensions( B );
        Matrix X = new Matrix( M, N );
        double[][] C = X.getArray();
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                C[ i ][ j ] = data[ i ][ j ] - B.data[ i ][ j ];
            }
        }
        return X;
    }

    public Matrix minusEquals( Matrix B ) {
        checkMatrixDimensions( B );
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                data[ i ][ j ] -= B.data[ i ][ j ];
            }
        }
        return this;
    }

    public Matrix arrayTimes( Matrix B ) {
        checkMatrixDimensions( B );
        Matrix X = new Matrix( M, N );
        double[][] C = X.getArray();
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                C[ i ][ j ] = data[ i ][ j ] * B.data[ i ][ j ];
            }
        }
        return X;
    }

    public Matrix arrayTimesEquals( Matrix B ) {
        checkMatrixDimensions( B );
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                data[ i ][ j ] *= B.data[ i ][ j ];
            }
        }
        return this;
    }

    public Matrix arrayRightDivide( Matrix B ) {
        checkMatrixDimensions( B );
        Matrix X = new Matrix( M, N );
        double[][] C = X.getArray();
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                C[ i ][ j ] = data[ i ][ j ] / B.data[ i ][ j ];
            }
        }
        return X;
    }

    public Matrix arrayRightDivideEquals( Matrix B ) {
        checkMatrixDimensions( B );
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                data[ i ][ j ] /= B.data[ i ][ j ];
            }
        }
        return this;
    }

    public Matrix arrayLeftDivide( Matrix B ) {
        checkMatrixDimensions( B );
        Matrix X = new Matrix( M, N );
        double[][] C = X.getArray();
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                C[ i ][ j ] = B.data[ i ][ j ] / data[ i ][ j ];
            }
        }
        return X;
    }

    public Matrix arrayLeftDivideEquals( Matrix B ) {
        checkMatrixDimensions( B );
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                data[ i ][ j ] = B.data[ i ][ j ] / data[ i ][ j ];
            }
        }
        return this;
    }

    public Matrix times( double s ) {
        Matrix X = new Matrix( M, N );
        double[][] C = X.getArray();
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                C[ i ][ j ] = s * data[ i ][ j ];
            }
        }
        return X;
    }

    public Matrix timesEquals( double s ) {
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                data[ i ][ j ] *= s;
            }
        }
        return this;
    }

    public Matrix times( Matrix B ) {
        if( B.M != N ) {
            throw new IllegalArgumentException( "Matrix inner dimensions DO NOT agree!" );
        }
        Matrix X = new Matrix( M, B.N );
        double[][] C = X.getArray();
        double[] Bcolj = new double[ N ];
        for( int j = 0; j < B.N; j++ ) {
            for( int k = 0; k < N; k++ ) {
                Bcolj[ k ] = B.data[ k ][ j ];
            }
            for( int i = 0; i < M; i++ ) {
                double[] Arowi = data[ i ];
                double s = 0;
                for(int k = 0; k < N; k++) {
                    s += Arowi[ k ] * Bcolj[ k ];
                }
                C[ i ][ j ] = s;
            }
        }
        return X;
    }

    public LUDecomposition lu() {
        return new LUDecomposition( this );
    }

    public QRDecomposition qr() {
        return new QRDecomposition( this );
    }

    public CholeskyDecomposition chol() {
        return new CholeskyDecomposition( this );
    }

    public SingularValueDecomposition svd() {
        return new SingularValueDecomposition( this );
    }

    public EigenvalueDecomposition eig() {
        return new EigenvalueDecomposition( this );
    }

    public Matrix solve( Matrix B ) {
        return ( M == N ? ( new LUDecomposition( this ) ).solve( B ) : ( new QRDecomposition( this ) ).solve( B ) );
    }

    public Matrix solveTranspose(Matrix B) {
        return transpose().solve( B.transpose() );
    }

    public Matrix inverse() {
        return solve( identity( M, M ) );
    }

    public double det() {
        return new LUDecomposition( this ).det();
    }

    public int rank() {
        return new SingularValueDecomposition( this ).rank();
    }

    public double cond() {
        return new SingularValueDecomposition( this ).cond();
    }

    public double trace() {
        double t = 0;
        for( int i = 0; i < Math.min( M, N ); i++ ) {
            t += data[ i ][ i ];
        }
        return t;
    }

    public static Matrix random( int m, int n ) {
        Matrix A = new Matrix( m, n );
        double[][] X = A.getArray();
        for(int i = 0; i < m; i++ ) {
            for(int j = 0; j < n; j++ ) {
                X[ i ][ j ] = Math.random();
            }
        }
        return A;
    }

    public static Matrix identity( int m, int n ) {
        Matrix A = new Matrix( m, n );
        double[][] X = A.getArray();
        for( int i = 0; i < m; i++ ) {
            for( int j = 0; j < n; j++ ) {
                X[ i ][ j ] = ( i == j ) ? 1.0 : 0.0;
            }
        }
        return A;
    }

    public void print( int w, int d ) {
        print( new PrintWriter( System.out, true ), w, d ); 
    }

    public void print( PrintWriter output, int w, int d ) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols( new DecimalFormatSymbols( Locale.US ) );
        format.setMinimumIntegerDigits( 1 );
        format.setMaximumFractionDigits( d );
        format.setMinimumFractionDigits( d );
        format.setGroupingUsed( false );
        print( output, format, w + 2 );
    }

    public void print( NumberFormat format, int width ) {
        print( new PrintWriter( System.out, true ), format, width ); 
    }

    public void print( PrintWriter output, NumberFormat format, int width ) {
        output.println();  // start on new line.
        for( int i = 0; i < M; i++ ) {
            for( int j = 0; j < N; j++ ) {
                String s = format.format( data[ i ][ j ] ); // format the number
                int padding = Math.max( 1, width - s.length() ); // At _least_ 1 space
                for( int k = 0; k < padding; k++ )
                    output.print(' ');
                output.print( s );
            }
            output.println();
        }
        output.println();   // end with blank line.
    }

    public static Matrix read( BufferedReader input ) throws IOException {
        StreamTokenizer tokenizer= new StreamTokenizer( input );
        /*
         * Although StreamTokenizer will parse numbers, it doesn't recognize scientific notation
         * (E or D); however, Double.valueOf does. The strategy here is to disable StreamTokenizer's
         * number parsing. We'll only get whitespace delimited words, EOL's and EOF's. These words 
         * should all be numbers, for Double.valueOf to parse.
         */
        tokenizer.resetSyntax();
        tokenizer.wordChars( 0, 255 );
        tokenizer.whitespaceChars( 0, ' ' );
        tokenizer.eolIsSignificant( true );
        ArrayList< Double > vD = new ArrayList< Double >();

        // Ignore initial empty lines
        while( tokenizer.nextToken() == StreamTokenizer.TT_EOL );
        
        if( tokenizer.ttype == StreamTokenizer.TT_EOF ) {
            throw new java.io.IOException( "Unexpected EOF on matrix read." );
        }
        do {
            vD.add( Double.valueOf( tokenizer.sval ) ); // Read & store 1st row.
        } while( tokenizer.nextToken() == StreamTokenizer.TT_WORD );

        double row[] = new double[ vD.size() ];
        for( int j = 0; j < vD.size(); j++ )  {// extract the elements of the 1st row.
            row[ j ] = vD.get( j ).doubleValue();
        }
        ArrayList< double[] > v = new ArrayList< double[] >();
        v.add( row );  // Start storing rows instead of columns.
        while( tokenizer.nextToken() == StreamTokenizer.TT_WORD ) {
            // While non-empty lines
            v.add( row = new double[ vD.size() ] );
            int j = 0;
            do {
                if( j >= vD.size() ) {
                    throw new IOException( "Row " + v.size() + " is too long." );
                }
                row[ j++ ] = Double.valueOf( tokenizer.sval ).doubleValue();
            } while ( tokenizer.nextToken() == StreamTokenizer.TT_WORD );
            if( j < vD.size() ) {
                throw new IOException( "Row " + v.size() + " is too short." );
            }
        }
        double[][] A = new double[ v.size() ][];
        //v.copyInto( A );  // copy the rows out of the vector
        //This should replace commented line above
        for( int i = 0; i < v.size(); i++ ) {
            for( int j = 0; j < v.get( i ).length; j++ ) {
                A[ i ][ j ] = v.get( i )[ j ];
            }
        }
        return new Matrix( A );
    }

    private void checkMatrixDimensions( Matrix B ) {
        if( B.M != M || B.N != N ) {
            throw new IllegalArgumentException( "Matrix dimensions must agree." );
        }
    }
}