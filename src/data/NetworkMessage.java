package data;

import java.io.Serializable;

public abstract class NetworkMessage implements Serializable {

    private static final long serialVersionUID = -4057760186023784992L;

    public static class ClientConnect{
        String userId;

        public ClientConnect(String userId){
            this.userId = userId;
        }
    }

    public static class ClientDisconnect {

    }

    public static class RoomCreate{
        String roomName;
        public RoomCreate(String roomName){
            this.roomName = roomName;
        }
    }

    public static class RoomDelete{
        String targetRoom;
        public RoomDelete(String targetRoom){
            this.targetRoom = targetRoom;
        }
    }

    public static class RoomJoin{
        String targetRoom;
        public RoomJoin(String targetRoom){
            this.targetRoom = targetRoom;
        }
    }

    public static class RoomLeave{
        String targetRoom;
        public RoomLeave(String targetRoom){
            this.targetRoom = targetRoom;
        }
    }

    public static class UserNameChange{
        String newName;
        String userId;

        public UserNameChange(String newName, String userId){
            this.newName = newName;
            this.userId = userId;
        }
    }

}//END OF CLASS
