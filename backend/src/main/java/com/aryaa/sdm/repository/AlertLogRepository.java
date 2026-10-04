package com.aryaa.sdm.repository;
import com.aryaa.sdm.model.AlertLog; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface AlertLogRepository extends JpaRepository<AlertLog,Long>{ List<AlertLog> findTop10ByOrderByTriggeredAtDesc(); }