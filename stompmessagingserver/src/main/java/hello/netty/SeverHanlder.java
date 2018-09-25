package hello.netty;

import java.util.Set;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SeverHanlder extends SimpleChannelInboundHandler<ChatBaseMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ChatBaseMessage msg) throws Exception {
		if (HeartBeatMessage.class.isInstance(msg)) {
			ctx.writeAndFlush(msg);
		} else {
			System.out.println(msg);
			Set<Channel> allChannels = ChannelRigter.getAllChannels();
			for (Channel channel : allChannels) {
				if (!channel.equals(ctx.channel())) {
					channel.writeAndFlush(msg);
					System.out.println("message from channel = " + ctx.channel() + " , send to channel " + channel);
				}
			}
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// ChatMessage message = null;
		// for (int i = 0; i < 10; i++) {
		// message = new ChatMessage();
		// message.setMessage("又是新的一天,加油！");
		// message.setSendTime(new Date());
		// message.setUserId(12L);
		// message.setUserName("无可奉告");
		// ctx.writeAndFlush(message);
		// }
		ChannelRigter.addChannel(ctx.channel());
		super.channelActive(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
