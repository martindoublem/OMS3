! @DLL("Olaf")

! @Execute
SUBROUTINE inc(arr, len) BIND(C, name='fooinc')
    ! @In
    INTEGER(c_int),DIMENSION(len) :: arr
    ! @In
    ! @Out
    INTEGER(c_int),VALUE :: len

    INTEGER :: i
    DO i = 1, len
        arr(i) = arr(i) + 30
    END DO
END SUBROUTINE
