package imageProcessing;

import java.util.ArrayList;
import java.util.List;

public class MergeSort {
    public static List<Individual> Sort(List<Individual> list) {
        if (list.size() <= 1) {
            return list;
        }
        List<Individual> aList = new ArrayList<Individual>();
        aList = list.subList(0, list.size() / 2);

        List<Individual> bList = new ArrayList<Individual>();
        bList = list.subList(list.size() / 2, list.size());

        Sort(aList);
        Sort(bList);

        merge(aList, bList, list);
        return list;
    }

    private static List<Individual> merge(List<Individual> alist,
        List<Individual> blist, List<Individual> list) {
        int alistIndex = 0, blistIndex = 0, listIndex = 0;
        while (alistIndex < alist.size() && blistIndex < blist.size()) {
            if (alist.get(alistIndex).getFitness() < blist.get(blistIndex).getFitness()) {
                list.set(listIndex, alist.get(alistIndex));
                alistIndex++;
            } else {
                list.set(listIndex, blist.get(blistIndex));
                blistIndex++;
            }
            listIndex++;
        }
        List<Individual> rest;
        if (alistIndex == alist.size()) {
            rest = blist.subList(blistIndex, blist.size());
            for(int c = blistIndex; c < rest.size(); c++){
                list.set(listIndex, blist.get(c));
                listIndex++;
            }
        } else {
            rest = alist.subList(alistIndex, alist.size());
            for(int c = alistIndex; c < rest.size(); c++){
                list.set(listIndex, alist.get(c));
                listIndex++;
            }
        }
        return list;
    }
}