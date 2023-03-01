! @Execute      
    subroutine hyd2ero(erosionsumint,erosiondurre,effint,effdrr,runoff,slplen,rolgth)
     
    USE, INTRINSIC :: ISO_C_BINDING
    IMPLICIT NONE     
     
    ! @In
    REAL(C_DOUBLE) :: erosionsumint

    ! @In
    REAL(C_DOUBLE) :: erosiondurre

    ! @Out
    REAL(C_DOUBLE) :: effint
    
    ! @Out
    REAL(C_DOUBLE) :: effdrr
    
    ! @Out
    REAL(C_DOUBLE) :: runoff
    
    ! @In
    REAL(C_DOUBLE) :: slplen

    ! @Out
    REAL(C_DOUBLE) :: rolgth
    
!      real*8 erosionsumint,erosiondurre,effint,effdrr,runoff
!      real*8 slplen,rolgth
!
!     Compute effective rainfall intensity needed by
!     erosion component, and effective duration of rainfall
!
      if (erosiondurre.gt.0.0d0) then
        effint = erosionsumint/erosiondurre
        effdrr = erosiondurre
!        print*,'init: ',effint, erosionsumint, erosiondurre
      else
        effint = 0.0d0
        effdrr = 0.0d0
      endif

!     Do unit conversion here because OMS cannot currently
!     do a conversion from "cm" to "m" and "h" to "s".

      effint = effint / 360000.0d0
      effdrr = effdrr * 3600.0d0
      runoff = runoff / 100.0d0

!     Set the value for ROLGTH, based on whether there is runon
!     and/or runoff to the current plane.  At the moment, we do
!     not yet have real values of QIN and QOUT from surface
!     hydrology, so in the meantime, make settings based on
!     runoff.
      if (runoff.gt.0.0d0) then
        rolgth = slplen
      else
        rolgth = 0.0d0
      endif

!     The following is how the code should be, if we had the
!     actual QIN and QOUT values.  Still need to have a way to
!     calculate ROLGTH properly when QIN>0 and QOUT=0, instead
!     of setting value to 0.5.    dcf   2-24-2006
!     if (qout.gt.0.0d0) then
!       rolgth = slplen
!     elseif (qin.gt.0.0d0) then
!       rolgth = slplen*0.5d0     ! needs a real calculation for this
!     else
!       rolgth = 0.0d0
!     endif
!       
      return
      end
