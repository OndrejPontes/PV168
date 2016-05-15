package security.logic.model;


import security.logic.entity.Agent;
import security.logic.entity.Manager;
import security.logic.entity.Mission;
import security.logic.exception.ServiceFailureException;
import java.util.List;

/**
 * @author opontes
 */
public interface ManagerModel {
    Manager create(Manager manager) throws ServiceFailureException;
    void update(Manager manager) throws ServiceFailureException;
    void delete(Long id) throws ServiceFailureException;
    Manager findById(Long id) throws ServiceFailureException;
    List<Manager> findAll() throws ServiceFailureException;
    List<Mission> findMissionsByAgent(Long id) throws ServiceFailureException;
    List<Agent> findAgentsByMission(Long id) throws ServiceFailureException;
}
