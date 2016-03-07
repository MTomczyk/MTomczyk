package year.y2014.greenlogistics;

import java.util.ArrayList;

import base.Gene;
import base.Specimen;
import interfaces.IGene;
import interfaces.ISpecimen;
import org.apache.commons.math3.random.MersenneTwister;
import reproducer.interfaces.IParents;
import reproducer.interfaces.IReproducer;

public class Reproducer implements IReproducer
{
    private int criteria = 3;

    @Override
    public ArrayList<ISpecimen> reproduce(ArrayList<IParents> parent, Object problem, int generation,
                                          MersenneTwister generator)
    {
        ArrayList<ISpecimen> reproduction = new ArrayList<>(parent.size());
        ISpecimen parents[] = new Specimen[2];

        for (int i = 0; i < parent.size(); i++)
        {
            parents[0] = parent.get(i).getParent(0);
            parents[1] = parent.get(i).getParent(1);
            ISpecimen child = this.cross(parents, problem, generator, 0);
            this.mutate(child, parents, generator, generation);
            child.setName(String.format("%d-%d", generation + 1, i / 2));
            reproduction.add(child);
        }

        return reproduction;
    }

    private void changeCriterion(double c[], double chance, MersenneTwister generator)
    {
        // 1. ZMIANA KRYTERIUM
        for (int i = 0; i < c.length; i++)
            if (generator.nextDouble() > chance) c[i] = generator.nextInt(criteria);
    }

    private void changeTransport(double t[], double chance, MersenneTwister generator)
    {
        // 1. ZMIANA KRYTERIUM
        for (int i = 0; i < t.length; i++)
            if (generator.nextDouble() > chance)
            {
                if (generator.nextBoolean()) t[i] = 5;
                else t[i] = generator.nextInt(5);
            }
    }

    private void changeTransportAll(double t[], double chance, int max, MersenneTwister generator)
    {
        int change = generator.nextInt(max);
        if (generator.nextDouble() > chance)
        {
            for (int i = 0; i < t.length; i++)
                t[i] = change;
        }
    }


    @SuppressWarnings({"SpellCheckingInspection", "UnusedParameters"})
    private void changeCritAll(double c[], double chance, int max, MersenneTwister generator)
    {
        int change = generator.nextInt(3);
        if (generator.nextDouble() > chance)
        {
            for (int i = 0; i < c.length; i++)
                c[i] = change;
        }
    }

    private void swap(double p[], double c[], double chance, MersenneTwister generator)
    {
        if (generator.nextDouble() < chance) return;

        int a = generator.nextInt(p.length);
        int b = generator.nextInt(p.length);

        if (a == b) return;
        if (b < a)
        {
            int tmp = a;
            a = b;
            b = tmp;
        }

        for (int i = a; i <= b; i++)
        {
            double tmp = p[i];
            p[i] = p[b - (i - a)];
            p[b - (i - a)] = tmp;

            tmp = c[i];
            c[i] = c[b - (i - a)];
            c[b - (i - a)] = tmp;
        }

    }

    private void shiftPriority(double p[], double c[], double chance, MersenneTwister generator)
    {
        // 2. ZMIANA PRIORYTETU (SHIFT)
        for (int i = 0; i < p.length; i++)
        {
            if (generator.nextDouble() > chance)
            {
                int place = generator.nextInt(p.length);
                if (place == i) continue;
                double val = p[i];
                double criterion = c[i];
                if (place > i)
                {
                    for (int j = i; j < place; j++)
                    {
                        p[j] = p[j + 1];
                        c[j] = c[j + 1];
                    }
                } else
                {
                    for (int j = i; j > place; j--)
                    {
                        p[j] = p[j - 1];
                        c[j] = c[j - 1];
                    }
                }
                p[place] = val;
                c[place] = criterion;
            }
        }
    }


