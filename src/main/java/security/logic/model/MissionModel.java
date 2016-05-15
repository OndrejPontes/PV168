package security.logic.model;


import security.logic.entity.Mission;
import java.util.List;

/**
 * @author opontes
 */
public interface MissionModel {
    Mission create(Mission mission);
    void update(Mission mission);
    void delete(Long id);
    Mission findById(Long id);
    List<Mission> findAll();
}
