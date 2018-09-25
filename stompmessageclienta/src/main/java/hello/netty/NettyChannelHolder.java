package hello.netty;

import io.netty.channel.Channel;

public class NettyChannelHolder {

	private static Channel registerChannel;

	/**
	 * 返回一个连接
	 * 
	 * @return
	 */
	public static Channel getChannel() {
		if (registerChannel != null) {
			return registerChannel;
		}
		return null;
	}

	/**
	 * 注册一个channel
	 * 
	 * @param channel
	 */
	public static void addChannel(Channel channel) {
		if (channel != null) {
			registerChannel = channel;
		} else {
			throw new RuntimeException("----");
		}
	}

}
