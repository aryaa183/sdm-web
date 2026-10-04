package com.aryaa.sdm.repository;
import com.aryaa.sdm.model.Hospital; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.List;
public interface HospitalRepository extends JpaRepository<Hospital,Long>{
 List<Hospital> findByRegionNameIgnoreCaseOrderByIdAsc(String regionName);
 @Modifying @Query("UPDATE Hospital h SET h.bedsAvailable=h.bedsAvailable-1 WHERE h.id=:id AND h.bedsAvailable>0") int tryOccupyBed(@Param("id")Long id);
 @Modifying @Query("UPDATE Hospital h SET h.bedsAvailable=h.bedsAvailable+1 WHERE h.id=:id AND h.bedsAvailable<h.totalBeds") int tryReleaseBed(@Param("id")Long id);
}