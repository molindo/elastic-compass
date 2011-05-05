package org.compass.core.test.reference;

import java.util.List;
import java.util.ArrayList;

/**
 * @author kimchy
 */
public class ManyToMany1 {

    Long id;

    String value;

    List<ManyToMany2> many2 = new ArrayList<ManyToMany2>();
}
