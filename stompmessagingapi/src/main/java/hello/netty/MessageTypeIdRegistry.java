package hello.netty;

import java.util.HashMap;
import java.util.Map;

public class MessageTypeIdRegistry {

    private static Map<Integer, Class<?>> map = new HashMap<Integer, Class<?>>(7);
//  这个bug其实很low 不知道怎么在用Map的时候编码成了 ConcurrentReferenceHashMap类型了这是一个同步的软(虚)引用Map 在初始化时会添加注册信息到map中,
//  但是在系统GC的时候如果内存充足是不会进行回收的,但是在内存不足的情况下,就会回收这个软引用Map中的信息 导致了注册信息的不存在  
//	private static Map<Integer, Class<?>> map = new ConcurrentReferenceHashMap<Integer, Class<?>>(11);
	private static Map<String, Integer> map2 = new HashMap<String, Integer>(7);

	static {
		map.put(MessageTypeId.CHAT_MESSAGE_ID, ChatMessage.class);
		map.put(MessageTypeId.HEARTBEAT_MESSAGE_ID, HeartBeatMessage.class);
		map2.put(ChatMessage.class.getName(), MessageTypeId.CHAT_MESSAGE_ID);
		map2.put(HeartBeatMessage.class.getName(), MessageTypeId.HEARTBEAT_MESSAGE_ID);
	}

	public static Class<?> getType(int messageId) {

		if (map.get(messageId) == null) {
			throw new RuntimeException("can not get the type of the message ,not register ? " + messageId);
		}

		return map.get(messageId);
	}

	public static Integer getMessageId(String messageType) {

		if (map2.get(messageType) == null) {
			throw new RuntimeException("can not get the typeid of the message ,not register ? " + messageType);
		}

		return map2.get(messageType);
	}

}
