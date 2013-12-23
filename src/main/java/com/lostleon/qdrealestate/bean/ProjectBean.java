package com.lostleon.qdrealestate.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectBean {
	
	/** 以下为不可变成员 **/
	
	/* 项目ID */
	private int projectId;
	
	/* 项目名称 */
	private String name;
	
	/* 项目地址 */
	private String addr;
	
	/* 所在区县 */
	private District district;
	
	/* 企业名称 */
	private String company;
	
	/* 经纬度 */
	private double lat;
	private double lng;
	
	/* 销售信息 */
	private int residentNumTotal; //住宅总套数
	private float residentSizeTotal; //住宅总面积
	
	/** 以下为可变成员 **/
	
	/* 项目状态 */
	private ProjectStatus status;
	
	/* 销售信息 */
	private int residentNumAvail; //住宅可售套数
	private float residentSizeAvail; //住宅可售面积
	
	/* 最新更新时间 */
	private Date lastUpdate;
	
	/* 包含的“开盘单元” */
	private List<UnitBean> units = new ArrayList<UnitBean>();
	
	/* 每天信息 */
	private List<DailyMetrics> metrics = new ArrayList<DailyMetrics>();
	
	public ProjectBean(int id) {
		this.projectId = id;
	}
	
	public ProjectBean(int id, ProjectStatus st) {
		this.projectId = id;
		this.status = st;
	}

	public ProjectBean(int id, ProjectStatus st, String name,
			District district, String addr, String company, double lng,
			double lat, int residentNumTotal, int residentNumAvail,
			float residentSizeTotal, float residentSizeAvail) {
		this.projectId = id;
		this.status = st;
		this.name = name;
		this.district = district;
		this.addr = addr;
		this.company = company;
		this.lng = lng;
		this.lat = lat;
		this.residentNumTotal = residentNumTotal;
		this.residentNumAvail = residentNumAvail;
		this.residentSizeTotal = residentSizeTotal;
		this.residentSizeAvail = residentSizeAvail;
	}
	
	public ProjectStatus getProjectStatus() {
		return this.status;
	}
	
	public int getProjectId() {
		return this.projectId;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getAddr() {
		return this.addr;
	}
	
	public District getDistrict() {
		return this.district;
	}
	
	public String getCompany() {
		return this.company;
	}
	
	public double getLat() {
		return this.lat;
	}
	
	public double getLng() {
		return this.lng;
	}
	
	public int getResidentNumTotal() {
		return this.residentNumTotal;
	}
	
	public int getResidentNumAvail() {
		return this.residentNumAvail;
	}
	
	public float getResidentSizeTotal() {
		return this.residentSizeTotal;
	}
	
	public float getResidentSizeAvail() {
		return this.residentSizeAvail;
	}
	
	public void setLatLng(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public void addUnit(UnitBean unit) {
		this.units.add(unit);
	}
	
	public void updateRemainAvailable(int residentNumAvail, float residentSizeAvail) {
		this.residentNumAvail = residentNumAvail;
		this.residentSizeAvail = residentSizeAvail;
	}
	
	public void addMetrics(DailyMetrics d) {
		this.metrics.add(d);
	}
	
}
