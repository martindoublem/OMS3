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
subroutine jupgm(id,im,iy,precip,tmin,tmax,rad,prevtmax,nexttmin,wstrs,co2,   &
    & biooptdelta,canheightact,deltabiomass,flatleaf,flatstem,flatstore,      &
    & fphuact,grainf,lai,phuact,root,standleaf,standstem,standstore,tstrs,    &
    & zrootd)
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
    include 'w1pavg.inc'
    include 'w1cli.inc'
    
    include 'upgmmain.inc'
    include 'clai.inc'
    include 'cenvr.inc'
    include 'cgrow.inc'
    
    ! @Description("current day")
    ! @In
    integer(C_INT) :: id
    
    ! @Description("current month")
    ! @In
    integer(C_INT) :: im
    
    ! @Description("current year")
    ! @In
    integer(C_INT) :: iy
    
    ! @Description("daily precipitation")
    ! @Unit("mm")
    ! @In
    real(C_FLOAT) :: precip
    
    ! @Description("minimum daily air temperature")
    ! @Unit("deg C")
    ! @In
    real(C_FLOAT) :: tmin
    
    ! @Description("maximum daily air temperature")
    ! @Unit("deg C")
    ! @In
    real(C_FLOAT) :: tmax
    
    ! @Description("global radiation")
    ! @Unit("MJ/m2")
    ! @In
    real(C_FLOAT) :: rad
    
    ! @Description("maximum daily air temperature of the previous day")
    ! @Unit("deg C")
    ! @In
    real(C_FLOAT) :: prevtmax
    
    ! @Description("minimum daily air temperature of the next day")
    ! @Unit("deg C")
    ! @In
    real(C_FLOAT) :: nexttmin
    
    ! @Description("water stress factor ratio")
    ! @Unit("0-1")
    ! @In
    real(C_FLOAT) :: wstrs
	
	! @Description("the daily atmospheric level of CO2")
    ! @Unit("ppm")
    ! @In
    real(C_FLOAT) :: co2
    
    ! @Description("potential daily biomass production")
    ! @Unit("kg/m^2")
    ! @Out
    real(C_FLOAT) :: biooptdelta
    
    ! @Description("crop height")
    ! @Unit("m")
    ! @Out
    real(C_FLOAT) :: canheightact
    
    ! @Description("stress adjusted daily biomass production")
    ! @Unit("kg/m^2")
    ! @Out
    real(C_FLOAT) :: deltabiomass
    
    ! @Description("crop flat leaf mass")
    ! @Unit("kg/m^2")
    ! @Out
    real(C_FLOAT) :: flatleaf
    
    ! @Description("crop flat stem mass")
    ! @Unit("kg/m^2")
    ! @Out
    real(C_FLOAT) :: flatstem
    
    ! @Description("crop flat storage mass")
    ! @Unit("kg/m^2")
    ! @Out
    real(C_FLOAT) :: flatstore
    
    ! @Description("heat unit index")
    ! @Out
    real(C_FLOAT) :: fphuact
    
    ! @Description("grain fraction of reproductive mass")
    ! @Unit("0-1")
    ! @Out
    real(C_FLOAT) :: grainf
    
    ! @Description("leaf area index based on whole field area")
    ! @Out
    real(C_FLOAT) :: lai
    
    ! @Description("heat unit index")
    ! @Out
    real(C_FLOAT) :: phuact
    
    ! @Description("total crop root mass")
    ! @Unit("kg/m^2")
    ! @Out
    real(C_FLOAT) :: root
    
    ! @Description("crop standing leaf mass")
    ! @Unit("kg/m^2")
    ! @Out
    real(C_FLOAT) :: standleaf
    
    ! @Description("crop standing stem mass")
    ! @Unit("kg/m^2")
    ! @Out
    real(C_FLOAT) :: standstem
    
    ! @Description("crop standing storage mass")
    ! @Unit("kg/m^2")
    ! @Out
    real(C_FLOAT) :: standstore
    
    ! @Description("temperature stress factor ratio")
    ! @Unit("0-1")
    ! @Out
    real(C_FLOAT) :: tstrs
    
    ! @Description("crop root depth")
    ! @Unit("m")
    ! @Out
    real(C_FLOAT) :: zrootd
    
    !
    ! Local variables
    !
    integer(C_INT) :: mature_warn_flg
    
    !
    ! Method
    !
    integer :: julday
    
    am0jd = julday(id,im,iy)
    
    !
    ! Initialize climate
    !
    wwzdpt(id) = precip
    wwtdmx(id) = tmax
    wwtdmn(id) = tmin
    wgrad(id) = rad * 23.895
    
    wwdurpt(id) = 0.0
    wwpeaktpt(id) = 0.0
    wwpeakipt(id) = 0.0
    wwtdpt(id) = 0.0
    
    awzdpt = wwzdpt(id)
    awdurpt = wwdurpt(id)
    awpeaktpt = wwpeaktpt(id)
    awpeakipt = wwpeakipt(id)
    !
    awtdmxprev = prevtmax
    !
    awtdmn = wwtdmn(id)
    awtdmx = wwtdmx(id)
    !
    awtdmnnext = nexttmin
    !
    awtdpt = wwtdpt(id)
    aweirr = rad
    !dayidx = dayidx + 1
    !
    ! calculate air density from temperature and pressure
    !
    awtdav = (awtdmx+awtdmn)/2.
    awdair = 348.56*(1.013-0.1183*(amzele/1000.)+0.0048*(amzele/1000.)**2.)         &
           & /(awtdav+273.1)
    
    ahfwsf(1) = wstrs
    
    co2atmos = co2

    if (am0jd==plant_jday) then
        write (*,*) 'planting date: ',pd,'/',pm,'/',py
        growcrop_flg = .true.
        am0cif = .true.
        am0cgf = .true.
    end if
    !
 
    !  if (am0jd.eq.harvest_jday) then !original way
 
    ! debe added new method of determining when harvest day occurs utilizing the
    ! phenolflg and writing out the value in season.out
    if (((phenolflg==1).and.(hrs(1)/=999).and.am0hrvfl==0).or.                    &
    & ((phenolflg==0).and.(am0jd==harvest_jday))) then
        if (phenolflg==1) then
            write (*,*) 'harvest date: ',hrs(1),hrs(2),hrs(3),hrs(4)
        else if (phenolflg==0) then
            write (*,*) 'harvest date: ',hd,'/',hm,'/',hy
        end if
        am0cgf = .false.
        am0hrvfl = 1     ! debe uncommented this line because it is now needed to prevent crop_endseason
                        ! being called after harvest every day until the end date of simulation.
        !
        call crop_endseason(ac0nam(sr),am0cfl,nslay(sr),ac0idc(sr),acdayam(sr),    &
                        & acthum(sr),acxstmrep(sr),prevstandstem(sr),            &
                        & prevstandleaf(sr),prevstandstore(sr),prevflatstem(sr), &
                        & prevflatleaf(sr),prevflatstore(sr),prevbgstemz(1,sr),  &
                        & prevrootstorez(1,sr),prevrootfiberz(1,sr),prevht(sr),  &
                        & prevstm(sr),prevrtd(sr),prevdayap(sr),prevhucum(sr),   &
                        & prevrthucum(sr),prevgrainf(sr),prevchillucum(sr),      &
                        & prevliveleaf(sr),acdayspring(sr),mature_warn_flg,      &
                        & acycon,acynmu,ies,joints,boots,heads,antss,mats,hrs,   &
                        & phenolflg)
        !debe added acycon to the passing arguments to crop_endseason to allow calculation
        ! of the yield in crop_endseason.
        !debe added phenolflg and several growth stage arrays to be passed to crop_endseason
        ! to print out in season.out. as different crops are tested more growth stage variables
        ! appropriate to each crop may need to be passed to crop_endseason.
    end if
    !
    if (growcrop_flg.eqv..true.) then
        !debe added the following variables for the emerge subroutine to the call to
        ! the callcrop subroutine: seedsw, soilwat, wfpslo, wfpsup, germgdd, ergdd,
        ! cropname, ddap, dgdds, elong, gddday.
        !debe added the variables icli and pd, pm, py to the call to the callcrop
        ! subroutine to enable printing the weather file name and planting date
        ! in emerge.out.
        !
        !debe added tupper and callgdd for the gddcalc subroutine to the call to
        ! the callcrop subroutine. tupper is read in from upgm_crop.dat. callgdd is
        ! initialized in cropinit.
        !debe added canopyflg to callcrop.
        !
        ! (am0jd-plant_jday+1) = current day of crop growth
        ! debe added temperature variables: tbase, toptlo, topup, tupper, canopyflg
        ! debe added passing the new variable 'phenolflg' which is read in from upgm_crop.dat
        ! and the phenological growth stages variables to callcrop which will pass
        ! them on to crop.
        call callcrop(aepa,aifs,am0jd-plant_jday+1,1,antes,antss,blstrs,boots,     &
                    & browns,callgdd,canht,canopyflg,cliname,cots,cropname,        &
                    & dayhtinc,dents,doughs,drs,dummy1,dummy2,ears,ecanht,egdd,    &
                    & emrgflg,ems,endlgs,epods,ergdd,eseeds,first7,fps,fullbs,     &
                    & gddtbg,germgdd,germs,ggdd,gmethod,gpds,growth_stress,halfbs, &
                    & heads,hrs,icli,ies,ies2,infls,joints,lf12s,lf1s,lf2s,lf3s,   &
                    & lf4s,lf8s,mats,maxht,mffls,milks,mpods,mseeds,opens,pchron,  &
                    & pd,phenolflg,pm,py,seedsw,silks,soilwat,srs,tbase,tis,toptlo,&
                    & toptup,tsints,tss,tupper,wfpslo,wfpsup,yelows,co2x,co2y,     &
                    & co2atmos)
 
            !if (am0jd.eq.harvest_jday) growcrop_flg = .false.
            !de and gm changed the above code so that if the input harvest date is
            ! reached before hrs(1) has a value .ne. to 999 when using phenolflg = 1,
            ! simulation will continue until it does reach a harvest date.
        if (((phenolflg==1).and.(hrs(1)/=999)).or.                                 &
        & ((phenolflg==0).and.(am0jd==harvest_jday))) growcrop_flg = .false.
 
    end if
    
    standleaf = acmstandleaf(sr)
    standstem = acmstandstem(sr)
    standstore = acmstandstore(sr)
    flatleaf = acmflatleaf(sr)
    flatstem = acmflatstem(sr)
    flatstore = acmflatstore(sr)
    grainf = acgrainf(sr)
    root = acmrt(sr)
    zrootd = aczrtd(sr)
    
    lai = trad_lai
    phuact = acthucum(sr)
    fphuact = hui
    
    if(upgmflg==0) then
        canheightact = aczht(sr)
    else
        canheightact = canht / 100.0
    end if
    
    tstrs = ts
    
    biooptdelta = pddm
    deltabiomass = ddm
	
    return
end subroutine jupgm