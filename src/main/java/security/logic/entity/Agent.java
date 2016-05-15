package security.logic.entity;

/**
 * @author opontes
 */
public class Agent {
    private Long id;
    private String name;
    private int rating;
    
    public Agent() {}

    public Agent(String name, int rating) {
        this.name = name;
        this.rating = rating;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Agent{" + "id=" + id + ", name=" + name + ", rating=" + rating + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Agent agent = (Agent) o;

        if (rating != agent.rating) return false;
        if (!id.equals(agent.id)) return false;
        return name != null ? name.equals(agent.name) : agent.name == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + rating;
        return result;
    }
}
