************************************************************************
************************************************************************
*     Subroutine PLANT2
*     This subroutine simulates the growth of the plant using pre-determined
*     conditions.Hourly values of temperature and photosyntetically active
*     radiation come from WEATHER subroutine and daily values of availability
*     of water in the soil come from SW subroutine. This subroutine supplies
*     the SW subroutine with daily values of leaf area index (LAI).
C****************************************************************************

*                  LIST OF VARIABLES 
*     di    = daily accumulated temperature above tb (degree days)
*     dLAI  = daily increase in leaf area index (m2/m2/d)
*     dN    = incremental leaf number
*     DOY   = day of the year 
*     DYN   = dynamic control variable
*     dw    = incremental total plant dry matter weight (g m-2)
*     dwc   = incremental canopy dry matter weight (g m-2)
*     dwf   = incremental fruit dry matter weight (g m-2)
*     dwr   = incremental root dry matter weight (g m-2)
*     E     = conversion efficiency of CH2O to plant tissue (g g-1)
*     EMP1  = empirical coef. for expoilinear eq.
*     EMP2  = empirical coef. for expoilinear eq.
*     endsim= code signifying physiological maturity (end of simulation)
*     Fc    = fraction of total crop growth partitioned to canopy
*     FL    = code for development phase (1=vegetative phase, 
*                 2=reproductive phase)
*     int   = accumulated temperature after reproductive phase starts (c)
*     INTOT = duration of reproductive stage (degree days)
*     LAI   = canopy leaf area index (m2 m-2)
*     Lfmax = maximum number of leaves
*     N     = leaf number
*     nb    = empirical coef. for expoilinear eq.
*     p1    = dry matter of leaves removed per plant per unit development after
*              maximum number of leaves is reached (g)
*     PD    = plant density m-2
*     Pg    = canopy gross photosynthesis rate (g plant-1 day-1)
*     PT    = photosynthesis reduction factor for temp.
*     rm    = maximum rate of leaf appearearance (day-1)
*     sla   = specific leaf area (m2 g-1)
*     SRAD  = Daily solar radiation (MJ m-2)
*     SWFAC1= soil water deficit stress factor 
*     SWFAC2= soil water excess stress factor
*     tb    = base temperature above which reproductive growth occurs (c)
*     TMAX  = Daily maximum temperature (c)
*     TMIN  = Daily manimum temperature (c)
*     TMN   = Daily mean temperature (c)
*     W     = total plant dry matter weight (g m-2)
*     Wc    = canopy dry matter weight (g m-2)
*     Wf    = fruit dry matter weight (g m-2)
*     Wr    = root dry matter weight (g m-2)


************************************************************************
! @Description("plant growth calculation - C02")
! @VersionInfo("$Id$")
! @DSSAT(control="DYN", states="RATE INTEGR OUTPT")  
! @Execute
      SUBROUTINE PLANT2(
     &    DOY, DOYP, endsim, TMAX, TMIN, PAR, CO2, SWFAC1, SWFAC2,   !Input
     &    LAI,                                            !Output
     &    PLANT_INP, PLANT_INP_LEN,                       !Input
     &    PLANT_OUT, PLANT_OUT_LEN,                             !Input     
     &    DYN, DYN_LEN)                                   !Control
 
!-----------------------------------------------------------------------  
      USE, INTRINSIC :: ISO_C_BINDING 
      IMPLICIT NONE 
      SAVE 
      
      ! @In
      INTEGER(C_INT) :: DOY
      
      ! @In
      INTEGER(C_INT) :: DOYP
      
      ! @Out
      INTEGER(C_INT) :: endsim
      
      ! @In
      REAL(C_FLOAT) :: TMAX, TMIN, PAR, CO2, SWFAC1, SWFAC2
      
      ! @Out
      REAL(C_FLOAT) :: LAI
      
      ! @In
      CHARACTER(kind = C_CHAR, len = PLANT_INP_LEN) :: PLANT_INP
      INTEGER(C_INT), VALUE :: PLANT_INP_LEN
      ! @In
      CHARACTER(kind = C_CHAR, len = PLANT_OUT_LEN) :: PLANT_OUT
      INTEGER(C_INT), VALUE :: PLANT_OUT_LEN
      ! @In
      CHARACTER(kind = C_CHAR, len = DYN_LEN) :: DYN
      INTEGER(C_INT), VALUE :: DYN_LEN

      REAL E,Fc,nb,N,PT,Pg, di
      REAL rm,dwf,int, p1, sla
      REAL PD,EMP1,EMP2,Lfmax,dwc, TMN
      REAL dwr,dw,dn,w,wc,wr,wf,tb,intot, dLAI, FL
      INTEGER  COUNT
      
      LOGICAL :: ISOPEN


