package year.y2014.greenlogistics.exp_visualization;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import base.Specimen;
import criterion.Criterion;
import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import sort.functions.Front;
import standard.Common;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 23.11.2015.
 */
public class SeparateA
{
    public static void main(String args[])
    {
        ArrayList<ICriterion> criteria = Criterion.getCriterionArray("C", 3, false);
        ArrayList<ISpecimen> pareto = new ArrayList<>(200);
        for (double[] aData : data)
        {
            IAlternative a = new Alternative("A", criteria);
            a.setEvaluationVector(aData, criteria);
            ISpecimen s = new Specimen("S", criteria);
            s.setAlternative(a);
            pareto.add(s);
        }

        // COST-PM
        {
            ArrayList<ICriterion> costPM = Criterion.getCriterionArray("C", 2, false);
            ArrayList<ISpecimen> costPMSpec = new ArrayList<>(pareto.size());
            for (ISpecimen s: pareto)
            {
                double e[] = {s.getAlternative().getEvaluationAt(criteria.get(0)),
                        s.getAlternative().getEvaluationAt(criteria.get(2))};
                IAlternative a = new Alternative("A", costPM);
                a.setEvaluationVector(e, costPM);
                ISpecimen dS = new Specimen("S", costPM);
                dS.setAlternative(a);
                costPMSpec.add(dS);
            }
            ArrayList<ISpecimen> p = Front.getPareto(costPMSpec, costPM, Common.EPSILON);
            for (ISpecimen s: p)
            {
                System.out.println(s.getAlternative().getEvaluationAt(costPM.get(0)) + " " +
                        s.getAlternative().getEvaluationAt(costPM.get(1)));
            }
        }
        System.out.println("-----------------------");
        // COST-CO
        {
            ArrayList<ICriterion> costCO2 = Criterion.getCriterionArray("C", 2, false);
            ArrayList<ISpecimen> costPMSpec = new ArrayList<>(pareto.size());
            for (ISpecimen s: pareto)
            {
                double e[] = {s.getAlternative().getEvaluationAt(criteria.get(0)),
                        s.getAlternative().getEvaluationAt(criteria.get(1))};
                IAlternative a = new Alternative("A", costCO2);
                a.setEvaluationVector(e, costCO2);
                ISpecimen dS = new Specimen("S", costCO2);
                dS.setAlternative(a);
                costPMSpec.add(dS);
            }
            ArrayList<ISpecimen> p = Front.getPareto(costPMSpec, costCO2, Common.EPSILON);
            for (ISpecimen s: p)
            {
                System.out.println(s.getAlternative().getEvaluationAt(costCO2.get(0)) + " " +
                        s.getAlternative().getEvaluationAt(costCO2.get(1)));
            }
        }
    }

