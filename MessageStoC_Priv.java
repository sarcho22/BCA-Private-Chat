public class MessageStoC_Priv extends Message {
    public String userName;
    public String msg;
    public String sender;

    public MessageStoC_Priv(String userName, String msg, String sender) {
        this.userName = userName;
        this.msg = msg;
        this.sender = sender;
    }

    public String toString() {
        return "Private Message from " + sender + ": " + msg;
    }
}