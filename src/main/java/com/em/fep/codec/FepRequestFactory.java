package com.em.fep.codec;

public class FepRequestFactory {

    public static FepRequest getFepRequest(String kindCode) {

        if ("0600".equals(kindCode)) {
            return new FepMessage600610();
        } else if ("0630".equals(kindCode)) {
            return new FepMessage630640();
        } else if ("0320".equals(kindCode)) {
            return new FepMessage320310();
        } else if ("0620".equals(kindCode)) {
            return new FepMessage620();
        }

        return null;
    }
}
