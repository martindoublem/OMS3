!
! This file is part of the AgroEcoSystem-Watershed (AgES-W) model component
! collection. AgES-W components are derived from multiple agroecosystem models
! including J2K and J2K-SN (FSU-Jena, DGHM, Germany), SWAT (USDA-ARS, USA), 
! WEPP (USDA-ARS, USA), RZWQM2 (USDA-ARS, USA), and others.
!  
! The AgES-W model is free software; you can redistribute the model and/or 
! modify the components under the terms of the GNU General Public License as 
! published by the Free Software Foundation, either version 3 of the License, 
! or (at your option) any later version.
! 
! This program is distributed in the hope that it will be useful, but WITHOUT 
! ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
! FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
! more details. 
!  
! You should have received a copy of the GNU General Public License along with
! this program. If not, see <http://www.gnu.org/licenses/>.
!
! 
! Description ("Add jupgm module definition here")
! Author (name= "Robert Streetman, Olaf David", contact= "jim.ascough@ars.usda.gov")
! Keywords ("jupgm")
! Bibliography ("Insert bibliography here")      
! SourceInfo ("http://javaforge.com/scm/file/3979484/default/src/upgm/jupgm.f90")
! License ("http://www.gnu.org/licenses/gpl.html")
! Status (Status.TESTED)
! Documentation ("src/upgm/Upgm.xml")