!************************************************************************
!************************************************************************
!     INITIALIZATION
!************************************************************************
      IF (INDEX(DYN,'RATE') .NE. 0) THEN
!************************************************************************
       IF (ISOPEN .eqv. .FALSE.) THEN  
            ! DO INIT HERE
         ISOPEN = .TRUE.
          
        endsim = 0

         OPEN (2,FILE=PLANT_INP,STATUS='UNKNOWN')
        OPEN (1,FILE=PLANT_OUT,STATUS='REPLACE')
        
        READ(2,10) Lfmax, EMP2,EMP1,PD,nb,rm,fc,tb,intot,n,lai,w,wr,wc
     &     ,p1,sla
   10   FORMAT(17(1X,F7.4))
        CLOSE(2)

        WRITE(1,11)
        WRITE(1,12)
   11   FORMAT('Results of plant growth simulation: ')
   12   FORMAT(/
     &/,'                Accum',
     &/,'       Number    Temp                                    Leaf',
     &/,'  Day      of  during   Plant  Canopy    Root   Fruit    Area',
     &/,'   of    Leaf  Reprod  Weight  Weight  Weight  weight   Index',
     &/,' Year   Nodes    (oC)  (g/m2)  (g/m2)  (g/m2)  (g/m2) (m2/m2)',
     &  '      Pg',
     &/,' ----  ------  ------  ------  ------  ------  ------  ------',
     &  '  ------')

        WRITE(*,11)
        WRITE(*,12)

        COUNT = 0
        
       ENDIF

!************************************************************************
!************************************************************************
!     RATE CALCULATIONS
!************************************************************************
!      ELSEIF (INDEX(DYN,'RATE') .NE. 0) THEN
!************************************************************************
        IF (DOY == 0) THEN
          RETURN
        ENDIF
        IF (DOY <= DOYP) THEN
            RETURN
        ENDIF
        TMN = 0.5 * (TMAX + TMIN)
        CALL PTS(TMAX,TMIN,PT)
        CALL PGS_CO2(CO2, SWFAC1, SWFAC2,PAR, PD, PT, Lai, Pg)
 
        IF (N .LT. Lfmax) THEN
!         Vegetative phase
          FL = 1.0
          E  = 1.0
          dN = rm * PT

          CALL LAIS(FL,di,PD,EMP1,EMP2,N,nb,SWFAC1,SWFAC2,PT,
     &         dN,p1, sla, dLAI)
          dw = E * (Pg) * PD
          dwc = Fc * dw
          dwr = (1-Fc) * dw
          dwf = 0.0

        ELSE
!         Reproductive phase
          FL = 2.0
          
          IF (TMN .GE. tb .and. TMN .LE. 25) THEN
            di = (TMN-tb)
          ELSE 
            di = 0.0
          ENDIF

          int = int + di
          E = 1.0
          CALL LAIS(FL,di,PD,EMP1,EMP2,N,nb,SWFAC1,SWFAC2,PT,
     &         dN,p1, sla, dLAI)
          dw = E * (Pg) * PD
          
          dwf = dw
          dwc = 0.0
          dwr = 0.0
          dn = 0.0
        ENDIF

!************************************************************************
!************************************************************************
!     INTEGRATION
!************************************************************************
      ELSEIF (INDEX(DYN,'INTEG') .NE. 0) THEN
!************************************************************************
        LAI = LAI + dLAI
        w  = w  + dw
        wc = wc + dwc
        wr = wr + dwr
        wf = wf + dwf

        LAI = MAX(LAI,0.0)
        W   = MAX(W, 0.0)
        WC  = MAX(WC,0.0)
        WR  = MAX(WR,0.0)
        WF  = MAX(WF,0.0)

        N   = N   + dN
        IF (int .GT. INTOT) THEN
          endsim = 1
          WRITE(1,14) doy
   14     FORMAT(/,'  The crop matured on day ',I3,'.')
          RETURN
        ENDIF

