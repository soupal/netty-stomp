package hello.netty;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;

public class NettyClient {

	private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

	private Bootstrap bootStrap;
	
	private EventLoopGroup eventLoopGroup;

	public void connect(String host, int port) {
		connect(host, port, null);
	}

	public void connect(String host, int port, final SimpMessagingTemplate simpMessagingTemplate) {

		eventLoopGroup = new NioEventLoopGroup();

		bootStrap = new Bootstrap();

		bootStrap.group(eventLoopGroup).channel(NioSocketChannel.class);

		// 设置一个重连的handler
		final ConnectionWatchdog connectionWatchdog = new ConnectionWatchdog(bootStrap, host, port) {
			@Override
			public ChannelHandler[] handlers() {
				return new ChannelHandler[] {
						// 定义你写心跳的时间 和读服务器数据的时间
						new IdleStateHandler(2, 1, 0, TimeUnit.SECONDS), 
						new ChatBaseMessageCodec(), 
						this, 
						new ClientHandler(simpMessagingTemplate)

				};
			}
		};

		ChannelFuture future;

		try {

			synchronized (bootStrap) {

				bootStrap.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(connectionWatchdog.handlers());
					}
				});

				future = bootStrap.connect(host, port);

			}
			future.sync();

		} catch (Exception e) {
			throw new RuntimeException("Connects to [" + host + ":" + port + "] fails", e);
		}

	}
	
	public Future<?> shutdown() {
		return this.eventLoopGroup.shutdownGracefully();
	}

	public static void main(String[] args) {
		try {
			new NettyClient().connect(ChatSeverHostPortHolder.DEFAULT_SERVER_HOST, ChatSeverHostPortHolder.DEFUALT_SERVER_PORT);
		} catch (Exception e) {
			logger.error("NettyClient ERROR", e);
		}

	}

}
