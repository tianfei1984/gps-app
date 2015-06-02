package cn.com.gps169.jt808.protocol.impl;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import cn.com.gps169.jt808.protocol.MessageBody;
import cn.com.gps169.jt808.tool.Tools;


/**
 * 终端注册
 * @author tianfei
 *
 */ 
public class JT0100 extends MessageBody {
	
	private int provinceId;			//省ID
	private int cityId;				//市ID
	private String manufacturer;		//制造厂商ID
	private String tmnlModel;			//终端型号
	private String tmnlId;				//终端ID
	private int color;				//车辆颜色
	private String licensePlate;	//车牌

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] encodeBody() {
		return null;
	}

	@Override
	public void decodeBody(ByteBuffer buff) {
		this.provinceId = Tools.getUnsignedShort(buff);
		this.cityId = Tools.getUnsignedShort(buff);
		//制作商ID
		byte[] b = new byte[5];
		buff.get(b);
		manufacturer = new String(b);
		//终端型号
		b = new byte[8];
		buff.get(b);
		tmnlModel = new String(b);
		//终端ID
		b = new byte[7];
		buff.get(b);
		tmnlId = new String(b);
		//车辆颜色
		color = buff.get();
		//车牌号
		b = new byte[buff.remaining()];
		buff.get(b);
		licensePlate = new String(b,Charset.forName("GBK"));
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getTmnlModel() {
		return tmnlModel;
	}

	public void setTmnlModel(String tmnlModel) {
		this.tmnlModel = tmnlModel;
	}

	public String getTmnlId() {
		return tmnlId;
	}

	public void setTmnlId(String tmnlId) {
		this.tmnlId = tmnlId;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public String getLicensePlate() {
		return licensePlate.trim();
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	@Override
	public String toString() {
		return String.format("省【%d】 市【%d】 终端制造商【%s】 终端型号【%s】 终端ID【%s】 车辆颜色【%d】 车牌号【%s】",
				provinceId,cityId,manufacturer,tmnlModel,tmnlId,color,licensePlate);
	}
}