    @SuppressWarnings("UnusedParameters")
    public void mutate(ISpecimen specimen, ISpecimen parent[], MersenneTwister generator, int generation)
    {
        double chance;

        double codeRM_P[] = specimen.getGene().getGene().get("RM_P").getValues();
        double codeRM_C[] = specimen.getGene().getGene().get("RM_C").getValues();
        double codeRM_T[] = specimen.getGene().getGene().get("RM_T").getValues();

        double codeDC_P[] = specimen.getGene().getGene().get("DC_P").getValues();
        double codeDC_C[] = specimen.getGene().getGene().get("DC_C").getValues();
        double codeDC_T[] = specimen.getGene().getGene().get("DC_T").getValues();

        // USUN COS Z DC + DODAJ COS DO DC
        ArrayList<Integer> pDC = new ArrayList<>(codeDC_P.length);
        ArrayList<Integer> cDC = new ArrayList<>(codeDC_C.length);
        ArrayList<Integer> tDC = new ArrayList<>(codeDC_T.length);

        for (int i = 0; i < codeDC_P.length; i++)
        {
            pDC.add((int) codeDC_P[i]);
            cDC.add((int) codeDC_C[i]);
            tDC.add((int) codeDC_T[i]);
        }

        // COS USUN
        boolean dDC[] = new boolean[codeDC_P.length];

        chance = 1.0d - (1.0d / (double) dDC.length);

        for (int i = 0; i < dDC.length; i++)
        {
            int place = generator.nextInt(dDC.length);
            if (generator.nextDouble() > chance) dDC[place] = true;
        }

        boolean res = true;
        for (boolean aDDC : dDC)
            if (!aDDC)
            {
                res = false;
                break;
            }

        if (res) for (int i = 0; i < dDC.length; i++)
            dDC[i] = false;

        // USUN
        int shift = 0;
        for (int i = 0; i < dDC.length; i++)
            if (dDC[i])
            {
                pDC.remove(i - shift);
                cDC.remove(i - shift);
                tDC.remove(i - shift);
                shift++;
            }

        boolean aDC[] = new boolean[16];
        for (int i = 0; i < 16; i++)
            aDC[i] = false;
        for (Integer aPDC : pDC) aDC[aPDC] = true;

        // DODAJ
        chance = 1.0d - (1.0d / (double) aDC.length);

        for (int i = 0; i < aDC.length; i++)
            if ((!aDC[i]) && (generator.nextDouble() > chance))
            {
                pDC.add(i);
                cDC.add(generator.nextInt(criteria));
                if (generator.nextBoolean()) tDC.add(5);
                else tDC.add(generator.nextInt(5));
            }

        codeDC_P = new double[pDC.size()];
        codeDC_C = new double[pDC.size()];
        codeDC_T = new double[pDC.size()];

        for (int i = 0; i < codeDC_P.length; i++)
        {
            codeDC_P[i] = pDC.get(i);
            codeDC_C[i] = cDC.get(i);
            codeDC_T[i] = tDC.get(i);
        }

        // ---- RM ----

        this.changeCriterion(codeRM_C, 1.0d - (1.0d / (double) codeRM_C.length), generator);
        this.changeTransport(codeRM_T, 1.0d - (1.0d / (double) codeRM_T.length), generator);
        this.shiftPriority(codeRM_P, codeRM_C, 1.0d - (1.0d / (double) codeRM_P.length), generator);
        this.swap(codeRM_P, codeRM_C, 1.0d - (1.0d / (double) codeRM_P.length), generator);

        if (generation > 100) this.changeTransportAll(codeRM_T, 1.0d - (1.0d / (double) codeRM_T.length), 5, generator);

        if (generation > 100) this.changeCritAll(codeRM_C, 1.0d - (1.0d / (double) codeRM_C.length), 5, generator);

        // ---- DC ----
        this.changeCriterion(codeDC_C, 1.0d - (1.0d / (double) codeDC_C.length), generator);
        this.changeTransport(codeDC_T, 1.0d - (1.0d / (double) codeDC_T.length), generator);
        this.shiftPriority(codeDC_P, codeDC_C, 1.0d - (1.0d / (double) codeDC_P.length), generator);
        this.swap(codeDC_P, codeDC_C, 1.0d - (1.0d / (double) codeDC_P.length), generator);

        if (generation > 100) this.changeTransportAll(codeDC_T, 1.0d - (1.0d / (double) codeDC_T.length), 6, generator);

        if (generation > 100) this.changeCritAll(codeDC_C, 1.0d - (1.0d / (double) codeDC_C.length), 5, generator);

        specimen.getGene().getGene().get("RM_C").setValues(codeRM_C);
        specimen.getGene().getGene().get("RM_P").setValues(codeRM_P);
        specimen.getGene().getGene().get("RM_T").setValues(codeRM_T);

        specimen.getGene().getGene().get("DC_C").setValues(codeDC_C);
        specimen.getGene().getGene().get("DC_P").setValues(codeDC_P);
        specimen.getGene().getGene().get("DC_T").setValues(codeDC_T);

        // MANIPULACJA EP
        chance = 0.9d;
        if (generator.nextDouble() > chance)
        {
            ArrayList<Integer> EP = new ArrayList<>(3);

            EP.add(3);
            EP.add(11);
            EP.add(15);
            this.shuffle(EP, EP.size() * 5, generator);

            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") ArrayList<Integer> nEP = new ArrayList<>(3);
            nEP.add(EP.get(0));

            if (generator.nextDouble() > 0.5d) nEP.add(EP.get(1));
            if (generator.nextDouble() > 0.5d) nEP.add(EP.get(2));

            double epCode_P[] = new double[EP.size()];
            double epCode_C[] = new double[EP.size()];

            for (int i = 0; i < EP.size(); i++)
            {
                epCode_P[i] = EP.get(i);
                epCode_C[i] = generator.nextInt(criteria);
            }

            specimen.getGene().getGene().get("EP_P").setValues(epCode_P);
            specimen.getGene().getGene().get("EP_C").setValues(epCode_C);
        }
    }

