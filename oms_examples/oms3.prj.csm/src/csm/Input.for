************************************************************************
*     SUBROUTINE OPENF(DOYP)
*     This subroutine opens the simulation control file, and reads date of
*     planting (DOYP)
*
*     SIMCTRL.INP => date of planting, frequency of printout
************************************************************************
      ! @Execute
      SUBROUTINE OPENF(SIMCTRL, SIMCTRL_LEN, DOYP, FROP)
          
      USE, INTRINSIC :: ISO_C_BINDING
      IMPLICIT NONE
      
      ! @In
      CHARACTER(kind = C_CHAR, len = SIMCTRL_LEN) :: SIMCTRL
      INTEGER(C_INT), VALUE :: SIMCTRL_LEN
      
      ! @Out
      INTEGER(C_INT) :: DOYP, FROP
      
      OPEN (UNIT=8, FILE=SIMCTRL, STATUS='UNKNOWN')
      READ(8,5) DOYP, FROP
      IF (FROP .LE. 0) FROP = 1
    5 FORMAT(2I6)
      CLOSE(8)
      
!      print *, DOYP, FROP
      
!-----------------------------------------------------------------------  
      RETURN
      END SUBROUTINE OPENF
************************************************************************
************************************************************************
