package security.logic.entity;

/**
 * @author opontes
 */
public class Manager {
    private Long id;
    private Long agent;
    private Long mission;

    public Manager() {
    }

    public Manager(Long agent, Long mission) {
        this.agent = agent;
        this.mission = mission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgent() {
        return agent;
    }

    public void setAgent(Long agent) {
        this.agent = agent;
    }

    public Long getMission() {
        return mission;
    }

    public void setMission(Long mission) {
        this.mission = mission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Manager manager = (Manager) o;

        if (id != null ? !id.equals(manager.id) : manager.id != null) return false;
        if (agent != null ? !agent.equals(manager.agent) : manager.agent != null) return false;
        return mission != null ? mission.equals(manager.mission) : manager.mission == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (agent != null ? agent.hashCode() : 0);
        result = 31 * result + (mission != null ? mission.hashCode() : 0);
        return result;
    }
}