    public void shuffle(ArrayList<Integer> array, int times, MersenneTwister generator)
    {
        int size = array.size();
        for (int i = 0; i < times; i++)
        {
            int a = generator.nextInt(size);
            int b = generator.nextInt(size);

            Integer tmp = array.get(a);
            array.set(a, array.get(b));
            array.set(b, tmp);
        }
    }

    private void standardCross(double childP[], double childC[], double childT[], double fatherP[], double fatherC[],
                               double fatherT[], double motherP[], double motherC[], double motherT[])
    {
        boolean mat[] = new boolean[16];

        int half = childP.length / 2;
        int halfM = motherP.length / 2;

        int pointer = 0;

        // POLOWA DETERMINISTYCZNIE OD OJCA
        for (int i = 0; i < half; i++)
        {
            childP[i] = fatherP[i];
            childC[i] = fatherC[i];
            childT[i] = fatherT[i];
            mat[(int) childP[i]] = true;
            pointer++;
        }
        // POLOWA OD MATKI
        for (int i = 0; i < half; i++)
        {
            if (i + halfM >= motherP.length) break;
            if (mat[(int) motherP[halfM + i]])
            {
                continue;
            }
            childP[pointer] = motherP[halfM + i];
            childC[pointer] = motherC[halfM + i];
            childT[pointer] = motherT[halfM + i];
            mat[(int) childP[pointer]] = true;
            pointer++;
            // UWAGA NA PRZEKROCZENIE ZAKRESU
            if (pointer == childP.length) break;
        }
        // UZUPELNIAMY JEZELI BRAKUJE COS
        // NAJPIERW OD OJCA
        if (pointer < childP.length)
        {
            for (int i = half; i < fatherP.length; i++)
            {
                if (mat[(int) fatherP[i]])
                {
                    continue;
                }
                childP[pointer] = fatherP[i];
                childC[pointer] = fatherC[i];
                childT[pointer] = fatherT[i];
                mat[(int) childP[pointer]] = true;
                pointer++;
                // UWAGA NA PRZEKROCZENIE ZAKRESU
                if (pointer == childP.length) break;
            }
        }
        // POTEM OD MATKI
        if (pointer < childP.length)
        {
            for (int i = 0; i < halfM; i++)
            {
                if (mat[(int) motherP[i]])
                {
                    continue;
                }
                childP[pointer] = motherP[i];
                childC[pointer] = motherC[i];
                childT[pointer] = motherT[i];
                mat[(int) childP[pointer]] = true;
                pointer++;
                // UWAGA NA PRZEKROCZENIE ZAKRESU
                if (pointer == childP.length) break;
            }
        }
        // POWINNO BYC OK
    }


