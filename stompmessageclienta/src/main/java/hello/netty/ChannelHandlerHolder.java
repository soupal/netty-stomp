
package hello.netty;

import io.netty.channel.ChannelHandler;

public interface ChannelHandlerHolder {

	ChannelHandler[] handlers();
}