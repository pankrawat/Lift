package com.liftindia.app.bean;

public class Chat {

    private String message;
    private String sender;
    private long time;

	// Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Chat() { }

    public Chat(String message, String sender, long time) {
        this.message = message;
        this.sender = sender;
        this.time = time;

    }

	public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

	public long getTime() {
		return time;
	}
}
