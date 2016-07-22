package com.newunion.newunionapp.utils;

import java.util.Collection;

/**
 * <p> Collection Utilities
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class CollectionUtils {
    public static <E> boolean isEmpty(Collection<E> list) {
        return list == null || list.isEmpty();
    }
}
