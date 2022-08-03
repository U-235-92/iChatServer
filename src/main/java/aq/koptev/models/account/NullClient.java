package aq.koptev.models.account;

public class NullClient implements Account {

    private String description;

    public NullClient(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
