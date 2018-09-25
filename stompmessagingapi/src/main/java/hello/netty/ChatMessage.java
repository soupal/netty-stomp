package hello.netty;

import java.util.Date;
/**
 * chat 传输实体类对象
 * @project stompmessagingapi
 * @author guanglai.zhou
 * @date 2018年9月7日
 */
@SuppressWarnings("serial")
public class ChatMessage extends ChatBaseMessage {

	private Long userId;
	private String userName;
	private String message;
	private Date sendTime;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	@Override
	public String toString() {
		return "ChatMessage [userId=" + userId + ", userName=" + userName + ", message=" + message + ", sendTime=" + sendTime + "]";
	}

}
