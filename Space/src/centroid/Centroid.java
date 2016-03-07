package centroid;

import criterion.interfaces.ICriterion;
import extractor.interfaces.IArrayExtractor;
import extractor.value.FromCentroid;

import tree.binary.BinaryTree;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 15.02.2016.
 */
public class Centroid
{
    public double data[] = null;

    public Centroid(double data[])
    {
        this.data = data;
    }


    @SuppressWarnings("unchecked")
    public static Centroid getCentroid(Object arrayOfObjects, IArrayExtractor extractor, ArrayList<ICriterion> criteria)
    {
        ArrayList<Object> objects = (ArrayList<Object>) arrayOfObjects;

        double e[] = new double[criteria.size()];
        for (Object o : objects)
        {
            double d[] = extractor.getValue(o);
            for (int i = 0; i < criteria.size(); i++)
                e[i] += d[i];
        }

        for (int i = 0; i < criteria.size(); i++)
            e[i] /= (double) objects.size();

        return new Centroid(e);
    }


    public static ArrayList<Centroid> getCentroidBoundsWithoutInsert(Centroid reference,
                                                        ArrayList<BinaryTree<Centroid>> centroids,
                                                        ArrayList<ICriterion> criteria,
                                                        boolean lowerBounds)
    {
        ArrayList<Centroid> result = new ArrayList<>(criteria.size());

        for (int i = 0; i < criteria.size(); i++)
        {
            BinaryTree<Centroid> t = centroids.get(i);
            t.setSearch(reference);
            if (lowerBounds) result.add(t.previous());
            else result.add(t.next());
        }
        return result;
    }

    public static ArrayList<Centroid> getCentroidBounds(Centroid reference,
                                                        ArrayList<BinaryTree<Centroid>> centroids,
                                                        ArrayList<ICriterion> criteria,
                                                        boolean lowerBounds)
    {
        ArrayList<Centroid> result = new ArrayList<>(criteria.size());

        for (int i = 0; i < criteria.size(); i++)
        {
            BinaryTree<Centroid> t = centroids.get(i);
            t.insert(reference);

            t.setSearch(reference);
            if (lowerBounds) result.add(t.previous());
            else result.add(t.next());
            t.remove(reference);
        }
        return result;
    }


    public static Centroid getCentroid(double data[][], ArrayList<ICriterion> criteria)
    {
        double e[] = new double[criteria.size()];
        for (double[] d : data)
            for (int i = 0; i < criteria.size(); i++)
                e[i] += d[i];

        for (int i = 0; i < criteria.size(); i++)
            e[i] /= (double) data.length;

        return new Centroid(e);
    }

    public static ArrayList<BinaryTree<Centroid>> getSortedTrees(ArrayList<Centroid> centroids,
                                                                    ArrayList<ICriterion> criteria)
    {
        ArrayList<BinaryTree<Centroid>> tree = new ArrayList<>(criteria.size());
        for (int c = 0; c < criteria.size(); c++)
        {
            BinaryTree<Centroid> tmp = new BinaryTree<>(new FromCentroid(c));
            if (!criteria.get(c).isGain()) tmp.setDirection(false);
            tree.add(tmp);
        }

        // Prepare Trees
        for (int c = 0; c < criteria.size(); c++)
        {
            tree.get(c).clear();
            for (Centroid centroid : centroids)
                tree.get(c).insert(centroid);
        }

        return tree;
    }

    public static ArrayList<ArrayList<Centroid>> getSortedCentroids(ArrayList<Centroid> centroids,
                                                             ArrayList<ICriterion> criteria)
    {
        ArrayList<BinaryTree<Centroid>> tree = getSortedTrees(centroids, criteria);

        ArrayList<ArrayList<Centroid>> result = new ArrayList<>(criteria.size());
        for (int c = 0; c < criteria.size(); c++)
        {
            result.add(new ArrayList<>(centroids.size()));
            BinaryTree<Centroid> t = tree.get(c);
            Centroid centroid = t.search();
            result.get(c).add(centroid);

            while ((centroid = t.next()) != null)
                result.get(c).add(centroid);
        }

        return result;
    }



    /*public static ArrayList<Range> getCentroidBox(ArrayList<BinaryTree<Centroid>> tree,
                                                  Centroid centroid,
                                                  ArrayList<ICriterion> criteria)
    {
        ArrayList<Range> result = new ArrayList<>(criteria.size());

        for (int c = 0; c < criteria.size(); c++)
        {
            tree.get(c).insert(centroid);
            tree.get(c).setSearch(centroid);
            Centroid p = tree.get(c).previous();
            tree.get(c).setSearch(centroid);
            Centroid n = tree.get(c).next();

            double left = Common.MIN_DOUBLE;
            if (p != null) left = p.data[c];

            double right = Common.MAX_DOUBLE;
            if (n != null) right = n.data[c];

            Range r = null;
            if (left < right) r = new Range(left, right);
            else r = new Range(right, left);

            result.add(r);
            // REMOVE
            //tree.get(c).
        }

        return result;
    }*/
}
