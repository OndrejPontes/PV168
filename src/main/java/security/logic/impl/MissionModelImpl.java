package security.logic.impl;


import security.logic.entity.Mission;
import security.logic.exception.ServiceFailureException;
import security.logic.model.MissionModel;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author opontes
 */

public class MissionModelImpl implements MissionModel {

    private final DataSource dataSource;

    public MissionModelImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Mission create(Mission mission) {

        if (mission == null) {
            throw new IllegalArgumentException("Mission is null");
        }
        if (mission.getId() != null) {
            throw new IllegalArgumentException("Mission id is already set");
        }
        if (mission.getName() == null || "".equals(mission.getName())) {
            throw new IllegalArgumentException("Mission name is empty or null");
        }
        if (mission.getTarget() == null || "".equals(mission.getTarget())) {
            throw new IllegalArgumentException("Mission target is empty or null");
        }
        if (mission.getFrom() == null || mission.getTo() == null || mission.getTo().before(mission.getFrom())) {
            throw new IllegalArgumentException("Mission dates are incorrect.");
        }
        addToDatabase(mission);
        return findById(mission.getId());
    }

    @Override
    public void update(Mission mission) {
        if (mission == null) throw new IllegalArgumentException("Mission can't be null");
        if (mission.getId() == null) throw new IllegalArgumentException("Mission with null id cannot be updated");
        if (mission.getName() == null || "".equals(mission.getName()))
            throw new IllegalArgumentException("Mission name is incorrect");
        if (mission.getTarget() == null || "".equals(mission.getTarget()))
            throw new IllegalArgumentException("Mission target is incorrect");
        if (mission.getFrom() == null || mission.getTo() == null || mission.getTo().before(mission.getFrom())) {
            throw new IllegalArgumentException("Mission dates are incorrect.");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE Mission SET name=?,target=?,from_date=?,to_date=? WHERE id=?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, mission.getName());
            statement.setString(2, mission.getTarget());
            statement.setDate(3, new Date(mission.getFrom().getTime()));
            statement.setDate(4, new Date(mission.getTo().getTime()));
            statement.setLong(5, mission.getId());
            if (statement.executeUpdate() != 1) {
                throw new IllegalArgumentException("cannot update mission " + mission);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM Mission WHERE id=?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            if (statement.executeUpdate() != 1) {
                throw new ServiceFailureException("did not delete mission with id =" + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Mission findById(Long id) {
        Mission result = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id,name,target,from_date,to_date FROM Mission WHERE id = ?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Mission mission = resultSetToMission(rs);
                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                                    + "(source id: " + id + ", found " + mission + " and " + resultSetToMission(rs));
                }
                result = mission;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Mission> findAll() {
        List<Mission> result = new ArrayList<Mission>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id,name,target,from_date,to_date FROM Mission", Statement.RETURN_GENERATED_KEYS)) {
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                result.add(resultSetToMission(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void addToDatabase(Mission mission) {
        Connection connection;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(
                    "INSERT INTO Mission (NAME, TARGET, FROM_DATE, TO_DATE) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, mission.getName());
            statement.setString(2, mission.getTarget());
            statement.setDate(3, new Date(mission.getFrom().getTime()));
            statement.setDate(4, new Date(mission.getTo().getTime()));
            int addedRows = statement.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows inserted when " +
                        "trying to insert mission " + mission);
            }
            ResultSet keyRS = statement.getGeneratedKeys();
            mission.setId(getKey(keyRS, mission));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ServiceFailureException serviceFailureException) {
            serviceFailureException.printStackTrace();
        }
    }

    private Long getKey(ResultSet keyRS, Mission mission) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert mission " + mission
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert mission " + mission
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retrieving failed when trying to insert mission " + mission
                    + " - no key found");
        }
    }

    private Mission resultSetToMission(ResultSet rs) throws SQLException {
        Mission mission = new Mission();
        mission.setId(rs.getLong("id"));
        mission.setName(rs.getString("name"));
        mission.setTarget(rs.getString("target"));
        mission.setFrom(rs.getDate("from_date"));
        mission.setTo(rs.getDate("to_date"));
        return mission;
    }
}
