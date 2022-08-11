package aq.koptev.i.services.connect;

import aq.koptev.i.models.Handler;
import aq.koptev.i.models.Server;
import aq.koptev.i.models.NetObject;
import aq.koptev.i.models.Client;
import aq.koptev.i.services.connect.authentication.AuthenticationService;
import aq.koptev.i.services.connect.registration.RegistrationService;
import aq.koptev.i.util.ParameterNetObject;

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
