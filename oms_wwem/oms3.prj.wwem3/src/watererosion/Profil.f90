! @Execute
SUBROUTINE PROFIL(A,B,AVGSLP,NSLPTS,SLPLEN,XXDIST,XINPUT,
     1                  XU,XL,Y,TOTLEN,SLPINP)
     
    USE, INTRINSIC :: ISO_C_BINDING
    IMPLICIT NONE
    
    ! @Out
    REAL(C_DOUBLE), DIMENSION(NSLPTS) :: A
    
    ! @Out
    REAL(C_DOUBLE), DIMENSION(NSLPTS) :: B
    
    ! @Out
    REAL(C_DOUBLE) :: AVGSLP
    
    ! @In
    INTEGER(C_INT) :: NSLPTS
    
    ! @In
    INTEGER(C_INT) :: SLPLEN
    
    ! @In
    REAL(C_DOUBLE), DIMENSION(NSLPTS) :: XXDIST
    
    ! @Out
    REAL(C_DOUBLE), DIMENSION(101) :: XINPUT
    
    ! @Out
    REAL(C_DOUBLE), DIMENSION(NSLPTS) :: XU
    
    ! @Out
    REAL(C_DOUBLE), DIMENSION(NSLPTS) :: XL
    
    ! @Out
    REAL(C_DOUBLE), DIMENSION(101) :: Y
    
    ! @Out
    INTEGER(C_INT) :: TOTLEN
    
    ! @Out
    REAL(C_DOUBLE), DIMENSION(NSLPTS) :: SLPINP
    
C
C     SUBROUTINE PROFIL CALCULATES SLOPE INPUT COEFFICIENTS
C
C     MODULE ADAPTED FROM WEPP VERSION 2004.7 AND CALLED FROM THE
C     MAIN PROGRAM
C
C     AUTHOR(S): D.C FLANAGAN AND J.C. ASCOUGH II
C     DATE LAST MODIFIED: 4-1-2005
C
C     + + + PARAMETER DECLARATIONS + + +
C
      INTEGER MXSLP
      PARAMETER (MXSLP = 40)
C
C     + + + ARGUMENT DECLARATIONS + + +
C

!      REAL*8 A(MXSLP), B(MXSLP), AVGSLP, SLPLEN, XINPUT(101), XU(MXSLP),
!     1    XL(MXSLP), Y(101), TOTLEN, XXDIST(MXSLP)
     
      INTEGER NSLPTS, L, KM, K
C
C     + + + ARGUMENT DEFINITIONS + + +
C
C
C     + + + LOCAL DECLARATIONS + + +
C
!      REAL*8 SLPINP(MXSLP)
      
      REAL*8 SLEN, SSTAR(MXSLP), XSTAR(MXSLP),
     1     YL(MXSLP), YU(MXSLP), C(MXSLP)
C
C     + + + LOCAL VARIABLE DEFINITIONS + + +
C
C     BEGIN SUBROUTINE PROFIL
C
C     READ (7,*) NSLPTS, SLPLEN
C     READ (7,*) (XINPUT(J),SLPINP(J),J = 1,NSLPTS)
C
      SLEN = XXDIST(NSLPTS)
      Y(NSLPTS) = 0.0d0
C
      DO K = 1, NSLPTS - 1
         KM = NSLPTS - K
         Y(KM) = Y(KM+1) + (XXDIST(KM+1) - XXDIST(KM)) *
     1           (SLPINP(KM) + SLPINP(KM+1)) / 2.0d0
      END DO
C
      AVGSLP = Y(1) / SLEN
C
      IF (AVGSLP.LE.0.0d0) AVGSLP = 0.000001d0
C
      DO K = 1, NSLPTS
         SSTAR(K) = SLPINP(K) / AVGSLP
         XSTAR(K) = XXDIST(K) / SLEN
      END DO
C
      DO K = 2, NSLPTS
         A(K) = (SSTAR(K)-SSTAR(K-1)) / (XSTAR(K)-XSTAR(K-1))
         B(K) = SSTAR(K-1) - A(K) * XSTAR(K-1)
      END DO
C
      YL(1) = 1.0d0
      XL(1) = 0.0d0
C
      DO K = 2, NSLPTS
         YU(K) = YL(K-1)
         XU(K) = XL(K-1)
         C(K) = YU(K) + A(K) * XSTAR(K-1) ** 2.0d0 / 2.0d0 + B(K) *
     +     XSTAR(K-1)
         YL(K) = -A(K) * XSTAR(K) ** 2.0d0 / 2.0d0 - B(K) * XSTAR(K) +
     +     C(K)
         XL(K) = XSTAR(K)
      END DO
C
      K = 2
      Y(1) = 1.0d0
C
      DO L = 2, 101
         XINPUT(L) = FLOAT(L-1) * 0.01d0
   10    IF (XINPUT(L).GT.XSTAR(K)) THEN
            K = K + 1
            GO TO 10
         END IF
         Y(L) = -A(K) * XINPUT(L) ** 2.0d0 / 2.0d0 - B(K) * XINPUT(L) +
     +     C(K)
      END DO
C
      TOTLEN = SLPLEN
C
      RETURN
      END
