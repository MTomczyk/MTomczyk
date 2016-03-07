package year.y2014.greenlogistics.lp.test;

import org.junit.Test;
import standard.Range;
import year.y2014.greenlogistics.lp.olds.Subregion;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by MTomczyk on 16.11.2015.
 */
public class SubregionTest
{

    @Test
    public void testAdd1() throws Exception
    {
        Subregion Q = new Subregion(2);

        ArrayList<Range> r = new ArrayList<>(3);
        r.add(new Range(0.0d, 5.0d));
        r.add(new Range(3.0d, 8.0d));
        Q.add(r);

        r = new ArrayList<>(3);
        r.add(new Range(15.0d, 20.0d));
        r.add(new Range(20.0d, 21.0d));
        Q.add(r);

        r = new ArrayList<>(3);
        r.add(new Range(3.0d, 17.0d));
        Q.add(r);

        r = new ArrayList<>(3);
        r.add(null);
        r.add(new Range(10.0d, 12.0d));
        Q.add(r);

        r = new ArrayList<>(3);
        r.add(null);
        r.add(new Range(-10.0d, 30.0d));
        Q.add(r);


        assertEquals("T1", 0, (int)Q.e.get(0).get(0).left);
        assertEquals("T1", 20, (int)Q.e.get(0).get(0).right);
        assertEquals("T1", -10, (int)Q.e.get(1).get(0).left);
        assertEquals("T1", 30, (int)Q.e.get(1).get(0).right);
    }

    @Test
    public void testAdd2() throws Exception

    {
        Subregion Q = new Subregion(3);

        ArrayList<Range> r = new ArrayList<>(3);
        r.add(new Range(-8.0d, 2.0d));
        r.add(new Range(5.0d, 7.0d));
        r.add(new Range(20.0d, 30.0d));
        Q.add(r);

        r = new ArrayList<>(3);
        r.add(null);
        r.add(new Range(6.0d, 10.0d));
        r.add(null);
        Q.add(r);

        r = new ArrayList<>(3);
        r.add(new Range(-4.0d, 5.0d));
        r.add(new Range(6.0d, 9.0d));
        r.add(new Range(25.0d, 26.0d));
        assertEquals("T2", false, Q.contains(r));

        r = new ArrayList<>(3);
        r.add(new Range(-4.0d, -1.0d));
        r.add(new Range(6.0d, 9.0d));
        r.add(new Range(25.0d, 26.0d));
        assertEquals("T2", true, Q.contains(r));

        r = new ArrayList<>(3);
        r.add(new Range(-4.0d, -1.0d));
        r.add(null);
        r.add(new Range(25.0d, 26.0d));
        assertEquals("T2", true, Q.contains(r));


        r = new ArrayList<>(3);
        r.add(new Range(-4.0d, -1.0d));
        r.add(new Range(6.0d, 12.0d));
        r.add(new Range(25.0d, 26.0d));
        assertEquals("T2", false, Q.contains(r));

        r = new ArrayList<>(3);
        r.add(new Range(-4.0d, -1.0d));
        r.add(new Range(6.0d, 9.0d));
        r.add(new Range(5.0d, 26.0d));
        assertEquals("T2", false, Q.contains(r));

        r = new ArrayList<>(3);
        r.add(null);
        r.add(new Range(6.0d, 9.0d));
        r.add(new Range(5.0d, 26.0d));
        assertEquals("T2", false, Q.contains(r));
    }

}