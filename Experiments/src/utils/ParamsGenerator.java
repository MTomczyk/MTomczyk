package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by MTomczyk on 23.08.2015.
 */
public class ParamsGenerator
{
    public static ArrayList<ArrayList<Object>> generateParams(ArrayList<String> paramsNames, HashMap<String, ArrayList<Object>> params)
    {
        LinkedList<ArrayList<Object>> tmpResult = new LinkedList<>();

        int cube[] = new int[paramsNames.size()];
        int max[] = new int[paramsNames.size()];

        for (int i = 0; i < max.length; i++)
        {
            String k = paramsNames.get(i);
            max[i] = params.get(k).size();
        }

        boolean run = true;
        while (run)
        {
            // BUILD
            ArrayList<Object> p = new ArrayList<>();
            for (int i = 0; i < max.length; i++)
            {
                String k = paramsNames.get(i);
                p.add(params.get(k).get(cube[i]));
            }
            tmpResult.add(p);

            // UPDATE
            cube[0]++;
            for (int i = 0; i < max.length; i++)
            {
                if (cube[i] == max[i])
                {
                    cube[i] = 0;
                    if (i == max.length - 1) run = false;
                    else
                    {
                        cube[i] = 0;
                        cube[i + 1]++;
                        continue;
                    }
                }
                break;
            }
        }

        ArrayList<ArrayList<Object>> result = new ArrayList<>(tmpResult.size());
        result.addAll(tmpResult.stream().collect(Collectors.toList()));

        return result;
    }
}
