package security.logic.model;


import security.logic.entity.Agent;
import security.logic.exception.ServiceFailureException;
import java.util.List;

/**
 * @author opontes
 */
public interface AgentModel {
    Agent create(Agent agent) throws ServiceFailureException;
    void update(Agent agent) throws ServiceFailureException;
    void delete(Long id) throws ServiceFailureException;
    Agent findById(Long id) throws ServiceFailureException;
    List<Agent> findAll() throws ServiceFailureException;
}
