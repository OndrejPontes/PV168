package security.logic.impl;


import security.logic.entity.Agent;
import security.logic.exception.ServiceFailureException;
import security.logic.model.AgentModel;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @author martin
 */
public class AgentModelImpl implements AgentModel {

    private final DataSource dataSource;

    public AgentModelImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Agent create(Agent agent) throws ServiceFailureException {

        validateObject(agent);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO AGENT (name, rating) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, agent.getName());
            preparedStatement.setInt(2, agent.getRating());
            int addedRows = preparedStatement.executeUpdate();
            correctNumOfAddedRows(addedRows);

            ResultSet keyResultSet = preparedStatement.getGeneratedKeys();
            agent.setId(getKey(keyResultSet, agent));
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting into Agent " + agent, ex);
        }

        return agent;
    }

    @Override
    public void update(Agent agent) throws ServiceFailureException {
        validateObject(agent);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE Agent SET name=?,rating=? WHERE id=?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, agent.getName());
            statement.setInt(2, agent.getRating());
            statement.setLong(3, agent.getId());
            if (statement.executeUpdate() != 1) {
                throw new IllegalArgumentException("Can not update agent" + agent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) throws ServiceFailureException {

        if (validateObject(id)) {
            throw new IllegalArgumentException("Id can't be null");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM Agent WHERE id=?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            if (statement.executeUpdate() != 1) {
                throw new ServiceFailureException("did not delete grave with id =" + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Agent findById(Long id) throws ServiceFailureException {

        if (validateObject(id)) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT id,name,rating FROM agent WHERE id = ?")) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Agent agent = resultSetToAgent(resultSet);

                if (resultSet.next()) {
                    throw new ServiceFailureException("More objects with same id: "
                            + id + " have been found. 1: " + agent + "\n2: "
                            + resultSetToAgent(resultSet));
                }

                return agent;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when selecting agent with id " + id, ex);
        }
    }

    @Override
    public List<Agent> findAll() throws ServiceFailureException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT id,name,rating FROM agent")) {

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Agent> ret = new ArrayList<>();
            while (resultSet.next()) {
                ret.add(resultSetToAgent(resultSet));
            }

            return ret;

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error while selecting all agents", ex);
        }
    }

    private Long getKey(ResultSet keyResultSet, Agent agent) throws SQLException, ServiceFailureException {
        if (keyResultSet.next()) {
            if (keyResultSet.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Error when inserting into " + agent
                        + " key fields count is wrong: " + keyResultSet.getMetaData().getColumnCount());
            }

            Long result = keyResultSet.getLong(1);
            if (keyResultSet.next()) {
                throw new ServiceFailureException("Error when inserting into " + agent + " more keys founded");
            }
            return result;
        } else {
            throw new ServiceFailureException("Error when insertin into " + agent + " no key found");
        }
    }

    private Agent resultSetToAgent(ResultSet resultSet) throws SQLException {
        Agent agent = new Agent();
        agent.setId(resultSet.getLong("id"));
        agent.setName(resultSet.getString("name"));
        agent.setRating(resultSet.getInt("rating"));
        return agent;
    }



    private boolean validateObject(Long id) {
        return id == null;
    }

    private void validateObject(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("Agent can't be null.");
        }
        if (agent.getName() == null || "".equals(agent.getName())) {
            throw new IllegalArgumentException("Agent's name cant be null.");
        }
    }

    private void correctNumOfAddedRows(int addedRows) throws ServiceFailureException {
        if (addedRows != 1) {
            throw new ServiceFailureException("Number of added rows must be one, you add " + addedRows);
        }
    }
}
