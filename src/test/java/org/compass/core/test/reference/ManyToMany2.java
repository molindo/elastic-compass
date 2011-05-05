package org.compass.core.test.reference;

import java.util.List;
import java.util.ArrayList;

/**
 * @author kimchy
 */
public class ManyToMany2 {

    Long id;

    String value;

    List<ManyToMany1> many1 = new ArrayList<ManyToMany1>();
}
