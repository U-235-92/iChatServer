package aq.koptev.services.connect;

import aq.koptev.models.connect.Handler;
import aq.koptev.models.connect.Server;
import aq.koptev.models.network.NetObject;
import aq.koptev.models.obj.Client;
import aq.koptev.services.connect.authentication.AuthenticationService;
import aq.koptev.services.connect.registration.RegistrationService;
import aq.koptev.util.ParameterNetObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IdentificationService {

    private AuthenticationService authenticationService;
    private RegistrationService registrationService;
    private ObjectInputStream objectInputStream;
    private Handler handler;
    private boolean isSuccessAuthentication = false;
    public IdentificationService(Server server, Handler handler, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        this.handler = handler;
        this.objectInputStream = objectInputStream;
        authenticationService = new AuthenticationService(server, handler, objectOutputStream);
        registrationService = new RegistrationService(objectOutputStream);
    }

    public void processIdentification() throws IOException, ClassNotFoundException {
        while(!isSuccessAuthentication) {
            NetObject netObject = (NetObject) objectInputStream.readObject();
            Client client = null;
                    switch (netObject.getType()) {
                case REQUEST_AUTHENTICATION:
                    client = NetObject.getObject(netObject.getData(ParameterNetObject.CLIENT));
                    isSuccessAuthentication = authenticationService.processAuthentication(client);
                    break;
                case REQUEST_REGISTRATION:
                    client = NetObject.getObject(netObject.getData(ParameterNetObject.CLIENT));
                    registrationService.processRegistration(client);
                    break;
            }
        }
    }
}
