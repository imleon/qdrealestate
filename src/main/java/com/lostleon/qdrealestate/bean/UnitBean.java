package com.lostleon.qdrealestate.bean;

/**
 * “开盘单元”，也即预售许可证的许可Unit，用于获取精确的总面积和已售面积
 * 但是每个Unit的性质是住宅还是非住宅，则是未知的
 * 
 * @author lostleon@gmail.com
 * 
 */
public class UnitBean {

    /* 编号 */
    private long id;

    /* 总套数 */
    private int totalNum;

    /* 可售套数 */
    private int availNum;

    /* 总面积 */
    private float totalSize;

    /* 可售面积 */
    private float availSize;

    /* 是否是住宅 */
    private boolean isResident;

}