    @SuppressWarnings("UnusedParameters")
    public ISpecimen cross(ISpecimen parent[], Object problem, MersenneTwister generator, int side)
    {
        // CROSS RM
        int w = 0;
        if (generator.nextDouble() > 0.5d) w = 1;

        ISpecimen f = parent[w];
        ISpecimen m = parent[1 - w];

        IGene childRM_P = new Gene();
        IGene childRM_C = new Gene();
        IGene childRM_T = new Gene();

        IGene childDC_P = new Gene();
        IGene childDC_C = new Gene();
        IGene childDC_T = new Gene();

        // REGIONAL MARKET
        double codeRM_P[] = new double[f.getGene().getGene().get("RM_P").getValues().length];
        double codeRM_C[] = new double[f.getGene().getGene().get("RM_C").getValues().length];
        double codeRM_T[] = new double[f.getGene().getGene().get("RM_T").getValues().length];

        this.standardCross(codeRM_P, codeRM_C, codeRM_T, f.getGene().getGene().get("RM_P").getValues(),
                f.getGene().getGene().get("RM_C").getValues(), f.getGene().getGene().get("RM_T").getValues(),
                m.getGene().getGene().get("RM_P").getValues(), m.getGene().getGene().get("RM_C").getValues(),
                m.getGene().getGene().get("RM_T").getValues());

        childRM_P.setValues(codeRM_P);
        childRM_C.setValues(codeRM_C);
        childRM_T.setValues(codeRM_T);

        // DISTRIBUTION CENTER
        double codeDC_P[] = new double[f.getGene().getGene().get("DC_P").getValues().length];
        double codeDC_C[] = new double[f.getGene().getGene().get("DC_C").getValues().length];
        double codeDC_T[] = new double[f.getGene().getGene().get("DC_T").getValues().length];

        this.standardCross(codeDC_P, codeDC_C, codeDC_T, f.getGene().getGene().get("DC_P").getValues(),
                f.getGene().getGene().get("DC_C").getValues(), f.getGene().getGene().get("DC_T").getValues(),
                m.getGene().getGene().get("DC_P").getValues(), m.getGene().getGene().get("DC_C").getValues(),
                m.getGene().getGene().get("DC_T").getValues());

        childDC_P.setValues(codeDC_P);
        childDC_C.setValues(codeDC_C);
        childDC_T.setValues(codeDC_T);

        // ENTRY POINT
        double codeEP_P[] = f.getGene().getGene().get("EP_P").getValues().clone();
        double codeEP_C[] = f.getGene().getGene().get("EP_C").getValues().clone();

        // CREATE CHILD
        ISpecimen specimen = parent[0].clone();

        specimen.getGene().getGene().get("RM_P").setValues(codeRM_P);
        specimen.getGene().getGene().get("RM_C").setValues(codeRM_C);
        specimen.getGene().getGene().get("RM_T").setValues(codeRM_T);

        specimen.getGene().getGene().get("DC_P").setValues(codeDC_P);
        specimen.getGene().getGene().get("DC_C").setValues(codeDC_C);
        specimen.getGene().getGene().get("DC_T").setValues(codeDC_T);

        specimen.getGene().getGene().get("EP_P").setValues(codeEP_P);
        specimen.getGene().getGene().get("EP_C").setValues(codeEP_C);

        return specimen;
    }

}