    public static double data[][] = {
    {	1028586.13	,	570174.21	,	2737.18	}	,
    {	845868.41	,	538848.5	,	13872.21	}	,
    {	850242.31	,	536836.55	,	11655.35	}	,
    {	957075.51	,	570882.3	,	4202.75	}	,
    {	852729.93	,	537981.27	,	10888.87	}	,
    {	1023950.27	,	570699.21	,	2774.02	}	,
    {	997989.66	,	564822.3	,	3740.53	}	,
    {	1015344.89	,	570174.21	,	2870.66	}	,
    {	938927.18	,	553213.93	,	6189.92	}	,
    {	859275.95	,	542807.93	,	9269.03	}	,
    {	1000086.32	,	567866.29	,	3320.25	}	,
    {	943472.44	,	557418.18	,	5530.22	}	,
    {	976594.14	,	567866.29	,	3851.06	}	,
    {	902457.55	,	561762.75	,	6024.02	}	,
    {	911023.76	,	558857.47	,	6087.58	}	,
    {	854699.77	,	541035.19	,	9971	}	,
    {	930679.31	,	564070.67	,	5142.94	}	,
    {	941028.49	,	555110.26	,	5668.64	}	,
    {	852390.16	,	540167.96	,	10093.58	}	,
    {	862960.03	,	537981.27	,	10646.02	}	,
    {	868247.69	,	549196.19	,	8052.56	}	,
    {	921838.59	,	550956.75	,	6754.64	}	,
    {	910936.74	,	561762.75	,	5727.09	}	,
    {	930514.64	,	555110.26	,	5856.38	}	,
    {	866525.29	,	537981.27	,	10587.72	}	,
    {	969806.07	,	564595.67	,	4200.5	}	,
    {	863861.44	,	545816.72	,	8873.03	}	,
    {	872008.05	,	552204.98	,	7579.76	}	,
    {	875734.06	,	544568.48	,	8781.7	}	,
    {	876370.92	,	540167.96	,	9627.82	}	,
    {	1011955.72	,	570699.21	,	2962.5	}	,
    {	874415.15	,	546039.52	,	8409.48	}	,
    {	856654.58	,	537981.27	,	10828.1	}	,
    {	858621.38	,	540167.96	,	10033.68	}	,
    {	871997.78	,	541663.21	,	9156.22	}	,
    {	1000472.01	,	570174.21	,	3221.44	}	,
    {	951995.14	,	558753.96	,	5110.64	}	,
    {	902124.14	,	544672	,	8249.56	}	,
    {	1004439.72	,	570699.21	,	3143.26	}	,
    {	885806.44	,	552101.47	,	7279.3	}	,
    {	1007627.35	,	567866.29	,	3146.97	}	,
    {	863074.12	,	547725.15	,	8571.89	}	,
    {	906435.8	,	551060.26	,	6983.55	}	,
    {	897153.87	,	546683.94	,	7940.98	}	,
    {	881100.37	,	557943.17	,	7009.42	}	,
    {	889310.9	,	543675.15	,	8500.66	}	,
    {	877082.12	,	548051.47	,	8015.97	}	,
    {	955694.6	,	561762.75	,	4673.94	}	,
    {	884721.31	,	549048.31	,	7866.63	}	,
    {	898336.1	,	543675.15	,	8325.65	}	,
    {	963737.8	,	564778.76	,	4384.12	}	,
    {	1012648.47	,	570174.21	,	3063.36	}	,
    {	996115.25	,	570174.21	,	3349.95	}	,
    {	967023.05	,	570174.21	,	4018.85	}	,
    {	972641.76	,	570174.21	,	3906.72	}	,
    {	988859.94	,	564595.67	,	3830.2	}	,
    {	985812.1	,	570882.3	,	3539.62	}	,
    {	884311.89	,	541663.21	,	8965.69	}	,
    {	993794.74	,	570882.3	,	3362.24	}	,
    {	892269.97	,	558857.47	,	6622.46	}	,
    {	900346.11	,	546580.43	,	7863.62	}	,
    {	983448.17	,	564070.67	,	3968.5	}	,
    {	961324.18	,	557712.75	,	5170.32	}	,
    {	926097.56	,	557418.18	,	5888.36	}	,
    {	913073.39	,	546039.52	,	7666.63	}	,
    {	949350.32	,	564070.67	,	4774.78	}	,
    {	887826.22	,	546580.43	,	8155.78	}	,
    {	899176.76	,	548451.24	,	7461.68	}	,
    {	866454.14	,	544672	,	8957.53	}	,
    {	1006898.98	,	570699.21	,	3042.4	}	,
    {	992409.19	,	564201.11	,	3934.07	}	,
    {	867205.66	,	540167.96	,	9840.27	}	,
    {	889849.88	,	541663.21	,	8885.82	}	,
    {	915699.99	,	551060.26	,	6846.08	}	,
    {	879498.89	,	552101.47	,	7427.14	}	,
    {	936074.27	,	558753.96	,	5460.14	}	,
    {	863776.37	,	541663.21	,	9387.32	}	,
    {	910483.66	,	546039.52	,	7709.96	}	,
    {	979434.65	,	570174.21	,	3747.71	}	,
    {	989498.08	,	570882.3	,	3457.09	}	,
    {	909568.09	,	552101.47	,	6710.14	}	,
    {	973395.3	,	567866.29	,	3974.55	}	,
    {	869943.33	,	544568.48	,	8848.07	}	,
    {	933079.37	,	555110.26	,	5836.1	}	,
    {	1005262.08	,	567866.29	,	3197.59	}	,
    {	976174.28	,	570174.21	,	3834.59	}	,
    {	935205.64	,	567866.29	,	4855.1	}	,
    {	947831.52	,	555110.26	,	5492.48	}	,
    {	991558.29	,	567866.29	,	3533.69	}	,
    {	867883.46	,	546039.52	,	8603.02	}	,
    {	977634.98	,	570699.21	,	3714.02	}	,
    {	965746.23	,	567866.29	,	4082.26	}	,
    {	981591.42	,	567866.29	,	3761.18	}	,
    {	871888.62	,	540167.96	,	9691.82	}	,
    {	918073.78	,	570174.21	,	5218.62	}	,
    {	901877.97	,	550956.75	,	7153.49	}	,
    {	945173.08	,	570174.21	,	4587.58	}	,
    {	871114.48	,	551060.26	,	7807.04	}	,
    {	953358.19	,	570174.21	,	4462.4	}	,
    {	923846.94	,	570174.21	,	5097.73	}	,
    {	985333.17	,	567866.29	,	3637.94	}	,
    {	948360.1	,	570882.3	,	4439.94	}	,
    {	959097.42	,	561762.75	,	4581.42	}	,
    {	960891.23	,	558753.96	,	4959.86	}	,
    {	907432.42	,	564595.67	,	5763.67	}	,
    {	956719.15	,	567866.29	,	4307.29	}	,
    {	886491.91	,	552101.47	,	7182.5	}	,
    {	880285.39	,	548051.47	,	7932.38	}	,
    {	927614.8	,	561762.75	,	5315.41	}	,
    {	911542.09	,	570174.21	,	5412.16	}	,
    {	919236.53	,	548451.24	,	7010.7	}	,
    {	904806.55	,	558753.96	,	6266.42	}	,
    {	980891.67	,	564778.76	,	4025.98	}	,
    {	857821.38	,	541663.21	,	9487.67	}	,
    {	930429.72	,	558753.96	,	5607.98	}	,
    {	903830.03	,	561762.75	,	5939.06	}	,
    {	901208.31	,	558753.96	,	6333.42	}	,
    {	879740.19	,	540167.96	,	9541.68	}	,
    {	985179.33	,	564778.76	,	3936.79	}	,
    {	863301.13	,	540167.96	,	9931.63	}	,
    {	924642.4	,	558753.96	,	5721.07	}	,
    {	928461.44	,	552101.47	,	6346.72	}	,
    {	907098.05	,	555110.26	,	6436.03	}	,
    {	880545.73	,	541663.21	,	9113.75	}	,
    {	883393	,	555110.26	,	6940.16	}	,
    {	896414.32	,	558753.96	,	6452.91	}	,
    {	895562.53	,	555110.26	,	6712.38	}	,
    {	977459.37	,	561762.75	,	4214.32	}	,
    {	932243.4	,	551060.26	,	6480.96	}	,
    {	923918.54	,	564595.67	,	5360.09	}	,
    {	882750.24	,	551060.26	,	7535.23	}	,
    {	940256.53	,	558753.96	,	5398.64	}	,
    {	866992.79	,	541663.21	,	9250.49	}	,
    {	958846	,	564595.67	,	4486.62	}	,
    {	905822.76	,	552101.47	,	6784.35	}	,
    {	899710.02	,	555110.26	,	6555.23	}	,
    {	915053.09	,	567866.29	,	5457.21	}	,
    {	957375.2	,	564595.67	,	4587.74	}	,
    {	992346.71	,	570174.21	,	3395.39	}	,
    {	862995.31	,	544568.48	,	9018.47	}	,
    {	952710.39	,	557609.24	,	5309.18	}	,
    {	894728.05	,	551060.26	,	7278.4	}	,
    {	889178.35	,	548451.24	,	7701.74	}	,
    {	870495.73	,	546039.52	,	8552.39	}	,
    {	893498.56	,	550956.75	,	7311.25	}	,
    {	910315.41	,	544672	,	8106.46	}	,
    {	910539.46	,	548051.47	,	7373.02	}	,
    {	882796.84	,	540167.96	,	9487.63	}	,
    {	922925.19	,	548451.24	,	6949.62	}	,
    {	913833.03	,	561762.75	,	5629.87	}	,
    {	910142.42	,	555110.26	,	6349.12	}	,
    {	952167.73	,	561762.75	,	4763.28	}	,
    {	944635.68	,	567866.29	,	4633.02	}	,
    {	913787.66	,	558753.96	,	5966.54	}	,
    {	918800.88	,	552101.47	,	6546.62	}	,
    {	889071.54	,	551060.26	,	7444	}	,
    {	964537.62	,	561762.75	,	4499.41	}	,
    {	920822.42	,	561762.75	,	5501.68	}	,
    {	889737.15	,	558857.47	,	6652.42	}	,
    {	867388.25	,	548051.47	,	8218.02	}	,
    {	892769.03	,	546039.52	,	8043.66	}	,
    {	944172.35	,	558753.96	,	5266.8	}	,
    {	989456.18	,	570174.21	,	3499.36	}	,
    {	985734.02	,	561762.75	,	4121.87	}	,
    {	871638.97	,	548051.47	,	8172.13	}	,
    {	902333.48	,	552101.47	,	6880.61	}	,
    {	931917.64	,	557712.75	,	5850.83	}	,
    {	919915.3	,	557418.18	,	6035.91	}	,
    {	897697.9	,	561762.75	,	6079.02	}	,
    {	880095.89	,	546039.52	,	8332.17	}	,
    {	944412.54	,	564070.67	,	4956.25	}	,
    {	894322.61	,	561762.75	,	6176.24	}	,
    {	876506.97	,	555110.26	,	7212.96	}	,
    {	896914.68	,	564070.67	,	6022.62	}	,
    {	923503.32	,	558857.47	,	5782.24	}	,
    {	928179.22	,	567866.29	,	5051	}	,
    {	904041.03	,	567866.29	,	5660.06	}	,
    {	953184.04	,	570699.21	,	4347.78	}	,
    {	939339.51	,	567866.29	,	4764.73	}	,
    {	907427.42	,	570699.21	,	5484.67	}	,
    {	949341.64	,	567866.29	,	4523.16	}	,
    {	915484.21	,	557418.18	,	6133.29	}	,
    {	964436.9	,	557712.75	,	5120.91	}	,
    {	932660	,	561762.75	,	5199.82	}	,
    {	876640.69	,	552101.47	,	7521.6	}	,
    {	880804.75	,	544568.48	,	8597.77	}	,
    {	973343.47	,	561762.75	,	4321.87	}	,
    {	957309.5	,	558753.96	,	5007.02	}	,
    {	893624.2	,	555110.26	,	6797.79	}	,
    {	976786.19	,	564595.67	,	4090.17	}	,
    {	930560	,	570174.21	,	4913.44	}	,
    {	915557.87	,	555110.26	,	6239.1	}	,
    {	918352.31	,	555110.26	,	6190.18	}	,
    {	964382.42	,	558857.47	,	4857.76	}	,
    {	935388.35	,	557418.18	,	5721.32	}	,
    {	897425.88	,	541663.21	,	8782.14	}	,
    {	940872.06	,	561762.75	,	5032.69	}	,
    {	920573.67	,	567866.29	,	5211.96	}	,
    {	949056.51	,	561762.75	,	4867.92	}	,
    {	981276	,	564070.67	,	4113.46	}	,
    {	897449.76	,	548451.24	,	7470.32	}	,
    {	893946.56	,	552101.47	,	7008	}	,
    {	918238.39	,	561762.75	,	5580.75	}	,
    {	919569.99	,	567866.29	,	5275.03	}	,
    {	898400.86	,	552101.47	,	6945.5	}	,
    {	932028.64	,	570699.21	,	4870.34	}	,
    {	924320.07	,	570699.21	,	5036.58	}	,
    {	915362.91	,	570174.21	,	5307.97	}	,
    {	911104.02	,	567866.29	,	5584.66	}	,
    {	935684.3	,	564070.67	,	5048.66	}	,
    {	930613.33	,	552101.47	,	6265.41	}	,
    {	880178.64	,	550956.75	,	7562.26	}	,
    {	919677.04	,	558753.96	,	5867.95	}	,
    {	923319.62	,	555110.26	,	6026.78	}	,
    {	876587.9	,	550956.75	,	7702.93	}	,
};
}
