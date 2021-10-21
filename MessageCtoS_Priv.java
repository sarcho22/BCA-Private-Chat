public class MessageCtoS_Priv extends Message {
    public String msg;
    public String userName;

    public MessageCtoS_Priv(String userName, String msg, String sender) {
        this.userName = userName;
        this.msg = msg;
        this.sender = sender;
    }

}