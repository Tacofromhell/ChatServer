package data;

import java.io.Serializable;

public abstract class NetworkMessage implements Serializable {
    private static final long serialVersionUID = -4057760186023784992L;

    public static class ClientConnect extends NetworkMessage {
        String userId;

        public ClientConnect(String userId) {
            this.userId = userId;
        }
    }

    public static class InitializeClient extends NetworkMessage {
        public String userId;

        public InitializeClient(String userId) {
            this.userId = userId;
        }
    }

    public static class ClientDisconnect extends NetworkMessage {
        String userId;

        public ClientDisconnect(String userId) {
            this.userId = userId;
        }
    }

    public static class RoomNameExists extends NetworkMessage {
    }

    public static class RoomCreate extends NetworkMessage {
        String roomName;
        boolean isPublic;

        public RoomCreate(String roomName, boolean isPublic) {
            this.roomName = roomName;
            this.isPublic = isPublic;
        }

        public String getRoomName() {
            return roomName;
        }

        public boolean isPublic() {
            return isPublic;
        }
    }

    public static class RoomDelete extends NetworkMessage {
        String targetRoom;

        public RoomDelete(String targetRoom) {
            this.targetRoom = targetRoom;
        }
    }

    public static class RoomJoin extends NetworkMessage {
        String targetRoom;
        User user;
        Room room;
        boolean firstConnection;

        public RoomJoin(String targetRoom, User user, Room room, boolean firstConnection) {
            this.targetRoom = targetRoom;
            this.user = user;
            this.room = room;
            this.firstConnection = firstConnection;
        }

        public User getUser() {
            return user;
        }

        public String getTargetRoom() {
            return targetRoom;
        }

        public Room getRoom() {
            return room;
        }
    }

    public static class RoomLeave extends NetworkMessage {
        String targetRoom;
        String userId;

        public RoomLeave(String targetRoom, String userId) {
            this.targetRoom = targetRoom;
            this.userId = userId;
        }
    }

    public static class UserNameChange extends NetworkMessage {
        private static final long serialVersionUID = 2206362119107373026L;
        String newName;
        String userId;

        public UserNameChange(String newName, String userId) {
            this.newName = newName;
            this.userId = userId;
        }

        public String getNewName() {
            return this.newName;
        }

        public String getUserId() {
            return this.userId;
        }
    }

    public static class UserActiveRoom extends NetworkMessage {
        String activeRoom;

        public UserActiveRoom(String activeRoom) {
            this.activeRoom = activeRoom;
        }

        public String getActiveRoom() {
            return activeRoom;
        }
    }

}//END OF CLASS