! @Execute
subroutine jupgminit(cropxmlfile,cropxmlfile_len,upgmcropfile,                &
    & upgmcropfile_len,upgmmgmtfile,upgmmgmtfile_len,canopyhtoutfile,         &
    & canopyhtoutfile_len,cdbugoutfile,cdbugoutfile_len,cropoutfile,          &
    & cropoutfile_len,emergeoutfile,emergeoutfile_len,inptoutfile,            &
    & inptoutfile_len,phenoloutfile,phenoloutfile_len,seasonoutfile,          &
    & seasonoutfile_len,shootoutfile,shootoutfile_len)
        
    use, intrinsic :: ISO_C_BINDING
    implicit none
    
    include 'p1werm.inc'
    include 'precision.inc'
    include 'command.inc'
    include 'file.fi'
    include 'w1clig.inc'
    include 's1layr.inc'
    include 's1dbc.inc'
    include 's1dbh.inc'
    include 's1phys.inc'
    include 'd1glob.inc'
    include 'c1gen.inc'
    include 'm1flag.inc'
    include 'm1sim.inc'
    include 'm1dbug.inc'
    include 'h1hydro.inc'
    include 'h1temp.inc'
    include 'c1info.inc'
    include 'c1db1.inc'
    include 'c1db2.inc'
    include 'w1wind.inc'
    include 'c1glob.inc'
    include 'h1et.inc'
    include 's1sgeo.inc'
    include 'h1db1.inc'
    include 's1agg.inc'
    include 'prevstate.inc'
    
    include 'upgmmain.inc'
       
    ! @Description("Cropxml.dat file")
    ! @In
    character(kind=C_CHAR, len=cropxmlfile_len) :: cropxmlfile
    integer(C_INT), value, intent(in) :: cropxmlfile_len
    
    ! @Description("Upgm_crop.dat file")
    ! @In
    character(kind=C_CHAR, len=upgmcropfile_len) :: upgmcropfile
    integer(C_INT), value, intent(in) :: upgmcropfile_len
    
    ! @Description("Upgm_mgmt.dat file")
    ! @In
    character(kind=C_CHAR, len=upgmmgmtfile_len) :: upgmmgmtfile
    integer(C_INT), value, intent(in) :: upgmmgmtfile_len
    
    ! @Description("Upgm_mgmt.dat file")
    ! @In
    character(kind=C_CHAR, len=canopyhtoutfile_len) :: canopyhtoutfile
    integer(C_INT), value, intent(in) :: canopyhtoutfile_len
    
    ! @Description("Upgm_mgmt.dat file")
    ! @In
    character(kind=C_CHAR, len=cdbugoutfile_len) :: cdbugoutfile
    integer(C_INT), value, intent(in) :: cdbugoutfile_len
    
    ! @Description("Upgm_mgmt.dat file")
    ! @In
    character(kind=C_CHAR, len=cropoutfile_len) :: cropoutfile
    integer(C_INT), value, intent(in) :: cropoutfile_len
    
    ! @Description("Upgm_mgmt.dat file")
    ! @In
    character(kind=C_CHAR, len=emergeoutfile_len) :: emergeoutfile
    integer(C_INT), value, intent(in) :: emergeoutfile_len
    
    ! @Description("Upgm_mgmt.dat file")
    ! @In
    character(kind=C_CHAR, len=inptoutfile_len) :: inptoutfile
    integer(C_INT), value, intent(in) :: inptoutfile_len
    
    ! @Description("Upgm_mgmt.dat file")
    ! @In
    character(kind=C_CHAR, len=phenoloutfile_len) :: phenoloutfile
    integer(C_INT), value, intent(in) :: phenoloutfile_len
    
    ! @Description("Upgm_mgmt.dat file")
    ! @In
    character(kind=C_CHAR, len=seasonoutfile_len) :: seasonoutfile
    integer(C_INT), value, intent(in) :: seasonoutfile_len
    
    ! @Description("Upgm_mgmt.dat file")
    ! @In
    character(kind=C_CHAR, len=shootoutfile_len) :: shootoutfile
    integer(C_INT), value, intent(in) :: shootoutfile_len
    
    
    !
    ! Method
    !
    integer :: julday

    row = 4
    i = 0
    elrate = 0.0
    germd = 0.0
    wlow = 0.0
    wup = 0.0
    seedbed = ''
    swtype = ''
    water_stress_max = 0.0 ! command line parameter in full WEPS, added 8/11/15
    !
    data icli/0/
    !
    ! open required input files
    !
    call fopenk(1,cropxmlfile,'old')      ! open weps crop parameter file
    ! call fopenk(luicli,'cligen.cli','old')  ! open cligen climate file
    call fopenk(3,upgmmgmtfile,'old')    ! open management file
    !call fopenk(4,'upgm_stress.dat','old')  ! open water stress file
    !call fopenk(7,'upgm_cli.dat','old')     ! open historical climate file
    call fopenk(8,upgmcropfile,'old')    ! open upgm crop file
    !
    !debe added these variables to be initialized. added canopyflg for determining
    ! which method of calculating canopy height will be used. added dayhtinc to get
    ! the daily increase in height when using the phenologymms method of calculating
    ! canopy height.
    call cropinit(1,aepa,aifs,antes,antss,blstrs,boots,browns,callgdd,canopyflg,    &
                & cliname,cots,cropname,dayhtinc,dents,doughs,drs,dummy1,dummy2,    &
                & ears,ecanht,egdd,emrgflg,ems,endlgs,epods,ergdd,eseeds,first7,fps,&
                & fullbs,gddtbg,germgdd,germs,ggdd,gmethod,gpds,growth_stress,      &
                & halfbs,heads,hrs,ies,ies2,infls,joints,lf1s,lf12s,lf2s,lf3s,lf4s, &
                & lf8s,mats,maxht,mffls,milks,mpods,mseeds,opens,pchron,phenolflg,  &
                & seedsw,silks,soilwat,srs,tbase,tis,toptlo,toptup,tsints,tss,      &
                & tupper,wfpslo,wfpsup,yelows,co2atmos,co2x,co2y)
 
    !debe added growth_stress because it is now read in. debe added temperature
    ! variables, cropname and gmethod to be initialized in cropinit.
    !
    ! not previously called jcaii  7/28/08
    cook_yield = 1     ! default to using functional yield/residue ratio info
    !
    sr = 1
    !
    growcrop_flg = .false.
    am0cif = .false.       ! flag to initialize crop initialization routines (set to true on planting date)
    am0cfl = 1             ! flag to specify if detailed (submodel) output file should be generated
    am0cgf = .false.       ! supposed to indicate a growing crop
    !am0hrvfl = 0          ! harvest flag (default is off)
    am0hrvfl = 0
                !debe turned it on
    acxrow(1) = 0.76       ! row spacing (m)
    ahfwsf(1) = 1.0        ! water stress factor
    !
    ! number of soil layers
    !
    nslay(1) = 1
    !
    !set thickness and depth of first layer (defaulting to a value deeper than
    ! roots will reach)
    !
    aszlyt(1,1) = 1000.0
    aszlyd(1,1) = 1000.0
    !
    ! assign values to some soil layer properties
    !
    asfcce(1,1) = 0.0
    asfcec(1,1) = 0.0
    asfom(1,1) = 3.0
    asftan(1,1) = 0.0
    asftap(1,1) = 0.0
    asmno3(1) = 0.0
    asfcla(1,1) = 20.0
    admbgz(1,1,1) = 0.0
    asdblk(1,1) = 1.0
    asfsan(1,1) = 40.0
    asfsil(1,1) = 40.0
    !
    ahtsav(1,1) = 20.0
    ahtsmx(1,1) = 24.0
    ahtsmn(1,1) = 22.0
    ahfice(1,1) = 0.0
    !
    amzele = 100.0    ! default simulation site elevation (m)
    amalat = -38.0
    !
    am0cdb = 1        ! set crop debug output flag (default to no output)
    !
    awudmx = 10.0     ! set max daily wind speed
    awudmn = 1.0      ! set min daily wind speed
    awadir = 0.0      ! set wind dir
    awhrmx = 12.0     ! set wind dir
    !
    awrrh = 0.0       ! relative humidity?
    !
    acthucum(1) = 0.0 ! initialize accumulated heat units
    acmst(1) = 0.0    ! initialize total standing crop mass
    acmrt(1) = 0.0    ! initialize total root crop mass
    acxstmrep = 0.0   ! initialize repesentative stem dia.
    !
    ahzeta = 0.0      ! initialize actual evapotranspiration
    ahzetp = 0.0      ! initialize potential evapotranspiration
    ahzpta = 0.0      ! initialize actual plant transpiration
    ahzea = 0.0       ! initialize bare soil evaporation
    ahzep = 0.0       ! initialize potential bare soil evaporation
    ahzptp = 0.0      ! initialize potential plant transpiration
    !
    as0rrk(1) = 0.0
    ! aslrrc(1) = 0.0 ! initialize random roughness parms
    ! aslrr(1) = 0.0  ! these are not used and are commented out jcaii 4/30/2013
    ahrsk(1,1) = 0.0  ! saturated soil hydraulic conductivity
    ahrwc(1,1) = 0.0  ! soil water content
    ah0cb(1,1) = 0.0
    aheaep(1,1) = 0.0
    !
    !soil layer water content variables
    !
    ahrwcs(1,1) = 0.0
    ahrwca(1,1) = 0.0
    ahrwcf(1,1) = 0.0
    ahrwcw(1,1) = 0.0
    !
    !soil layer aggregate size distribution stuff
    !
    aslagm(1,1) = 0.0
    as0ags(1,1) = 0.0
    aslagn(1,1) = 0.0
    aslagx(1,1) = 0.0
    aseags(1,1) = 0.0
    !
    ! initialize math precision global variables
    !
    max_real = huge(1.0)*0.999150
    max_arg_exp = log(max_real)
    !
    !
    ! read in plant parameters from cropxml.dat
    !
    read (1,' (a80) ') ac0nam(1)
    read (1,*) acdpop(1),acdmaxshoot(1),acbaflg(1),acytgt(1),acbaf(1),acyraf(1),    &
             & achyfg(1),acynmu(1)
    read (1,*) acywct(1),acycon(1),ac0idc(1),acgrf(1),ac0ck(1),acehu0(1),aczmxc(1), &
             & ac0growdepth(1)
    read (1,*) aczmrt(1),actmin(1),actopt(1),acthudf(1),actdtm(1),acthum(1),        &
             & ac0fd1(1,1),ac0fd2(1,1)
    read (1,*) ac0fd1(2,1),ac0fd2(2,1),actverndel(1),ac0bceff(1),ac0alf(1),ac0blf(1)&
             & ,ac0clf(1),ac0dlf(1)
    read (1,*) ac0arp(1),ac0brp(1),ac0crp(1),ac0drp(1),ac0aht(1),ac0bht(1),ac0ssa(1)&
             & ,ac0ssb(1)
    read (1,*) ac0sla(1),ac0hue(1),ac0transf(1),ac0diammax(1),ac0storeinit(1),      &
             & ac0shoot(1),acfleafstem(1),acfshoot(1)
    read (1,*) acfleaf2stor(1),acfstem2stor(1),acfstor2stor(1),acrbc(1),            &
             & acdkrate(1,1),acdkrate(2,1),acdkrate(3,1),acdkrate(4,1)
    read (1,*) acdkrate(5,1),acxstm(1),acddsthrsh(1),accovfact(1),acresevapa(1),    &
             & acresevapb(1),acyld_coef(1),acresid_int(1)
    !
    !read management information from upgm_mgmt.dat. currently it includes
    ! starting and ending day, month, and year for planting and harvest.
    !
    read (3,*) sd,sm,sy,ed,em,ey
    read (3,*) pd,pm,py,hd,hm,hy
 
    start_jday = julday(sd,sm,sy)
 
    end_jday = julday(ed,em,ey)
 
    plant_jday = julday(pd,pm,py)
    harvest_jday = julday(hd,hm,hy)
 
    !set cropname equal to ac0nam and pass it on through to emerge
    cropname = ac0nam(1)
    print *,'crop=',cropname
 
    ! ***** emergence *****
    !
    !debe added canopyflg
    !read in canopyflg and emergence data for the crop from upgm_crop.dat.
    !debe added reading in phenolflg from upgm_crop.dat
 
    read (8,*) canopyflg,emrgflg,phenolflg,seedbed
    if (seedbed=='Optimum') then
      seedsw = 1          !set seedsw = to a real number. changed back to integer 2/23/11
    else if (seedbed=='Medium') then
      seedsw = 2
    else if (seedbed=='Dry') then
      seedsw = 3
    else if (seedbed=='Plantedindust') then
      seedsw = 4
    end if
    
    ! force All flags to be either 0 or 1. nathan 8/13/2015
    if(canopyflg==0.and.emrgflg==0.and.phenolflg==0) then
        upgmflg = 0
    else
        upgmflg = 1
        canopyflg = 1
        emrgflg = 1
        phenolflg = 1
    end if
    
    print *,'seedbed = ',seedbed
    print *,'canopyflg = ',canopyflg,'emrgflg = ',emrgflg,'phenolflg = ',phenolflg
                                       !, 'seedsw = ', seedsw
    !
    ! put these values into 5 one dimensional arrays.
    do i = 1,row
      read (8,*) swtype          !swtype = soil moisture condition.
      soilwat(i) = swtype
      read (8,*) wlow            !wlow = lower range of soil moisture
      wfpslo(i) = wlow
      read (8,*) wup             !wup = upper range of soil moisture
      wfpsup(i) = wup
      read (8,*) germd           !germd = gdd's for germination at soil moisture
      germgdd(i) = germd
      read (8,*) elrate          !elrate = elongation for emergence
      ergdd(i) = elrate
 
    end do
 
    !print*, 'soilwat(i) = ', soilwat(i)
    !
    !
    ! ***** phenology *****
    !
    !the following is read in whether leaf number or gdd is used.
    ! read in phenology parameters and 4 temperature values from upgm_crop.dat.
    read (8,*) pchron
    read (8,*) tbase
    read (8,*) toptlo
    read (8,*) toptup
    read (8,*) tupper
 
    ! read in method of calculating gdd (gmethod) from upgm_crop.dat
    read (8,*) gmethod
 
    !debe added reading in maxht value for canopy height subroutine.
    !debe added ecanht for height in phase 1 of canopy height.
    read (8,*) maxht,ecanht
    print *,'maxht = ',maxht,'ecanht = ',ecanht
 
    !debe added reading in growth_stress to set which kind of stress to be used:
    ! 0=no stress, 1=water stress only, 2=temp stress only,
    ! 3=min of water and temp stress.
    read (8,*) growth_stress
    print *,'growth_stress = ',growth_stress
    !debe changed dimensions of dummy1 and dummy2 to allow both non-stresseed
    ! and stressed values to be read in.
    do i = 1,30
      read (8,*) dummy1(i),dummy2(i)
      if (dummy1(i)=='ln'.or.dummy1(i)=='ls') dummy2(i) = dummy2(i)*pchron
    end do
    
    !debe added for CO2 table
    !do k = 1, 10
        !read (9,*) co2x(k),co2y(k)
        !print *, 'co2x = ', co2x, 'co2y = ', co2y 
    !end do
    
    co2x(1) = 0.0
    co2y(1) = 0.0
    co2x(2) = 220.0
    co2y(2) = 0.71
    co2x(3) = 330.0
    co2y(3) = 1.0
    co2x(4) = 440.0
    co2y(4) = 1.08
    co2x(5) = 550.0
    co2y(5) = 1.17
    co2x(6) = 660.0
    co2y(6) = 1.25
    co2x(7) = 770.0
    co2y(7) = 1.32
    co2x(8) = 880.0
    co2y(8) = 1.38
    co2x(9) = 990.0
    co2y(9) = 1.43
    co2x(10) = 9999.0
    co2y(10) = 1.5
 
    if (am0cfl>0) then
      call fopenk(17,cropoutfile,'unknown')    ! daily crop output of most state variables
      call fopenk(59,seasonoutfile,'unknown')  ! seasonal summaries of yield and biomass
      call fopenk(60,inptoutfile,'unknown')    ! echo crop input data
      call fopenk(62,shootoutfile,'unknown')   ! crop shoot output
      call fopenk(63,emergeoutfile,'unknown')  ! debe added for emergence output
      call fopenk(64,phenoloutfile,'unknown')  ! debe added for phenology output
      call fopenk(65,canopyhtoutfile,'unknown')
                                              !debe added for canopy height output
      call cpout                              ! print headings for crop output files
    end if
    !
    if (am0cfl>1) call fopenk(61,'allcrop.prn','unknown')     ! main crop debug output file
    if (am0cdb>0) call fopenk(27,cdbugoutfile,'unknown')       ! crop submodel debug output file
    
    !close input files
    close(1)
    close(3)
    close(8)
    
    return
end subroutine jupgminit