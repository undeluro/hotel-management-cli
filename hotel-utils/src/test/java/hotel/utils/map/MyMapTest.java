package hotel.utils.map;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyMapTest {

    @Test
    void testPutNewKeyReturnsTrueAndGetReturnsValue() {
        Map<String, Integer> map = new MyMap<>();
        assertTrue(map.put("a", 1));
        assertEquals(1, map.get("a"));
    }

    @Test
    void testPutExistingKeyReplacesValue() {
        Map<String, Integer> map = new MyMap<>();
        assertTrue(map.put("a", 1));
        assertTrue(map.put("a", 2));
        assertEquals(2, map.get("a"));
        assertEquals(1, map.keys().size());
    }

    @Test
    void testGetNonExistentKeyReturnsNull() {
        Map<String, Integer> map = new MyMap<>();
        assertNull(map.get("missing"));
    }

    @Test
    void testKeysReturnsAllKeysInInsertionOrder() {
        Map<String, Integer> map = new MyMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        List<String> keys = map.keys();
        assertEquals(List.of("a", "b", "c"), keys);
        // ensure unmodifiable copy
        assertThrows(UnsupportedOperationException.class, () -> keys.add("x"));
    }

    @Test
    void testRemoveExistingKeyReturnsTrueAndKeyRemoved() {
        Map<String, Integer> map = new MyMap<>();
        map.put("a", 1);
        assertTrue(map.remove("a"));
        assertFalse(map.contains("a"));
        assertNull(map.get("a"));
    }

    @Test
    void testRemoveNonExistentKeyReturnsFalse() {
        Map<String, Integer> map = new MyMap<>();
        assertFalse(map.remove("nope"));
    }

    @Test
    void testContainsForExistingAndNonExisting() {
        Map<String, Integer> map = new MyMap<>();
        map.put("a", 1);
        assertTrue(map.contains("a"));
        assertFalse(map.contains("b"));
        assertFalse(map.contains(null));
    }

    @Test
    void testPutNullKeyOrValueHandledAppropriately() {
        Map<String, Integer> map = new MyMap<>();
        assertFalse(map.put(null, 1));
        assertFalse(map.put("a", null));
        assertNull(map.get(null));
        assertFalse(map.remove(null));
    }
}
