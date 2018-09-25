package hello.netty;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChannelRigter {

	private static Logger logger = LoggerFactory.getLogger("SERVER-CHANNLE-GROUP");

	private static ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup("SERVER-CHANNLE-GROUP", GlobalEventExecutor.INSTANCE);

	/**
	 * 添加channel
	 * 
	 * @param channel
	 */
	public static void addChannel(Channel channel) {
		CHANNEL_GROUP.add(channel);
		if (logger.isInfoEnabled()) {
			logger.info("channel " + channel + "add to server");
		}
	}

	/**
	 * 所有注册是channel
	 * 
	 * @return
	 */
	public static Set<Channel> getAllChannels() {
		return CHANNEL_GROUP;
	}

}
