************************************************************************
************************************************************************
*     SUBROUTINE WEATHR - Reads daily weather data from file
************************************************************************
*
*     LIST OF VARIABLES
*
*     DATE = date of weather record (YYDDD)
*     DYN  = dynamic control variable
*     PAR  = photosynthetically active radiation (MJ/m2/d)
*     RAIN = daily rainfall (mm)
*     SRAD = daily solar radiation (MJ/m2/d)
*     TMAX = daily maximum temperature (Celsius)
*     TMIN = daily minimum temperature (Celsius)
*
***********************************************************************
! @DSSAT(control="DYN", states="RATE")
! @Execute
      SUBROUTINE WEATHR(DOY, SIMCTRL,SIMCTRL_LEN,SRAD,TMAX,TMIN,
     &   RAIN,PAR,DYN,DYN_LEN)

!-----------------------------------------------------------------------  
      USE, INTRINSIC :: ISO_C_BINDING
      IMPLICIT NONE 
      
      ! @In
      INTEGER(C_INT) :: DOY
      
       ! @In
      CHARACTER(kind = C_CHAR, len = SIMCTRL_LEN) :: SIMCTRL
      INTEGER(C_INT), VALUE :: SIMCTRL_LEN

      ! @Out
      REAL(C_FLOAT) :: SRAD,TMAX,TMIN,RAIN,PAR
      
      ! @In
      CHARACTER(kind = C_CHAR, len = DYN_LEN) :: DYN
      INTEGER(C_INT), VALUE :: DYN_LEN
      
      INTEGER DATE
      
      !CHARACTER*5 :: DYN
      
      LOGICAL :: ISOPEN

!************************************************************************
!************************************************************************
!     INITIALIZATION
!************************************************************************
      IF (INDEX(DYN,'INIT') .NE. 0) THEN
!************************************************************************
        OPEN (4,FILE=SIMCTRL,STATUS='UNKNOWN')  
      
!************************************************************************
!************************************************************************
!     RATE CALCULATIONS
!************************************************************************
      ELSEIF (INDEX(DYN,'RATE') .NE. 0) THEN
        ! Check if open.  
        INQUIRE(unit=4, opened=ISOPEN)
        IF (ISOPEN .eqv. .FALSE.) THEN  
          OPEN (4,FILE=SIMCTRL,STATUS='UNKNOWN')  
        ENDIF
        IF (DOY .eq. 0) THEN  
            RETURN
        ENDIF
!************************************************************************
C     Loop to compute data for one year. Climatic data of the year 1987,
C     for Gainesville, Florida, were used as verification.

!        OPEN (4,FILE=SIMCTRL,STATUS='UNKNOWN')  
        READ(4,20) DATE,SRAD,TMAX,TMIN,RAIN,PAR
   20   FORMAT(I5,2X,F4.1,2X,F4.1,2X,F4.1,F6.1,14X,F4.1)
      
        PAR = 0.5 * SRAD   ! Par is defined as 50% of SRAD
        

!************************************************************************
!************************************************************************
      ELSEIF (INDEX(DYN,'CLOSE') .NE. 0) THEN
!************************************************************************
        CLOSE(4) 

!************************************************************************
!************************************************************************
!     End of dynamic 'IF' construct
!************************************************************************
      ENDIF
!************************************************************************
      RETURN
      END SUBROUTINE WEATHR
************************************************************************
************************************************************************
