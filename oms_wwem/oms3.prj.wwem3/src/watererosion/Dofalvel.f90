
! @Description("computes the fall velocity for a specific size class particle")
! @Author(name="D.C. Flanagan and J.C. Ascough II")
! @VersionInfo("Last modified: 02-12-2009")

! @Execute
SUBROUTINE DOFALVEL(DIA, SPG, FALL, NPART)

    USE, INTRINSIC :: ISO_C_BINDING
    IMPLICIT NONE

    ! @Description("particle size diameter")
    ! @In
    ! @Out
    REAL(C_DOUBLE), DIMENSION(NPART) :: DIA

    ! @Description("specific gravity")
    ! @In
    REAL(C_DOUBLE), DIMENSION(NPART) :: SPG

    ! @Description("Fall velocity")
    ! @Out
    REAL(C_DOUBLE), DIMENSION(NPART) :: FALL

    ! @Description("Number of particles")
    ! @In
    INTEGER(C_INT) :: NPART

    REAL*8 :: FALVEL
    INTEGER :: I

    DO I = 1, NPART
        DIA(I) = DIA(I) / 1000.0d0
        FALL(I) = FALVEL(SPG(I), DIA(I))
    END DO

END SUBROUTINE
