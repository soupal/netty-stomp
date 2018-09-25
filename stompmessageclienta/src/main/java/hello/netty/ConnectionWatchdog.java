package hello.netty;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@Sharable
public abstract class ConnectionWatchdog extends ChannelDuplexHandler implements ChannelHandlerHolder, Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionWatchdog.class);

	private Bootstrap bootstrap;

	private String host;

	// ScheduledExecutorService scheduledExecutorService =
	// Executors.newScheduledThreadPool(1);

	private int port;

	public ConnectionWatchdog(Bootstrap bootstrap, String host, int port) {
		this.bootstrap = bootstrap;
		this.host = host;
		this.port = port;
	}

	/**
	 * 设置重连时间
	 */
	private int reconnectTime = 3;

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// 直接使用netty的线程池进行任务的管理
		ctx.executor().schedule(this, reconnectTime, TimeUnit.SECONDS);
		// scheduledExecutorService.schedule(this, reconnectTime, TimeUnit.SECONDS);
		super.channelInactive(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (IdleStateEvent.class.isInstance(evt)) {
			IdleStateEvent event = (IdleStateEvent) evt;
			// 重连机制 关闭连接
			if (IdleState.READER_IDLE.equals(event.state())) {
				ctx.close();
			}
			// 心跳机制
			if (IdleState.WRITER_IDLE.equals(event.state())) {
				ctx.writeAndFlush(new HeartBeatMessage());
			}
		}
	}

	@Override
	public void run() {
		ChannelFuture future;
		synchronized (bootstrap) {
			bootstrap.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(handlers());
				}
			});
			future = bootstrap.connect(host, port);
		}

		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				boolean succeed = f.isSuccess();

				if (logger.isWarnEnabled()) {
					logger.warn("Reconnects with {}, {}.", host + ":" + port, succeed ? "succeed" : "failed");
				}

				if (!succeed) {
					f.channel().pipeline().fireChannelInactive();
				}
			}
		});
	}
}