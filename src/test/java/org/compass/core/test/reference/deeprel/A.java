package org.compass.core.test.reference.deeprel;

import java.util.HashSet;
import java.util.Set;

/**
 * @author kimchy
 */
public class A {

    int id;

    String value;

    Set<B> bs = new HashSet<B>();
}
