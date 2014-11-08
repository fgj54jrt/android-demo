package com.cwits.cyx_drive_sdk.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 查询行程列表时从AR端返回的奖励信息
 * @author lxh
 *
 */
public class ARAwardInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String rid;				//行程ID
	private int sc;				//得分
	private int gt;					//领取标志，0未领，1已领
	private List<AwardData> pz;	//奖励数据
	
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public int getSc() {
		return sc;
	}
	public void setSc(int score) {
		this.sc = score;
	}
	public int getGt() {
		return gt;
	}
	public void setGt(int gt) {
		this.gt = gt;
	}
	public List<AwardData> getPzArray() {
		return pz;
	}
	public void setAward(List<AwardData> award) {
		this.pz = award;
	}
	/**
	 * 奖励数据
	 * @author lxh
	 *
	 */
	public class AwardData {

	    private int tp ;				//奖励类型 1金币，2物品
	    private int id;					//奖励物品ID，对于物品有ID，金币暂时ID就为1
	    private int n;					//奖励数量，金币数或物品数。
		public int getTp() {
			return tp;
		}
		public void setTp(int tp) {
			this.tp = tp;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public int getN() {
			return n;
		}
		public void setN(int n) {
			this.n = n;
		}
		   
	}
}
