package security.logic.entity;


import java.util.Date;

/**
 * @author opontes
 */
public class Mission {
    private Long id;
    private String name;
    private String target;
    private Date from;
    private Date to;

    public Mission(){}

    public Mission(String name, String target, Date from, Date to) {
        this.name = name;
        this.target = target;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "\nMission:\n" +
                "- id: " + id.toString() + "\n" +
                "- name: " + name + "\n" +
                "- target: " + target;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mission mission = (Mission) o;

        if (id != null ? !id.equals(mission.id) : mission.id != null) return false;
        if (!name.equals(mission.name)) return false;
        if (!target.equals(mission.target)) return false;
        if (!from.equals(mission.from)) return false;
        return to.equals(mission.to);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }
}
