package cn.com.gps169.server.protocol.impl;

import java.nio.ByteBuffer;

import cn.com.gps169.server.protocol.Jt808MessageBody;


public class JT0102 extends Jt808MessageBody {
	
	private String authorCode; 		//终端鉴权码

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public byte[] encodeBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void decodeBody(ByteBuffer buff) {
		// TODO Auto-generated method stub
		authorCode = new String(buff.array());
	}

	public String getAuthorCode() {
		return authorCode;
	}

	public void setAuthorCode(String authorCode) {
		this.authorCode = authorCode;
	}

	@Override
	public String toString() {
		return "终端鉴权码："+authorCode;
	}
}
