package security.logic.impl;


import security.logic.entity.Agent;
import security.logic.entity.Manager;
import security.logic.entity.Mission;
import security.logic.exception.ServiceFailureException;
import security.logic.model.AgentModel;
import security.logic.model.ManagerModel;
import security.logic.model.MissionModel;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author opontes
 */
public class ManagerModelImpl implements ManagerModel {

    private final DataSource dataSource;

    public ManagerModelImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Manager create(Manager manager) throws ServiceFailureException {

        validateObject(manager);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO MANAGER (agent, mission) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, manager.getAgent());
            preparedStatement.setLong(2, manager.getMission());
            int addedRows = preparedStatement.executeUpdate();
            correctNumberOfAddedRows(addedRows);

            ResultSet keyResultSet = preparedStatement.getGeneratedKeys();
            manager.setId(getKey(keyResultSet, manager));

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error while creating " + manager, ex);
        }
        return manager;
    }

    @Override
    public void update(Manager manager) throws ServiceFailureException {

        validateObject(manager);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE Manager SET agent=?,mission=? WHERE id=?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, manager.getAgent());
            statement.setLong(2, manager.getMission());
            statement.setLong(3, manager.getId());
            if (statement.executeUpdate() != 1) {
                throw new IllegalArgumentException("cannot update impl " + manager);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) throws ServiceFailureException {

        if (validateId(id)) {
            throw new IllegalArgumentException("Id must be greater or equal to zero");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM Manager WHERE id=?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            if (statement.executeUpdate() != 1) {
                throw new ServiceFailureException("Did not delete impl with id =" + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Manager findById(Long id) throws ServiceFailureException {
        Manager result = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id,agent,mission FROM Manager WHERE id = ?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Manager manager = resultSetToManager(rs);
                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                                    + "(source id: " + id + ", found " + manager + " and " + resultSetToManager(rs));
                }
                result = manager;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Manager> findAll() throws ServiceFailureException {
        List<Manager> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id,agent,mission FROM Manager", Statement.RETURN_GENERATED_KEYS)) {
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                result.add(resultSetToManager(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Agent> findAgentsByMission(Long id) throws ServiceFailureException {
        AgentModel agentModel = new AgentModelImpl(dataSource);
        List<Agent> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id,agent,mission FROM Manager WHERE mission=?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                result.add(agentModel.findById(rs.getLong("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Mission> findMissionsByAgent(Long id) throws ServiceFailureException {
        MissionModel missionModel = new MissionModelImpl(dataSource);
        List<Mission> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id,agent,mission FROM Manager WHERE agent=?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                result.add(missionModel.findById(rs.getLong("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void validateObject(Manager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Manager can't be null.");
        } else if (validateId(manager.getAgent())) {
            throw new IllegalArgumentException("Agent's id must be greater or equals to 0");
        } else if (validateId(manager.getMission())) {
            throw new IllegalArgumentException("Mission's id must be greater or equals to 0");
        }
    }

    private boolean validateId(Long id) {
        return id == null || id < 0;
    }


    private void correctNumberOfAddedRows(int addedRows) throws ServiceFailureException {
        if (addedRows != 1) {
            throw new ServiceFailureException("Number of added rows must be one, you add " + addedRows);
        }
    }

    private Long getKey(ResultSet keyResultSet, Manager manager) throws SQLException, ServiceFailureException {
        if (keyResultSet.next()) {
            if (keyResultSet.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Error when inserting into " + manager
                        + " key fields count is wrong: " + keyResultSet.getMetaData().getColumnCount());
            }

            Long result = keyResultSet.getLong(1);
            if (keyResultSet.next()) {
                throw new ServiceFailureException("Error when inserting into " + manager + " more keys founded");
            }
            return result;
        } else {
            throw new ServiceFailureException("Error when insertin into " + manager + " no key found");
        }
    }

    private Manager resultSetToManager(ResultSet rs) throws SQLException {
        Manager m = new Manager();
        m.setId(rs.getLong("id"));
        m.setAgent(rs.getLong("agent"));
        m.setMission(rs.getLong("mission"));
        return m;
    }
}
