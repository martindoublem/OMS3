real :: aepa,canht,dayhtinc,ecanht,elrate,gddtbg,germd,maxht,pchron,tbase,     &
    & toptlo,toptup,tupper,wlow,wup,co2atmos
integer,dimension(4) :: aifs,antes,antss,blstrs,boots,browns,cots,dents,doughs,&
    & drs,ears,ems,endlgs,epods,eseeds,fps,fullbs,germs,gpds,halfbs,heads,hrs, &
    & ies,ies2,infls,joints,lf12s,lf1s,lf2s,lf3s,lf4s,lf8s,mats,mffls,milks,   &
    & mpods,mseeds,opens,silks,srs,tis,tsints,tss,yelows
integer :: am0hrvfl,canopyflg,ed,em,emrgflg,end_jday,ey,first7,gmethod,upgmflg,&
    & growth_stress,harvest_jday,hd,hm,hy,i,icli,pd,phenolflg,plant_jday,pm,py,&
    & row,sd,seedsw,sm,sr,start_jday,sy
logical :: callgdd,growcrop_flg
character(80) :: cliname
character(80) :: cropname
character(5),dimension(30) :: dummy1
real,dimension(30) :: dummy2
real,dimension(6) :: egdd,ggdd
real,dimension(4) :: ergdd,germgdd,wfpslo,wfpsup
real,dimension(10) :: co2x,co2y
character(40) :: seedbed,swtype
character(80),dimension(4) :: soilwat

common / upgmmain /                                                            &
     & aepa,canht,dayhtinc,ecanht,elrate,gddtbg,germd,maxht,pchron,tbase,      &
     & toptlo,toptup,tupper,wlow,wup,co2atmos,aifs,antes,antss,blstrs,boots,   &
     & browns,cots,dents,doughs,drs,ears,ems,endlgs,epods,eseeds,fps,fullbs,   &
     & germs,gpds,halfbs,heads,hrs,ies,ies2,infls,joints,lf12s,lf1s,lf2s,lf3s, &
     & lf4s,lf8s,mats,mffls,milks,mpods,mseeds,opens,silks,srs,tis,tsints,tss, &
     & yelows,am0hrvfl,canopyflg,ed,em,emrgflg,end_jday,ey,first7,gmethod,     &
     & upgmflg,growth_stress,harvest_jday,hd,hm,hy,i,icli,pd,phenolflg,        &
     & plant_jday,pm,py,row,sd,seedsw,sm,sr,start_jday,sy,callgdd,growcrop_flg,&
     & cliname,cropname,egdd,ggdd,ergdd,germgdd,wfpslo,wfpsup,co2x,co2y,       &
     & seedbed,swtype,soilwat,dummy2,dummy1