package com.lostleon.qdrealestate.bean;

public enum District {
    QU_SHINAN,    // 市南区
    QU_SHIBEI,    // 市北区
    QU_SIFANG,    // 四方区
    QU_LICANG,    // 李沧区
    QU_LAOSHAN,   // 崂山区
    QU_HUANGDAO,  // 黄岛区
    QU_CHENGYANG, // 城阳区
    QU_GAOXIN,    // 高新区
    SHI_JIMO,     // 即墨市
    SHI_PINGDU,   // 平度市
    SHI_JIAOZHOU, // 胶州市
    SHI_JIAONAN,  // 胶南市
    SHI_LAIXI;    // 莱西市

    public static District getDistrictByCHN(String str) {
        if (str.equals("市南区")) {
            return QU_SHINAN;
        } else if (str.equals("市北区")) {
            return QU_SHIBEI;
        } else if (str.equals("四方区")) {
            return QU_SIFANG;
        } else if (str.equals("李沧区")) {
            return QU_LICANG;
        } else if (str.equals("崂山区")) {
            return QU_LAOSHAN;
        } else if (str.equals("黄岛区")) {
            return QU_HUANGDAO;
        } else if (str.equals("城阳区")) {
            return QU_CHENGYANG;
        } else if (str.equals("高新区")) {
            return QU_GAOXIN;
        } else if (str.equals("即墨市")) {
            return SHI_JIMO;
        } else if (str.equals("平度市")) {
            return SHI_PINGDU;
        } else if (str.equals("胶州市")) {
            return SHI_JIAOZHOU;
        } else if (str.equals("胶南市")) {
            return SHI_JIAONAN;
        } else if (str.equals("莱西市")) {
            return SHI_LAIXI;
        }
        throw new IllegalArgumentException("No District for string: " + str);
    }

    public static District getDistrictByENG(String str) {
        if (str.equals("QU_SHINAN")) {
            return QU_SHINAN;
        } else if (str.equals("QU_SHIBEI")) {
            return QU_SHIBEI;
        } else if (str.equals("QU_SIFANG")) {
            return QU_SIFANG;
        } else if (str.equals("QU_LICANG")) {
            return QU_LICANG;
        } else if (str.equals("QU_LAOSHAN")) {
            return QU_LAOSHAN;
        } else if (str.equals("QU_HUANGDAO")) {
            return QU_HUANGDAO;
        } else if (str.equals("QU_CHENGYANG")) {
            return QU_CHENGYANG;
        } else if (str.equals("QU_GAOXIN")) {
            return QU_GAOXIN;
        } else if (str.equals("SHI_JIMO")) {
            return SHI_JIMO;
        } else if (str.equals("SHI_PINGDU")) {
            return SHI_PINGDU;
        } else if (str.equals("SHI_JIAOZHOU")) {
            return SHI_JIAOZHOU;
        } else if (str.equals("SHI_JIAONAN")) {
            return SHI_JIAONAN;
        } else if (str.equals("SHI_LAIXI")) {
            return SHI_LAIXI;
        }
        throw new IllegalArgumentException("No District for string: " + str);
    }

}
