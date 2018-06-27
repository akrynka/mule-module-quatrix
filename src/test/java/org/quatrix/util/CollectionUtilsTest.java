package org.quatrix.util;

import com.google.common.base.Function;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionUtilsTest {

    private static final Function<Object, Object> STUB_MAP_FUNC = new Function<Object, Object>() {
        @Nullable
        @Override
        public Object apply(@Nullable Object o) {
            return o;
        }
    };

    @Test
    public void testMapToEmptyListIfValuesAreNull() {
        List<Object> values = CollectionUtils.map(null, STUB_MAP_FUNC);

        Assert.assertNotNull(values);
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void testMapToEmptyListIfValuesAreEmpty() {
        List<Object> values = CollectionUtils.map(new ArrayList<>(), STUB_MAP_FUNC);

        Assert.assertNotNull(values);
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void testMapToCorrectResultList() {
        final List<Integer> source = Arrays.asList(1, 2, 3);
        List<String> result = CollectionUtils.map(source, new Function<Integer, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Integer integer) {
                return Integer.toString(integer);
            }
        });

        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
    }
}