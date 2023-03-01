! @Execute
      SUBROUTINE XINFLO(XINPUT,SLPLEN,A,B,QIN,QOUT,ROLGTH,
     1    QOSTAR,AINF,BINF,CINF,AINFTC,BINFTC,CINFTC,QSHEAR,RSPACE,
     1    NSLPTS)
     
      USE, INTRINSIC :: ISO_C_BINDING
      IMPLICIT NONE
      
      ! @Out
      REAL(C_DOUBLE), DIMENSION(101) :: XINPUT

      ! @In
      REAL(C_DOUBLE) :: SLPLEN
      
      ! @In
      REAL(C_DOUBLE), DIMENSION(MXSLP) :: A

      ! @In
      REAL(C_DOUBLE), DIMENSION(MXSLP) :: B

      ! @In
      REAL(C_DOUBLE) :: QIN

      ! @Out
      REAL(C_DOUBLE) :: QOUT
      
      ! @In
      REAL(C_DOUBLE) :: ROLGTH
      
      ! @Out
      REAL(C_DOUBLE) :: QOSTAR
      
      ! @Out
      REAL(C_DOUBLE), DIMENSION(MXSLP) :: AINF, BINF, CINF
      
      ! @Out
      REAL(C_DOUBLE), DIMENSION(MXSLP) :: AINFTC, BINFTC, CINFTC 
      
      ! @Out
      REAL(C_DOUBLE) :: QSHEAR

      ! @In
      REAL(C_DOUBLE) :: RSPACE
      
      ! @In
      INTEGER(C_INT) :: NSLPTS
      
C
C     SUBROUTINE XINFLO CONTROLS THE VARIABLES AFFECTED BY THE
C     RUNOFF BOTH LEAVING AND ENTERING THE FLOW PLANE
C
C     MODULE ADAPTED FROM WEPP VERSION 2004.7 AND CALLED FROM THE
C     MAIN PROGRAM
C
C     AUTHOR(S): D.C FLANAGAN AND J.C. ASCOUGH II
C     DATE LAST MODIFIED: 4-1-2005
C
C     + + + PARAMETER DECLARATIONS + + +
C
      INTEGER MXSLP, I
      PARAMETER (MXSLP = 40)
C
C     + + + ARGUMENT DECLARATIONS + + +
C
      REAL*8 XINPUT(101), SLPLEN, A(MXSLP), B(MXSLP), QIN, QOUT, DEL,
     1     QOSTAR, AINF(MXSLP), BINF(MXSLP), CINF(MXSLP),ROLGTH,
     1     AINFTC(MXSLP), BINFTC(MXSLP), CINFTC(MXSLP), QSHEAR, RSPACE
      INTEGER NSLPTS

C
C     + + + ARGUMENT DEFINITIONS + + +
C
C     BEGIN SUBROUTINE XINFLO
C
      DO I = 2, 101
         XINPUT(I) = FLOAT(I-1) * .01d0
      END DO
C
      DEL = QOUT - QIN
C
      IF (QOUT.LE.0.0d0) THEN
         QOSTAR = -ROLGTH / SLPLEN
      ELSE IF (ABS(DEL).GT.1.0d-10) THEN
         IF (QIN.LE.0.0d0) THEN
            QOSTAR = 0.0d0
         ELSE
            QOSTAR = QIN / DEL
         END IF
      ELSE
         IF (DEL.GE.0.0d0) QOSTAR = QIN / 1.0d-10
         IF (DEL.LT.0.0d0) QOSTAR = -QIN / 1.0d-10
      END IF
C
      IF (QOUT.GT.0.0d0) THEN
         IF (QOSTAR.EQ.-1.0d0) QOSTAR = -1.001d0
C
         DO I = 2, NSLPTS
            AINF(I) = A(I) / (QOSTAR+1.0d0)
            BINF(I) = (A(I)*QOSTAR+B(I)) / (QOSTAR+1.0d0)
            CINF(I) = (B(I)*QOSTAR/(QOSTAR+1.0d0))
            AINFTC(I) = AINF(I)
            BINFTC(I) = BINF(I)
            CINFTC(I) = CINF(I)
         END DO
C
         QSHEAR = QOUT * RSPACE

C
      ELSE
C
         IF (ABS(QOSTAR).LT.0.00001d0) QOSTAR = -0.00001d0
C
         DO I = 2, NSLPTS
            AINF(I) = A(I) / (QOSTAR)
            BINF(I) = (A(I)*QOSTAR+B(I)) / (QOSTAR)
            CINF(I) = B(I)
            AINFTC(I) = AINF(I)
            BINFTC(I) = BINF(I)
            CINFTC(I) = CINF(I)
         END DO
C
         QSHEAR = QIN * RSPACE

      END IF
C
      RETURN
      END
