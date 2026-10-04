package com.aryaa.sdm.service;
import com.aryaa.sdm.dto.*; import com.aryaa.sdm.exception.*; import com.aryaa.sdm.model.*; import com.aryaa.sdm.repository.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service @Transactional(readOnly=true)
public class RegionService {
 private final RegionRepository regions; private final HospitalRepository hospitals; private final AlertLogRepository logs; private final EventBroadcastService events;
 public RegionService(RegionRepository r,HospitalRepository h,AlertLogRepository l,EventBroadcastService e){regions=r;hospitals=h;logs=l;events=e;}
 public List<RegionDto> getAllRegions(){return regions.findAll().stream().map(r->RegionDto.from(r,hospitalDtosFor(r.getName()))).toList();}
 public List<Region> getAllRegionEntities(){return regions.findAll();}
 public Region getRegionOrThrow(String name){return regions.findByNameIgnoreCase(name).orElseThrow(()->new ResourceNotFoundException("Unknown region: "+name));}
 public List<Hospital> getHospitals(String name){return hospitals.findByRegionNameIgnoreCaseOrderByIdAsc(name);}
 private List<HospitalDto> hospitalDtosFor(String name){return getHospitals(name).stream().map(HospitalDto::from).toList();}
 @Transactional public AlertResponse alertRegion(String name){Region r=getRegionOrThrow(name);r.setDisasterActive(true);var local=hospitalDtosFor(r.getName());Map<String,List<HospitalDto>> connected=new LinkedHashMap<>();for(String n:r.getConnectedRegions())connected.put(n,hospitalDtosFor(n));int notified=local.size()+connected.values().stream().mapToInt(List::size).sum();logs.save(new AlertLog(r.getName(),notified));AlertResponse out=new AlertResponse(r.getName(),local,connected);events.broadcast("alert",out);return out;}
 @Transactional public RegionDto resolveRegion(String name){Region r=getRegionOrThrow(name);r.setDisasterActive(false);RegionDto out=RegionDto.from(r,hospitalDtosFor(r.getName()));events.broadcast("resolve",out);return out;}
}