package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.BGRSServer.DB.Database;
import bgu.spl.net.impl.BGRSServer.SystemCommands.*;
import bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands.*;

import java.util.NoSuchElementException;

public class CRSMessagingProtocol implements MessagingProtocol<Commands> {
    private boolean shouldTerminate = false;
    private String userName = null;

    /**
     * Process the Command sent from the user, using msg.execute() for the different actions per OPCode.
     * Make sure to validate whether the action is allowed, if the user should be already logged in and is the right
     * userType for the action.
     * Make sure to update userName with the currently loggedIn user (null if no user loggedIn)
     *
     * @param msg the received Command
     * @return @ServerCommand (ACK/ERR) with the relevant response for the action
     */
    public Commands process(Commands msg) {
        //In case the command is ACK/ERR - throw an exception - invalid use of process
        if (!ClientCommand.class.isInstance(msg)) {
            throw new IllegalArgumentException("The server can only process Client-2-Server Commands");
        }//TODO:maybe not as an exception
        int opCode = msg.getOpCode();
        /*Validation:
            -If the user should be logged in to preform the command
            -If the user is not allowed to (Admin VS Student).
            -If the user is already logged in and try to log-in/Register
         */
        if (((opCode > 3) & (userName == null)) || ((opCode > 4) && !userTypeVerify(msg)) || ((opCode < 4) & (userName != null))) {
            return new ERR(msg.getOpCode());
        }
        //update the user name in the Command
        if (opCode > 3) {
            ((ClientCommand) msg).setUserName(userName);
        }
        //execute task
        ServerCommand resultOP = ((ClientCommand) msg).execute();
        //if action was successful:
        if (resultOP.getClass() == ACK.class) {
            //if msg is login - update username
            if (msg.getOpCode() == 3) {
                userName = ((Login) msg).getUserName();
            }
            //if msg is logout - update username and shouldTerminate
            if (msg.getOpCode() == 4) {
                shouldTerminate = true;
                userName = null;
            }
        }
        return resultOP;
    }

    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    /**
     * Make sure whether the user (@op.username) is allowed to perform the action or isn't.
     * Admin isn't allowed to perform
     *
     * @param op - the client command the user with to perform
     * @return false if there is the user isn't allowed to use the Command, or else if he is
     */
    private boolean userTypeVerify(Commands op) {
        boolean isAdmin;
        try {
            isAdmin = Database.getInstance().isAdmin(userName);
        } catch (NoSuchElementException e) {
            return false;
        }
        boolean isAdminCommand = AdminCommand.class.isInstance(op);
        if (isAdminCommand!=isAdmin) {
            return false;
        }
        return true;
    }
}
