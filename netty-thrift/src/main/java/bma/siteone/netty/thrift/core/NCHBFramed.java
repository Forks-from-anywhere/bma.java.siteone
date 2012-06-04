package bma.siteone.netty.thrift.core;

import org.jboss.netty.channel.ChannelHandler;

import bma.common.langutil.pipeline.CommonPipeline;
import bma.common.langutil.pipeline.CommonPipelineBuilder;

public class NCHBFramed implements CommonPipelineBuilder<ChannelHandler> {

	private String name = "framed";
	private int maxLength;

	public NCHBFramed() {
		super();
	}

	public NCHBFramed(String name, int maxLength) {
		super();
		this.name = name;
		this.maxLength = maxLength;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public void buildPipeline(CommonPipeline<ChannelHandler> pipeline) {
		pipeline.addLast(name, new NCHFramed(maxLength));
	}

}
