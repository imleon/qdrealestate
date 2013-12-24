package com.lostleon.qdrealestate.bean;

import java.util.List;

/**
 * “开盘单元”，也即预售许可证的许可Unit，用于获取精确的总面积和已售面积
 * 但是每个Unit的性质是住宅还是非住宅，则是未知的
 * 
 * @author lostleon@gmail.com
 * 
 */
public class UnitBean {

    /** 以下为不可变 **/

    /* 编号 */
    private long id;

    /* 总套数 */
    private int totalNum;

    /* 总面积 */
    private float totalSize;

    /* 是否是住宅，默认为false */
    private boolean isResident;
    
    /* 是否是模拟的，默认为false */
    private boolean isFake;

    /** 以下为可变 **/

    /* 可售套数 */
    private int availNum;

    /* 可售面积 */
    private float availSize;
    
    public UnitBean(long id, int totalNum, int availNum, float totalSize,
            float availSize, boolean isResident, boolean isFake) {
        this.id = id;
        this.totalNum = totalNum;
        this.availNum = availNum;
        this.totalSize = totalSize;
        this.availSize = availSize;
        this.isResident = isResident;
        this.isFake = isFake;
    }
    
    public void setResident(boolean isResident) {
        this.isResident = isResident;
    }
    
    public int getTotalNum() {
        return this.totalNum;
    }
    
    public int getAvailNum() {
        return this.availNum;
    }
    
    public float getTotalSize() {
        return this.totalSize;
    }
    
    public float getAvailSize() {
        return this.availSize;
    }
    
    public boolean getIsResident() {
        return this.isResident;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append("|").append(totalNum).append("|").append(availNum)
                .append("|").append(totalSize).append("|").append(availSize)
                .append("|").append(isResident).append("|").append(isFake);
        return sb.toString();
    }
    
    /**
     * 调整UnitBean中的isResident字段
     * 
     * @param prj
     * @param ubs
     */
    public static void adjustUnitBeans(final ProjectBean prj, List<UnitBean> ubs) {
        int size = ubs.size();
        // 从1遍历到2^n-1
        for (int num = 1; num < (2 << size) - 1; num++) {

            int adjNumTotal = 0;
            int adjNumAvail = 0;
            float adjSizeTotal = 0.0f;
            float adjSizeAvail = 0.0f;

            // 遍历每一位，按照0/1来决定true/false
            for (int i = 0; i < size; i++) {
                int flag = (num >> i) & 1;
                if (flag == 0) {
                    adjNumTotal += ubs.get(i).getTotalNum();
                    adjNumAvail += ubs.get(i).getAvailNum();
                    adjSizeTotal += ubs.get(i).getTotalSize();
                    adjSizeAvail += ubs.get(i).getAvailSize();
                }
                ubs.get(i).setResident(flag == 0);
            }

            // 如果找到的话，则结束
            if (prj.getResidentNumTotal() == adjNumTotal
                    && prj.getResidentNumAvail() == adjNumAvail
                    && Math.abs(prj.getResidentSizeTotal() - adjSizeTotal) < 0.5f
                    && Math.abs(prj.getResidentSizeAvail() - adjSizeAvail) < 0.5f) {
                prj.setResidentSize(adjSizeTotal, adjSizeAvail);
                return;
            }
        }
        // 如果一直没找到，说明UnitBean是住宅、非住宅混搭的，则Fake一个UnitBean出来
        for (int i = 0; i < size; i++) {
            ubs.get(i).setResident(false);
        }
        ubs.add(new UnitBean(-1l, prj.getResidentNumTotal(), prj
                .getResidentNumAvail(), prj.getResidentSizeTotal(), prj
                .getResidentSizeAvail(), true, true));
    }
    
}
