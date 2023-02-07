package com.mwlib.ptrace;

import ru.ts.utils.data.Pair;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 29.12.13
 * Time: 18:17
 *
 */
public class Param implements Cloneable
{

    public final static int POTRACE_TURNPOLICY_BLACK=0;
    public final static int POTRACE_TURNPOLICY_WHITE=1;
    public final static int POTRACE_TURNPOLICY_LEFT=2;
    public final static int POTRACE_TURNPOLICY_RIGHT=3;
    public final static int POTRACE_TURNPOLICY_MINORITY=4;
    public final static int POTRACE_TURNPOLICY_MAJORITY=5;
    public final static int POTRACE_TURNPOLICY_RANDOM=6;


    public int turnPolicy = POTRACE_TURNPOLICY_MINORITY;/*resolves ambiguous turns in path decomposition */
    public int turdsize = 2;            /* area of largest path to be ignored */
    public double alphamax = 1;      /* corner threshold */
    public int opticurve = 1;       /* use curve optimization? */
    public double opttolerance = 0.2; /* curve optimization tolerance */

    public boolean bPrint=false; //Показывает что библиотека должна вывести результат трассировки

    public Param(){}

}
