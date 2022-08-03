package aq.koptev.services.connect;

import aq.koptev.models.Handler;
import aq.koptev.models.account.Account;
import aq.koptev.services.connect.authentication.AuthenticationService;
import aq.koptev.services.connect.registration.RegistrationService;
import aq.koptev.util.Command;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IdentificationService {

    private AuthenticationService authenticationService;
    private RegistrationService registrationService;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Handler handler;
    private boolean isSuccessIdentification = false;
    public IdentificationService(Handler handler, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        this.handler = handler;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    public void processIdentification() throws IOException, ClassNotFoundException {
        while(!isSuccessIdentification) {
            Account account = (Account) objectInputStream.readObject();
            String incomingLogin = account.getLogin();
            if(incomingLogin.startsWith(Command.REGISTRATION.getCommand())) {
                account = registrationService.processRegistration(account);
                sendAccount(account);
            } else if(incomingLogin.startsWith(Command.AUTHENTICATION.getCommand())) {
                account = authenticationService.processAuthentication(handler, account);
                if(account.getDescription() == null) {
                    isSuccessIdentification = true;
                }
                sendAccount(account);
            }
        }
    }

    private void sendAccount(Account account) throws IOException {
        objectOutputStream.writeObject(account);
    }
}
