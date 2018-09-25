package hello.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

	public static void main(String[] args) {

		try {
			new NettyServer().bind(ChatSeverHostPortHolder.DEFUALT_SERVER_PORT);
		} catch (Exception e) {
			logger.error("NettyServer error", e);
		}
	}

	// 线程管理
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
	private EventLoopGroup workerGroup = new NioEventLoopGroup();
	// 定义服务端
	private ServerBootstrap bootStrap = new ServerBootstrap();

	public void bind(int port) {
		
		bootStrap.group(bossGroup, workerGroup)
		        .channel(NioServerSocketChannel.class)
		        // 日志
//		        .handler(new LoggingHandler())
		        // 加入所有的handler
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new ChatBaseMessageCodec());
						pipeline.addLast(new SeverHanlder());
					}
				});

		try {
			bootStrap.bind(port).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
//			bossGroup.shutdownGracefully();
//			workerGroup.shutdownGracefully();
		}

	}

}
