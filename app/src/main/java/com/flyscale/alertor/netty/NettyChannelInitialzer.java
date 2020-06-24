package com.flyscale.alertor.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 高鹤泉
 * @TIME 2020/6/24 11:09
 * @DESCRIPTION 暂无
 */
public class NettyChannelInitialzer extends ChannelInitializer<SocketChannel> {

    ChannelPipeline mChannelPipeline;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        mChannelPipeline = ch.pipeline();
    }
}
