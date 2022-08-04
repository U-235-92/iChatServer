package aq.koptev.models.account;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class NullClient implements Account {

    private String description;

    public NullClient() {}

    public NullClient(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(description);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        description = (String) in.readObject();
    }
}