!************************************************************************
!************************************************************************
!     OUTPUT
!************************************************************************
      ELSEIF (INDEX(DYN,'OUTPT') .NE. 0) THEN
!************************************************************************
        WRITE(1,20) DOY,n,int,w,wc,wr,wf,lai,Pg
   20   FORMAT(I5,10F8.2)

        IF (COUNT .EQ. 23) THEN
          COUNT = 0
          WRITE(*,30)
   30     FORMAT(2/)
          WRITE(*,12)
        ENDIF

        COUNT = COUNT + 1
        WRITE(*,20) DOY,n,int,w,wc,wr,wf,lai, pg

!************************************************************************
!************************************************************************
!     CLOSE
!************************************************************************
      ELSEIF (INDEX(DYN,'CLOSE') .NE. 0) THEN
!************************************************************************
       CLOSE(1)   

!************************************************************************
!************************************************************************
!     End of dynamic 'IF' construct
!************************************************************************
      ENDIF
!************************************************************************
      RETURN
      END SUBROUTINE PLANT2
************************************************************************



!************************************************************************
!*     Subroutine LAIS
!*     Calculates the canopy leaf area index (LAI)
!!-----------------------------------------------------------------------  
!*     Input:  FL, di, PD, EMP1, EMP2, N, nb, SWFAC1, SWFAC2, PT, dN
!*     Output: dLAI
!*************************************************************************
!      SUBROUTINE LAIS(FL,di,PD,EMP1,EMP2,N,nb,SWFAC1,SWFAC2,PT,
!     &         dN,p1, sla, dLAI)
!
!!-----------------------------------------------------------------------  
!      IMPLICIT NONE
!      SAVE
!      REAL PD,EMP1,EMP2,N,nb,dLAI, SWFAC,a, dN, p1,sla
!      REAL SWFAC1, SWFAC2, PT, di, FL
!!-----------------------------------------------------------------------  
!
!      SWFAC = MIN(SWFAC1, SWFAC2)
!      IF (FL .EQ. 1.0) THEN 
!          a = exp(EMP2 * (N-nb))  
!          dLAI = SWFAC * PD * EMP1 * PT * (a/(1+a)) * dN 
!      ELSEIF (FL .EQ. 2.0) THEN
!
!          dLAI = - PD * di * p1 * sla 
!
!      ENDIF
!!-----------------------------------------------------------------------  
!      RETURN
!      END SUBROUTINE LAIS
!************************************************************************



*****************************************************************************
*     Subroutine PGS_CO2
*     Calculates the canopy gross photosysntesis rate (PG)
******************************************************************************
      SUBROUTINE PGS_CO2(CO2, SWFAC1, SWFAC2,PAR, PD, PT, Lai, Pg)

!-----------------------------------------------------------------------  
      IMPLICIT NONE 
      SAVE
      REAL PAR, Lai, Pg, PT, Y1
      REAL SWFAC1, SWFAC2, SWFAC,ROWSPC,PD
      REAL CO2, PCO2

!-----------------------------------------------------------------------  
!     ROWSP = row spacing
!     Y1 = canopy light extinction coefficient

      SWFAC = MIN(SWFAC1, SWFAC2)
      ROWSPC = 60.0  
      Y1    = 1.5 - 0.768 * ((ROWSPC * 0.01)**2 * PD)**0.1 
      Pg = PT * SWFAC * 2.1 * PAR/PD * (1.0 - EXP(-Y1 * LAI))
      
!     CO2 effect on photosynthesis      
      PCO2 = CO2 / 1233. + 0.73
      Pg = Pg * PCO2
      
!-----------------------------------------------------------------------  
      RETURN
      END SUBROUTINE PGS_CO2
************************************************************************



!************************************************************************
!*     Subroutine PTS
!*     Calculates the factor that incorporates the effect of temperature 
!*     on photosynthesis
!*************************************************************************
!      SUBROUTINE PTS(TMAX,TMIN,PT)
!!-----------------------------------------------------------------------
!      IMPLICIT NONE 
!      SAVE  
!      REAL PT,TMAX,TMIN
!
!!-----------------------------------------------------------------------  
!      PT = 1.0 - 0.0025*((0.25*TMIN+0.75*TMAX)-26.0)**2
!
!!-----------------------------------------------------------------------  
!      RETURN
!      END SUBROUTINE PTS
!************************************************************************
!************************************************************************